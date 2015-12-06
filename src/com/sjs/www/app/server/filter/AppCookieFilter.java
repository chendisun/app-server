/**
 * 
 */
package com.sjs.www.app.server.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;

import com.sjs.www.app.server.dao.UserServiceDao;
import com.sjs.www.app.server.dao.ipml.UserServiceDaoImpl;
import com.sjs.www.app.server.model.WebTransException;
import com.sjs.www.app.server.model.bean.HasUserName;
import com.sjs.www.app.server.model.bean.User;
import com.sjs.www.app.server.model.http.LoginResponse;
import com.sjs.www.app.server.servlet.EntryServiceServlet;
import com.sjs.www.app.server.servlet.EntryServiceServlet.HttpRequestContext;
import com.sjs.www.app.server.servlet.EntryServiceServlet.Parms;
import com.sjs.www.app.server.util.CookieManager;
import com.sjs.www.app.server.util.DESEncrypt;

/**
 * app自动登录
 *
 */
public class AppCookieFilter implements Filter {
	protected WebApplicationContext springContext;
	protected static final Logger LOGGER = Logger.getLogger(AppCookieFilter.class);
	private FilterConfig config; 
	private final String LAST_LOGIN_TIME = "_LastLoginTime";
	private final int MAX_LOGIN_TIME = 5 * 1000;
	private static final List<String> filters = new ArrayList<String>();
	
	private final Map<String, Object> loginCache = new ConcurrentHashMap<String, Object>();
	
	static {
		filters.add("APP0001");//不用过滤登录
		filters.add("login");//不用过滤登录
	}
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
		
		springContext = (WebApplicationContext) config
				.getServletContext()
				.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		LOGGER.info("ServiceServlet初始化完成");
		if (springContext == null)
			throw new ServletException("Check your web.xml setting, no Spring context configured");
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		ThreadLocal<HttpRequestContext> threadLocal = HttpRequestContext.ThreadLocalHttpRequestContext;
			
		try {
			threadLocal.set(new HttpRequestContext((HttpServletRequest)req, (HttpServletResponse)resp, null, config.getServletContext()));

			doCheckCookie();
		} catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			threadLocal.remove();
		}
		
		chain.doFilter(req, resp);
	}
	
	protected void doCheckCookie() {
		String jkid = HttpRequestContext.get().getRequest().getParameter(Parms.jkid.name());
		if(filters.contains(jkid))// 需要过滤的接口
			return;
		
		HasUserName userSession = (HasUserName) HttpRequestContext.getAttribute(EntryServiceServlet.USER_SESSION);

		if (userSession == null) {// 无会话
			String key = HttpRequestContext.getHttpSession().getId();
			
			if(loginCache.containsKey(key)) // 已经在进行登录中
				return;
			
			try {
				loginCache.put(key, key);
				
				Long x = (Long) HttpRequestContext.getAttribute(LAST_LOGIN_TIME);
				
				if(x != null && (System.currentTimeMillis() - x) <= MAX_LOGIN_TIME) {// 小于5秒的不检查,避免过于频繁
					return;
				}
				
				doAction();
				
				HttpRequestContext.setAttribute(LAST_LOGIN_TIME, System.currentTimeMillis());
			} catch(Exception e) {
				LOGGER.error(e.getMessage(), e);
			} finally {
				loginCache.remove(key);
			}
		}
	}

	protected void doAction() {
		// 修改为用Cookie来进行管理，不再进行localUserInfo进行管理，避免太多的数据库查询
		CookieManager cm = new CookieManager();
		Cookie[] cookies = HttpRequestContext.get().getRequest().getCookies();

		Cookie ckUserName = cm.getCookieValue(cookies, UserServiceDaoImpl.Cookie_Key_UserName);
		Cookie ckPwkey = cm.getCookieValue(cookies, UserServiceDaoImpl.Cookie_Key_PwdKey);

		if (ckUserName != null && ckPwkey != null) {// 有cookie信息
			String username = new String(Base64.decodeBase64(ckUserName.getValue().getBytes()));
			String pwkey = new String(Base64.decodeBase64(ckPwkey.getValue().getBytes()));

			String pwd = null;
			try {
				DESEncrypt desEncrypt = new DESEncrypt(UserServiceDaoImpl.Cookie_DESede_Key);
				
				pwd = desEncrypt.getDesString(pwkey);
			} catch (Exception e1) {
				LOGGER.error(e1.getMessage(), e1);
			}
			
			LoginResponse user = null;
			
			if(pwd != null) {
				try {
					user = ((UserServiceDao)springContext.getBean("app.user")).login(new User(username, pwd));
					
					LOGGER.info("CookieFilter 通过Cookie进行登录校验:{} {}", username, user != null);
				} catch(WebTransException e) {
					LOGGER.info("CookieFilter 通过Cookie进行登录校验:{} {}", username, user != null);
				} catch(Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			
			if(user == null) {// 登录失败,清除cookie信息
				ckUserName.setMaxAge(0);
				HttpRequestContext.get().getResponse().addCookie(ckUserName);
				
				ckPwkey.setMaxAge(0);
				HttpRequestContext.get().getResponse().addCookie(ckPwkey);
			}
		}
	}

	@Override
	public void destroy() {
		
	}
}
