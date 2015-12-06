package com.sjs.www.app.server.service;

import javax.annotation.Resource;

import com.sjs.www.app.server.dao.UserServiceDao;
import com.sjs.www.app.server.model.WebTransException;
import com.sjs.www.app.server.servlet.ServiceRequest;
import com.sjs.www.app.server.servlet.ServiceResponse;

public class Test2Service extends BaseService {
	@Resource
	private UserServiceDao userDao;

	@Override
	public ServiceResponse doService(ServiceRequest request) {
		return super.doService(request);
	}
	
	@Override
	protected String doBusiService(String requestData) throws WebTransException {
		userDao.currentUser();
		
		return "测试需要登录";
	}

}
