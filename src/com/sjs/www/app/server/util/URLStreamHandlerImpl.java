/**
 * 
 */
package com.sjs.www.app.server.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class URLStreamHandlerImpl extends URLStreamHandler {

	private int connectTimeout = 10000;
	private int readTimeout = 20000;
	
	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		URL clone_url = new URL(url.toString());
		HttpURLConnection clone_urlconnection = (HttpURLConnection) clone_url.openConnection();
		// TimeOut settings
		clone_urlconnection.setConnectTimeout(connectTimeout);
		clone_urlconnection.setReadTimeout(readTimeout);
		return (clone_urlconnection);
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

}
