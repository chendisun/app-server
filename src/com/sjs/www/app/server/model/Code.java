/**
 * 
 */
package com.sjs.www.app.server.model;

import com.sjs.www.app.server.model.WebTransException.WebTransCode;

public enum Code implements WebTransCode {

	授权编号错误("0001", "不合法调用"),
	接口签名错误("0002", "不合法调用"),
	接口ID错误("0003", "无相应的接口处理服务"),
	时间差不在合理范围("0004","时间差不在合理范围"),
	接口方法重复("0005","无相应的接口处理服务"),
	接口方法参数过多("0006","无相应的接口处理服务"),
	接口方法参数错误("0007","无相应的接口处理服务"),
	接口方法参数类型错误("0008","无相应的接口处理服务"),
	会话过期_或者未登录("0009","会话过期，或者未登录"),
	接口权限不足("0010","权限不足"),
	
	用户名密码错误("0050","您的用户名密码错误,请重新再试！"),
	用户名错误("0060","您的用户名错误,请重新再试！"),
	密码错误("0070","您的旧密码错误，请重新尝试！"),
	
	没有找到更新记录("0080","没有找到更新记录，请重新尝试！"),
	
	未知异常("9999", "系统繁忙,请稍后再试");
	
	private String code;
	private String msg;
	
	private Code(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	@Override
	public String responseCode() {
		return code;
	}

	@Override
	public String resonseMsg() {
		return msg;
	}
}
