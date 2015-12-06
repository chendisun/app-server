package com.sjs.www.app.server.service;

import javax.annotation.Resource;

import com.sjs.www.app.server.dao.UserServiceDao;
import com.sjs.www.app.server.model.WebTransException;
import com.sjs.www.app.server.model.bean.User;
import com.sjs.www.app.server.model.http.LoginRequest;
import com.sjs.www.app.server.model.http.LoginResponse;
import com.sjs.www.app.server.servlet.ServiceRequest;
import com.sjs.www.app.server.servlet.ServiceResponse;
import com.sjs.www.app.server.util.GsonUtils;

public class LoginService extends BaseService {
	@Resource
	private UserServiceDao userDao;

	@Override
	public ServiceResponse doService(ServiceRequest request) {
		return super.doService(request);
	}
	
	@Override
	protected String doBusiService(String requestData) throws WebTransException {
		LoginRequest request = GsonUtils.json2Bean(requestData, LoginRequest.class);
		User user = new User(request.getUeseIdentif(), request.getPasswd());
		LoginResponse loginResponse = userDao.login(user);
		
		return GsonUtils.bean2json(loginResponse);
	}

}
