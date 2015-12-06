/**
 * 
 */
package com.sjs.www.app.server.model;

public class WebTransException extends Exception {
	private static final long serialVersionUID = 4742929652979095567L;
	private String responseCode;
	private String responseMsg;
	
	public WebTransException() {
	}

	public WebTransException(WebTransCode contant) {
		this(contant.responseCode(), contant.resonseMsg());
	}
	
	public WebTransException(String responseCode, String responseMsg) {
		this.responseCode = responseCode;
		this.responseMsg = responseMsg;
	}
	
	public WebTransException(Throwable e) {
		super(e);
	}

	/**
	 * 返回码
	 */
	public String getResponseCode() {
		return responseCode;
	}
	
	/**
	 * 返回信息
	 */
	public String getResponseMsg() {
		return responseMsg;
	}
	
	public static interface WebTransCode {
		String responseCode();
		
		String resonseMsg();
	}
	
}
