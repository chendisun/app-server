/**
 * 
 */
package com.sjs.www.app.server.util;

import javax.servlet.http.Cookie;

/**
 * Cookie 管理器
 * 
 */
public class CookieManager {

	public Cookie getCookieValue(Cookie[] cookies, String name) {
		if (cookies == null)
			return null;

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name))
				return cookie;
		}

		return null;
	}

}
