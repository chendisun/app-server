package com.sjs.www.app.server.service.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sjs.www.app.server.servlet.ServiceRequest;
import com.sjs.www.app.server.servlet.ServiceResponse;
import com.sjs.www.app.server.util.GsonUtils;
import com.sjs.www.app.server.util.MD5;
import com.sjs.www.app.server.util.StringUtils;
import com.sjs.www.app.server.util.URLStreamHandlerImpl;

public class TestServiceUitl {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestServiceUitl.class);

//	private String trafficUrl = "http://120.24.60.216:8090/appservice/entryService";
	private String trafficUrl = "http://localhost:8080/app-web-service/entryService";

	private String sqbh = "test";

	private String sqkey = "123456abc";

	public void start() {
	}

	public void stop() {
	}

	public <T> T doPost(String jkid, String requestData, Class<T> responseClazz) throws Exception {
		String responseData = doPost(jkid, requestData);
		return GsonUtils.json2Bean(responseData, responseClazz);
	}
		
	public String doPost(String jkid, String requestData) throws Exception {
		if (StringUtils.isEmptyByTrim(jkid))
			throw new Exception();

		HttpURLConnection conn = null;
		Writer write = null;

		try {
			final ServiceRequest trafficRequest = new ServiceRequest();
			trafficRequest.setJkid(jkid);
			trafficRequest.setRequestData(requestData);
			trafficRequest.setSqbh(sqbh);
			trafficRequest.setTime(System.currentTimeMillis() + "");

			String jkqm = MD5.md5toUpperCase(trafficRequest.getJkid() + "&" + trafficRequest.getTime() + "&" + trafficRequest.getRequestData() + "&" + sqkey);
			trafficRequest.setJkqm(jkqm);

			final long startTime = System.currentTimeMillis();

			conn = (HttpURLConnection) new URL(null, trafficUrl, new URLStreamHandlerImpl()).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(15 * 1000);
			conn.setConnectTimeout(5 * 1000);

			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

			String reqtxt = trafficRequest.toString();
			write = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			write.write(reqtxt);
			write.flush();
			// write.close();

			LOGGER.info("req{} {}", trafficUrl, reqtxt.length() > 1024 ? reqtxt.substring(0, 1024) + "..." : reqtxt);
			
			int responseCode = conn.getResponseCode();
			
			if (responseCode == HttpURLConnection.HTTP_OK) {
				String resptxt = toString(conn.getInputStream());

				LOGGER.info("resp{} {} " + (System.currentTimeMillis() - startTime) + "ms", responseCode, resptxt.length() > 1024 ? resptxt.substring(0, 1024) + "..." : resptxt);
				
				ServiceResponse response = GsonUtils.json2Bean(resptxt, ServiceResponse.class);
				
				if(!StringUtils.isEmptyByTrim(response.getResponseCode()) && response.getResponseCode().matches("0+")){
					return response.getResponseData();
				} else {
					return "";
				}
				 
			} else {
				String resptxt = toString(conn.getErrorStream());

				LOGGER.info("resp{} {} " + (System.currentTimeMillis() - startTime) + "ms", responseCode, resptxt);

				throw new Exception();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (write != null)
				try {
					write.close();
				} catch (Exception e) {
				}

			if (conn != null)
				conn.disconnect();
		}

		throw new Exception();
	}

	private String toString(InputStream input) throws IOException {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(input, "utf-8"));

			StringBuilder builder = new StringBuilder();
			do {
				String line = reader.readLine();

				if (line == null)
					break;

				builder.append(line);
			} while (true);

			return builder.toString();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			
			if(input != null)
				input.close();
		}
	}

	public void setTrafficUrl(String trafficUrl) {
		this.trafficUrl = trafficUrl;
	}

	public void setSqbh(String sqbh) {
		this.sqbh = sqbh;
	}

	public void setSqkey(String sqkey) {
		this.sqkey = sqkey;
	}
}
