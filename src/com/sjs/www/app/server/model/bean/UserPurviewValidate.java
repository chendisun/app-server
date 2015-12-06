package com.sjs.www.app.server.model.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UserPurviewValidate {

	/**
	 * 标示符
	 */
	String name() default "";
	
	/**
	 * 是否校验
	 */
	boolean validate() default true;

}
