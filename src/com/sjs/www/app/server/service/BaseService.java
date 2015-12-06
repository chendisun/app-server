/**
 * 
 */
package com.sjs.www.app.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sjs.www.app.server.model.WebTransException;
import com.sjs.www.app.server.servlet.IService;
import com.sjs.www.app.server.servlet.ServiceRequest;
import com.sjs.www.app.server.servlet.ServiceResponse;

public abstract class BaseService implements IService{
	protected static final Logger LOGGER = LoggerFactory.getLogger(BaseService.class);
	
	@Override
	public ServiceResponse doService(ServiceRequest request){
		
		String responseData = null;
		try {
			responseData = doBusiService(request.getRequestData());
		} catch (WebTransException e) {
			return setResponse(request, e, null);
		}
		
		return setResponse(request, null, responseData);
	}

	protected abstract String doBusiService(String requestData) throws WebTransException;
	
	protected ServiceResponse setResponse(ServiceRequest request, WebTransException e, String responseData) {
		ServiceResponse response = new ServiceResponse();
		
		response.setSqbh(request.getSqbh());
		response.setJkid(request.getJkid());
		response.setTime(System.currentTimeMillis() + "");
		
		if(e != null){
			response.setResponseCode(e.getResponseCode());
			response.setResponseMsg(e.getResponseMsg());
		} else {
			response.setResponseCode("0000");
			if(responseData != null)
				response.setResponseData(responseData);
		}
		
		return response;
	}
	
}
