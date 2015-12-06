package com.sjs.www.app.server.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.sjs.www.app.server.model.Code;
import com.sjs.www.app.server.model.WebTransException;
import com.sjs.www.app.server.util.GsonUtils;
import com.sjs.www.app.server.util.MD5;
import com.sjs.www.app.server.util.StringUtils;

/**
 * 所有接口公共入口
 *
 */
public class EntryServiceServlet extends HttpServlet{

	private static final long serialVersionUID = -8680696395886848388L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EntryServiceServlet.class);
	
	public static final String USER_SESSION = "UserSession";
	public static final String VALIDATE_CODE = "ValidateCode";
	public static final String AUTH_INFO = "AuthInfo";
	
	public enum Parms{
		sqbh, //授权编号
		jkid, //接口编号
		jkqm, //接口签名
		time, //时间戳
		requestData, //请求信息
		responseData, //返回信息
		responseCode,  //返回码
		responseMsg; //错误信息
	}

	private static final String s = "-----------------------------------------------------------------------";

	private WebApplicationContext appctx = null;//定义全局变量context
	
	private final Map<String, String> keys = new HashMap<String, String>();
	private final Map<String, IService> services = new HashMap<String, IService>();
	
	@SuppressWarnings("unchecked")
	@Override
	public void init() throws ServletException {
		super.init();
		
		LOGGER.info("载 spring beans");
		  
        appctx = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        keys.putAll((Map<String, String>)appctx.getBean("keys"));
        services.putAll((Map<String, IService>)appctx.getBean("services"));
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		long startTime = System.currentTimeMillis();
		
		req.setCharacterEncoding("utf-8");
		
		resp.setContentType("text/plain;charset=utf-8");
		resp.setCharacterEncoding("utf-8");
		resp.setHeader("Accept-Encoding", "gzip, deflate");
		
		ServiceRequest request = new ServiceRequest();
		request.setSqbh(req.getParameter(Parms.sqbh.name()));
		request.setJkid(req.getParameter(Parms.jkid.name()));
		request.setJkqm(req.getParameter(Parms.jkqm.name()));
		request.setTime(req.getParameter(Parms.time.name()));
		request.setRequestData(req.getParameter(Parms.requestData.name()));
		
		final String clientIPAddress = getClientIPAddress(req);
		final String sessionId = req.getSession().getId();
		
		logIn(req, startTime, request, clientIPAddress, sessionId);
		
		ThreadLocal<HttpRequestContext> threadLocal = HttpRequestContext.ThreadLocalHttpRequestContext;
		try {
			threadLocal.set(new HttpRequestContext(req, resp, getServletConfig(), getServletContext()));
			checkRequest(request);
		} catch (WebTransException e) {
			writeResp(req, resp, e);
			
			logOut(req, startTime, e, clientIPAddress, sessionId);
			
			return;
		}
		
		ServiceResponse response = null;

		try {
			response = services.get(req.getParameter(Parms.jkid.name())).doService(request);
		} catch (Throwable err) {
			LOGGER.error(err.getMessage(), err);
			
			WebTransException e = new WebTransException(Code.未知异常);
			
			writeResp(req, resp, e);
			
			logOut(req, startTime, e, clientIPAddress, sessionId);
			
			return;
		} finally {
			threadLocal.remove();
		}
		
		writeResp(resp, response);
		
		logOut(req, startTime, response, clientIPAddress, sessionId);
	}

	private void checkRequest(ServiceRequest request) throws WebTransException{
		
		String sqkey = keys.get(request.getSqbh());
		if(sqkey == null) {
			throw new WebTransException(Code.授权编号错误);
		}
		
		long clientTime = 0;
		try{
			 clientTime = Long.parseLong(request.getTime());
		} catch (NumberFormatException e){
			throw new WebTransException(Code.时间差不在合理范围);
		}
		
		if(Math.abs(Calendar.getInstance().getTimeInMillis() - clientTime ) > 2 * 60 * 1000){
			throw new WebTransException(Code.时间差不在合理范围);
		}
		
		String _jkqm = MD5.md5toUpperCase(request.getJkid() + "&" + request.getTime() + "&" + (StringUtils.isEmptyByTrim(request.getRequestData()) ? "" : request.getRequestData()) + "&" + sqkey);

		if(!_jkqm.equalsIgnoreCase(request.getJkqm())) {
			throw new WebTransException(Code.接口签名错误);
		}
		
		if(!services.containsKey(request.getJkid())){
			throw new WebTransException(Code.接口ID错误);
		}
		
	}
	
	private void writeResp(HttpServletResponse resp, ServiceResponse response) throws UnsupportedEncodingException, IOException{
		Map<String, String> map = new HashMap<String, String>();
		map.put(Parms.sqbh.name(), response.getSqbh());
		map.put(Parms.jkid.name(), response.getJkid());
		map.put(Parms.time.name(), response.getTime());
		map.put(Parms.responseCode.name(), response.getResponseCode());
		map.put(Parms.responseMsg.name(), response.getResponseMsg());
		map.put(Parms.responseData.name(), response.getResponseData());
		
		map.put(Parms.jkqm.name(), getJkqmValue(map));
		
		resp.getOutputStream().write(GsonUtils.bean2json(map).getBytes(resp.getCharacterEncoding()));
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
	private void writeResp(HttpServletRequest req, HttpServletResponse resp, WebTransException e) throws UnsupportedEncodingException, IOException{
		Map<String, String> map = new HashMap<String, String>();
		map.put(Parms.sqbh.name(), req.getParameter(Parms.sqbh.name()));
		map.put(Parms.jkid.name(), req.getParameter(Parms.jkid.name()));
		map.put(Parms.time.name(), System.currentTimeMillis() + "");
		map.put(Parms.responseCode.name(), e.getResponseCode());
		map.put(Parms.responseMsg.name(), e.getResponseMsg());
		
		map.put(Parms.jkqm.name(), getJkqmValue(map));
		
		resp.getOutputStream().write(GsonUtils.bean2json(map).getBytes(resp.getCharacterEncoding()));
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
	private String getJkqmValue(Map<String, String> map){
		//md5(接口编号&时间戳&请求信息&返回码&错误信息&授权密钥)
		StringBuilder sb = new StringBuilder();
		sb.append(map.get(Parms.jkid.name()) == null ? map.get(Parms.jkid.name()) : "").append("&")
		.append(map.get(Parms.time.name())).append("&")
		.append(map.containsKey(Parms.responseData.name()) ? map.get(Parms.responseData.name()) : "").append("&")
		.append(map.get(Parms.responseCode.name())).append("&")
		.append(map.containsKey(Parms.responseMsg.name()) ? map.get(Parms.responseMsg.name()) : "").append("&")
		.append(keys.get(map.get(Parms.sqbh.name())));

		return MD5.md5toUpperCase(sb.toString());
	}
	
	protected void logIn(HttpServletRequest req, long startTime,
			ServiceRequest request, final String clientIPAddress,
			final String sessionId) {
		StringBuffer requestLog = new StringBuffer("\n[" + sessionId +"]请求包：")
			.append("\n").append(s)
			.append("\n 请求地址：").append(clientIPAddress)
			.append("\n 会话编号：").append(sessionId)
			.append("\n 服务接口：").append(req.getServletPath())
			.append("\n 请求时间：").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S").format(new Date(startTime)))
			.append("\n 接口时间：").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S").format(new Date(Long.valueOf(request.getTime()))))
			.append("\n 浏览器名：").append(req.getHeader("User-Agent"))
			.append("\n").append(s)
			;
		LOGGER.info(requestLog.toString());
		
		LOGGER.info("req:" + clientIPAddress + " " + request.toString());
	}
	
	protected void logOut(HttpServletRequest req, long startTime,
			Object response, final String clientIPAddress,
			final String sessionId) {
		long ss = System.currentTimeMillis() - startTime;
		
		if (response instanceof WebTransException) {
			WebTransException err = (WebTransException) response;
			
			LOGGER.info("resp:" + clientIPAddress + " " + err.getResponseCode() + " " + err.getResponseMsg() + " " + ss + "ms");
		} else {
			LOGGER.info("resp:" + clientIPAddress + " " + response.toString() + " " + ss + "ms");
		}
		
		StringBuffer responseLog = new StringBuffer("\n[" + sessionId +"]返回包：")
			.append("\n").append(s)
			.append("\n 请求地址：").append(clientIPAddress)
			.append("\n 会话编号：").append(sessionId)
			.append("\n 服务耗时：").append(ss + "毫秒")
			.append("\n 返回时间：").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S").format(new Date()))
			.append("\n").append(s)
			;
		LOGGER.info(responseLog.toString());
	}
	
	static String getClientIPAddress(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP"); 
		if(StringUtils.isEmptyByTrim(ip) || "unknown".equalsIgnoreCase(ip)) { 
			ip = request.getHeader("x-forwarded-for"); 
		} 
	    if(StringUtils.isEmptyByTrim(ip) || "unknown".equalsIgnoreCase(ip)) { 
	        ip = request.getHeader("Proxy-Client-IP"); 
	    } 
	    if(StringUtils.isEmptyByTrim(ip) || "unknown".equalsIgnoreCase(ip)) { 
	        ip = request.getHeader("WL-Proxy-Client-IP"); 
	    } 
	    if(StringUtils.isEmptyByTrim(ip) || "unknown".equalsIgnoreCase(ip)) { 
	        ip = request.getRemoteAddr(); 
	    } 
	    return ip;  
	}
	public static class HttpRequestContext {
		private HttpServletRequest request;
		private HttpServletResponse response;
		private ServletConfig servletConfig;
		private ServletContext servletContext;

		public static ThreadLocal<HttpRequestContext> ThreadLocalHttpRequestContext = new ThreadLocal<HttpRequestContext>();

		public HttpRequestContext(HttpServletRequest request,
				HttpServletResponse response, ServletConfig servletConfig, ServletContext context) {
			this.request = request;
			this.response = response;
			this.servletConfig = servletConfig;
			this.servletContext = context;
		}

		public HttpServletRequest getRequest() {
			return request;
		}

		public HttpServletResponse getResponse() {
			return response;
		}

		public ServletConfig getServletConfig() {
			return servletConfig;
		}
		
		public ServletContext getServletContext(){
			return servletContext;
		}

		public static HttpSession getHttpSession() {
			if(HttpRequestContext.ThreadLocalHttpRequestContext.get() != null){
				return HttpRequestContext.ThreadLocalHttpRequestContext.get().getRequest().getSession();
			}
			return null;
		}
		
		public static HttpRequestContext get() {
			return HttpRequestContext.ThreadLocalHttpRequestContext.get();
		}

		public static void setAttribute(String attri, Object object) {
			getHttpSession().setAttribute(attri, object);
		}

		public static Object getAttribute(String attri) {
			HttpSession httpSession = getHttpSession();
			
			return httpSession == null ? null : httpSession.getAttribute(attri);
		}
		
//		public static Object getAttributeValidate(String attri) throws WebTransException {
//			Object obj = getAttribute(attri);
//			if(attri.equals(GWTRemoteServiceServlet.USER_SESSION) && obj == null)
//				throw new WebTransException(OssTransCode.USER_未登录);
//			return obj;
//		}
	}	

}
