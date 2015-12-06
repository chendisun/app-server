/**
 * 
 */
package com.sjs.www.app.server.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 3DES工具类
 * 
 * 
 */
public class DESEncrypt {
	protected static final Logger logger = LoggerFactory.getLogger(DESEncrypt.class);

	public static final String charset = "gbk";
	
	private static final String Algorithm = "DESede"; //定义加密算法,可用 DES,DESede,Blowfish
	
	Key key;

	public DESEncrypt(String strKey) throws UnsupportedEncodingException, InvalidKeyException {
		byte[] keyBytes = strKey.getBytes(charset);
		
		if(keyBytes.length != 24)
			throw new java.security.InvalidKeyException("Invalid key length: " + keyBytes.length + " bytes");
		
		try {
			key = new SecretKeySpec(keyBytes, Algorithm);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 加密
	 * @param strMing 加密明文
	 * @return 加密后的密文
	 */
	public String getEncString(String strMing) {
		try {
			byte[] byteMing = strMing.getBytes(charset);
			byte[] byteMi = getEncCode(byteMing);
			return new String(Base64.encodeBase64(byteMi), charset);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}
	
	private byte[] getEncCode(byte[] byteS) {
		byte[] byteFina = null;
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(Algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byteFina = cipher.doFinal(byteS);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	/**
	 * 解密
	 * @param strMi 加密后的密文
	 * @return 解密后的明文
	 */
	public String getDesString(String strMi) {
		try {
			byte[] byteMi = Base64.decodeBase64(strMi.getBytes(charset));
			byte[] byteMing = this.getDesCode(byteMi);

			return new String(byteMing, charset);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	private byte[] getDesCode(byte[] byteD) {
		try {
			Cipher cipher = Cipher.getInstance(Algorithm);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(byteD);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}

	public static void main(String[] args) throws UnsupportedEncodingException, InvalidKeyException {
		DESEncrypt des = new DESEncrypt("123456789012345678901234");
		
		String newKey = "1234567890123456你厉害";
		System.out.println("newKey:" + newKey);
		
		String strEnc = des.getEncString(newKey);// 加密字符串,返回String的密文
		System.out.println("Enc:" + strEnc);
		
		String strDes = des.getDesString(strEnc);// 把String 类型的密文解密
		System.out.println("Des:" + strDes);
	}
}
