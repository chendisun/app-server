/**
 * 
 */
package com.sjs.www.app.server.util;

public class StringUtils {
	private static final String POSITIVENUMREGEX = "^\\d*[1-9]\\d*$";//正整数

	/**
	 * <br>
	 * StringUtils.isNotEmptyByTrim("") = false <br>
	 * StringUtils.isNotEmptyByTrim("  ") = false <br>
	 * StringUtils.isNotEmptyByTrim(null) = false <br>
	 * StringUtils.isNotEmptyByTrim("123") = true <br>
	 * StringUtils.isNotEmptyByTrim(" 123 ") = true
	 */
	public static boolean isNotEmptyByTrim(String text) {
		if (text == null)
			return false;

		if (text.trim().isEmpty())
			return false;

		return true;
	}

	/**
	 * <br>
	 * StringUtils.isNotEmptyByTrim("") = true <br>
	 * StringUtils.isNotEmptyByTrim("  ") = true <br>
	 * StringUtils.isNotEmptyByTrim(null) = true <br>
	 * StringUtils.isNotEmptyByTrim("123") = false <br>
	 * StringUtils.isNotEmptyByTrim(" 123 ") = false
	 */
	public static boolean isEmptyByTrim(String text) {
		if (text == null)
			return true;

		if (text.trim().isEmpty())
			return true;

		return false;
	}

	/**
	 * <br>
	 * StringUtils.toStringByTrim("") = "" <br>
	 * StringUtils.toStringByTrim("  ") = "" <br>
	 * StringUtils.toStringByTrim(null) = null <br>
	 * StringUtils.toStringByTrim("123") = "123" <br>
	 * StringUtils.toStringByTrim(" 123 ") = "123"
	 */
	public static String toStringByTrim(String text) {
		if (text == null)
			return null;

		return text.trim();
	}
	
	public static String toStringByTrim(Object val) {
		if(val == null)
			return null;
		
		return toStringByTrim(val.toString());			
	}

	/**
	 * <br>
	 * StringUtils.toInteger("") = null <br>
	 * StringUtils.toInteger("  ") = null <br>
	 * StringUtils.toInteger(null) = null <br>
	 * StringUtils.toInteger("123") = 123 <br>
	 * StringUtils.toInteger(" 123 ") = 123 <br>
	 * StringUtils.toInteger("ABC") throws NumberFormatException
	 */
	public static Integer toInteger(String text) throws NumberFormatException {
		if (isNotEmptyByTrim(text))
			return Integer.valueOf(text.trim());
		else
			return null;
	}

	public static boolean equals(String text1, String text2) {
		if(text1 == text2)
			return true;
		
		if(text1 == null || text2 == null)
			return false;
		
		return text1.equals(text2);
	}
	
	public static String padChar(String text, char fill, int len, boolean leftpad) {
		if(text.length() >= len)
			return text.substring(0, len);
		
		StringBuilder builder = new StringBuilder();
		int size = len - text.length();
		for (int i = 0; i < size; i++) {
			builder.append(fill);
		}
		
		if(!leftpad)
			return builder.insert(0, text).toString();
		else
			return builder.append(text).toString();
	}
	
	/**
	 * 判断字符串text是否是电话号码
	 * @param text
	 * @return
	 */
	public static boolean isPhoneNo(String text) {
		if(text.isEmpty())
			return false;
		return text.matches("^((13|15|18)[0-9]{9})|((020|066|075|076)([0-9]{8}|[0-9]{9}))$");
	}
	
	
	/**
	 * 字符串加*号
	 * 例如：倒数第6位到倒数第3位加*号     str="123456789",a=6,b=3。返回的是"123***789"
	 */
	public static String addXingHao(String str,int a,int b){
		char[] c = str.toCharArray();
		if(a<=c.length-1){
			for(int i=c.length-b;i>=c.length-a;i--){
				c[i] = '*';
			}			
		}
		
		StringBuffer s = new StringBuffer();
		for(char cc : c){
			s.append(cc);
		}
		return s.toString();
	}
	
	public static boolean isZzhengshu(String str){
		if(str!=null && str.trim().length()!=0)
			return matchPositiveNum(str);
		else
			return false;
	}
	public static boolean matchPositiveNum(String str){
		return matchRegex(str,POSITIVENUMREGEX);
	}
	public static boolean matchRegex(String str, String regex){
		return str.matches(regex);
	}
	
	public static void main(String[] args) {
		System.out.println(isZzhengshu("123456"));
		System.out.println(isZzhengshu("123.456"));
		System.out.println(isZzhengshu("0.123456"));
		System.out.println(isZzhengshu("123456.0"));
	}
}
