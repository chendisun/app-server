package com.sjs.www.app.server.service.test;

import org.junit.Test;

import com.sjs.www.app.server.model.http.LoginRequest;
import com.sjs.www.app.server.service.util.TestServiceUitl;
import com.sjs.www.app.server.util.GsonUtils;

public class TestLoginService {

	@Test
	public void testService() {
		TestServiceUitl serviceUitl = new TestServiceUitl();
		try {
			LoginRequest request = new LoginRequest();
			request.setPasswd("fasfasfsafasf");
			request.setUeseIdentif("adddd");
			serviceUitl.doPost("login", GsonUtils.bean2json(request));
			serviceUitl.doPost("APP0002", "aaaa");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
