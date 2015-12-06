/**
 * 
 */
package com.sjs.www.app.server.dao;

import com.sjs.www.app.server.model.WebTransException;
import com.sjs.www.app.server.model.bean.User;
import com.sjs.www.app.server.model.http.LoginResponse;

public interface UserServiceDao {

	User currentUser() throws WebTransException;

	LoginResponse login(User user) throws WebTransException;

	void logout() throws WebTransException;

	void modifyPwd(String hex_md5, String hex_md52) throws WebTransException;

}
