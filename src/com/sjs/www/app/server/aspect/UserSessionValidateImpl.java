/**
 * 
 */
package com.sjs.www.app.server.aspect;

import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.sjs.www.app.server.model.Code;
import com.sjs.www.app.server.model.WebTransException;
import com.sjs.www.app.server.model.bean.HasUserName;
import com.sjs.www.app.server.servlet.EntryServiceServlet;
import com.sjs.www.app.server.servlet.EntryServiceServlet.HttpRequestContext;

/**
 * 检验用户是否登录
 */
@Aspect
// 声明切面
public class UserSessionValidateImpl {

	// 声明切入点
	@Pointcut("execution(@com.sjs.www.app.server.model.bean.UserSessionValidate public * * (..))")
	public void aroundPointCut() {
	}

	// 拦截对目标对象方法调用
	@Around("com.sjs.www.app.server.aspect.UserSessionValidateImpl.aroundPointCut()")
	public Object doLoggerPointCut(ProceedingJoinPoint jp) throws Throwable {
		HttpSession httpSession = HttpRequestContext.getHttpSession();
		String id = httpSession.getId();
		HasUserName userSession = (HasUserName) HttpRequestContext.getAttribute(EntryServiceServlet.USER_SESSION);

		if (userSession == null) {// 无会话
			throw new WebTransException(Code.会话过期_或者未登录);
		}

		return jp.proceed();
	}
	
}
