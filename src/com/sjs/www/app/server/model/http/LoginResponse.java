package com.sjs.www.app.server.model.http;

public class LoginResponse {
	private int userId;

	private String mobile;

	private String userIconUrl;

	private String vipGrade;

	private boolean hasRealName;

	private String idcardNo;

	private String nickName;

	private String email;

	private boolean hasBindBankCard;

	private boolean hasBindIdentifData;

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setUserIconUrl(String userIconUrl) {
		this.userIconUrl = userIconUrl;
	}

	public String getUserIconUrl() {
		return this.userIconUrl;
	}

	public void setVipGrade(String vipGrade) {
		this.vipGrade = vipGrade;
	}

	public String getVipGrade() {
		return this.vipGrade;
	}

	public void setHasRealName(boolean hasRealName) {
		this.hasRealName = hasRealName;
	}

	public boolean getHasRealName() {
		return this.hasRealName;
	}

	public void setIdcardNo(String idcardNo) {
		this.idcardNo = idcardNo;
	}

	public String getIdcardNo() {
		return this.idcardNo;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getNickName() {
		return this.nickName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return this.email;
	}

	public void setHasBindBankCard(boolean hasBindBankCard) {
		this.hasBindBankCard = hasBindBankCard;
	}

	public boolean getHasBindBankCard() {
		return this.hasBindBankCard;
	}

	public void setHasBindIdentifData(boolean hasBindIdentifData) {
		this.hasBindIdentifData = hasBindIdentifData;
	}

	public boolean getHasBindIdentifData() {
		return this.hasBindIdentifData;
	}
}
