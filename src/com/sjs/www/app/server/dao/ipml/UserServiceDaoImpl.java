package com.sjs.www.app.server.dao.ipml;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sjs.www.app.server.dao.UserServiceDao;
import com.sjs.www.app.server.model.Code;
import com.sjs.www.app.server.model.WebTransException;
import com.sjs.www.app.server.model.bean.User;
import com.sjs.www.app.server.model.http.LoginResponse;
import com.sjs.www.app.server.servlet.EntryServiceServlet;
import com.sjs.www.app.server.servlet.EntryServiceServlet.HttpRequestContext;
import com.sjs.www.app.server.util.CookieManager;
import com.sjs.www.app.server.util.DESEncrypt;

public class UserServiceDaoImpl implements UserServiceDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceDaoImpl.class);
	
	public static final String Cookie_Key_UserName = "autoUserName";
	public static final String Cookie_Key_PwdKey = "autoPwKey";
	public static final String Cookie_DESede_Key = "ThisIsAppKey@_2015112915";

	@Override
	public User currentUser() throws WebTransException {
		HttpSession httpSession = HttpRequestContext.getHttpSession();
		String id = httpSession.getId();
		Object attribute = HttpRequestContext.getAttribute(EntryServiceServlet.USER_SESSION);
		return (User) attribute;
	}

	@Override
	public LoginResponse login(User user) throws WebTransException {
		if(user != null) {
			HttpRequestContext.setAttribute(EntryServiceServlet.USER_SESSION, user);
			
			//去服务核心系统验证登录和获取登录返还的数据
			HttpSession httpSession = HttpRequestContext.getHttpSession();
			String id = httpSession.getId();
			
			Object attribute = HttpRequestContext.getAttribute(EntryServiceServlet.USER_SESSION);
			
			// 设置Cookie,保持8小时内自动登录
			try {
				Cookie ckUserName = new Cookie(Cookie_Key_UserName, new String(Base64.encodeBase64(user.getUsername().getBytes()))); //用户名
				ckUserName.setMaxAge(60 * 60 * 8);//设置 Cookie 有效期为8小时
				
				DESEncrypt desEncrypt = new DESEncrypt(Cookie_DESede_Key);
				String encString = desEncrypt.getEncString(user.getPwd());
				
				Cookie ckPwkey = new Cookie(Cookie_Key_PwdKey, new String(Base64.encodeBase64(encString.getBytes()))); //自动登录密钥
				ckPwkey.setMaxAge(60 * 60 * 8); //设置 Cookie 有效期为8小时
				
				HttpRequestContext.get().getResponse().addCookie(ckUserName);
				HttpRequestContext.get().getResponse().addCookie(ckPwkey);
			} catch(Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			
			//test代码
			LoginResponse response = new LoginResponse();
			response.setEmail("23242@163.com");
			response.setHasBindBankCard(true);
			response.setHasBindIdentifData(false);
			response.setHasRealName(true);
			response.setIdcardNo("3243545325325235");
			response.setMobile("130000000000");
			response.setNickName("daasf");
			response.setUserIconUrl("www.hdhhdh.com/jidasd.jpg");
			response.setUserId(10000);
			response.setVipGrade("5");
			
			return response;
		}
		
		throw new WebTransException(Code.用户名密码错误);
	}

	@Override
	public void logout() throws WebTransException {
		HttpRequestContext.getHttpSession().removeAttribute(EntryServiceServlet.USER_SESSION);
		
		// 清除cookie信息
		CookieManager cm = new CookieManager();
		Cookie[] cookies = HttpRequestContext.get().getRequest().getCookies();

		Cookie ckUserName = cm.getCookieValue(cookies, Cookie_Key_UserName);
		Cookie ckPwkey = cm.getCookieValue(cookies, Cookie_Key_PwdKey);

		if (ckUserName != null) {
			ckUserName.setMaxAge(0);
			
			HttpRequestContext.get().getResponse().addCookie(ckUserName);
		}
		
		if(ckPwkey != null) {
			ckPwkey.setMaxAge(0);
			
			HttpRequestContext.get().getResponse().addCookie(ckPwkey);
		}
	}

	@Override
	public void modifyPwd(String hex_md5, String hex_md52)
			throws WebTransException {
		
	}

}
