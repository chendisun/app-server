package com.sjs.www.app.server.service.test;

import org.junit.Test;

import com.sjs.www.app.server.service.util.TestServiceUitl;

public class TestService {

	@Test
	public void testService() {
		TestServiceUitl serviceUitl = new TestServiceUitl();
		try {
			serviceUitl.doPost("APP0002", "aaaa");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
