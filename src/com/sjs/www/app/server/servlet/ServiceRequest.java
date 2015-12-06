/**
 * 
 */
package com.sjs.www.app.server.servlet;

import java.lang.reflect.Field;
import java.net.URLEncoder;

public class ServiceRequest {
	private String sqbh; //授权编号
	private String jkid; //接口编号
	private String jkqm; //接口签名
	private String time; //时间戳
	private String requestData; //请求信息
	public String getSqbh() {
		return sqbh;
	}
	public void setSqbh(String sqbh) {
		this.sqbh = sqbh;
	}
	public String getJkid() {
		return jkid;
	}
	public void setJkid(String jkid) {
		this.jkid = jkid;
	}
	public String getJkqm() {
		return jkqm;
	}
	public void setJkqm(String jkqm) {
		this.jkqm = jkqm;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getRequestData() {
		return requestData;
	}
	public void setRequestData(String requestData) {
		this.requestData = requestData;
	}
		
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		Field[] fields = this.getClass().getDeclaredFields();
		
		for (Field field : fields) {
			field.setAccessible(true); 
			try {
				stringBuffer.append(field.getName())
				.append("=")
				.append(URLEncoder.encode(field.get(this).toString(), "utf-8"))
				.append("&");
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		return stringBuffer.substring(0, stringBuffer.length() -1);
	}
}
