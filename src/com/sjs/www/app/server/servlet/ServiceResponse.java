/**
 * 
 */
package com.sjs.www.app.server.servlet;

public class ServiceResponse {

	private String sqbh; //授权编号
	private String jkid; //接口编号
	private String jkqm; //接口签名
	private String time; //时间戳
	private String responseData; //返回信息
	private String responseCode;  //返回码
	private String responseMsg; //错误信息
	
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
	public String getResponseData() {
		return responseData;
	}
	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMsg() {
		return responseMsg;
	}
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	@Override
	public String toString() {
		return sqbh + " " + jkid + " " + jkqm + " " + time + " " + ((responseData == null || responseData.length() <= 512) ? responseData : responseData.substring(0, 512) + "...") + " " + responseCode + " " + responseMsg;
	}
}
