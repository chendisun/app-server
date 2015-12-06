package com.sjs.www.app.server.service;

import com.sjs.www.app.server.model.WebTransException;
import com.sjs.www.app.server.servlet.ServiceRequest;
import com.sjs.www.app.server.servlet.ServiceResponse;

public class TestService extends BaseService {

	@Override
	public ServiceResponse doService(ServiceRequest request) {
		return super.doService(request);
	}
	
	@Override
	protected String doBusiService(String requestData) throws WebTransException {
		return "成功了";
	}

}
