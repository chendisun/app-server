package com.sjs.www.app.server.model.http;

public class LoginRequest {
	private String ueseIdentif;

	private String passwd;

	public void setUeseIdentif(String ueseIdentif) {
		this.ueseIdentif = ueseIdentif;
	}

	public String getUeseIdentif() {
		return this.ueseIdentif;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getPasswd() {
		return this.passwd;
	}
}
