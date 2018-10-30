package personal.mybatisx.util;

import java.util.Random;

public class CodeUtil {
	private static String[] strs = { "a", "b", "c", "d", "e", "f", "g", "h",
			"i", "j", "k","l", "m", "n","o", "p", "q", "r", "s", "t", "u",
			"v", "w", "x", "y", "z", "0","1", "2", "3", "4", "5", "6", "7", "8",
			"9" };
	
	private static String[] numb = { "1","2", "3", "4", "5", "6", "7", "8",
		"9" ,"0"};
	/**
	  * 方法的描述： 随机生成随字符串
	  * @param n 生成字符个数
	  * @return  String 生成的字符串
	 */
	private static String getRundomStr1(int n,String[] strs) {
		// 用Stringbuffer拼接字符串
		StringBuffer s = new StringBuffer();
		// 循环控制字符个数
		for (int i = 0; i < n; i++) {
			String temp = "";
			// 随机产生下标
			Random r = new Random();
			int a = r.nextInt(strs.length);// 0-36
			if (a < 26) {// 0-23是字母
				int b = r.nextInt(100);
				if (b % 2 == 0) {
					temp = strs[a].toUpperCase();
					s.append(temp);
				} else {
					s.append(strs[a]);
				}
			} else {
				s.append(strs[a]);
			}
		}
		return s.toString();
	}
	/**
	  * 方法的描述：默认返回4位的字符串
	  * @return  String
	  *
	 */
	public static String getRundomStr(int n) {
		return getRundomStr1(n,strs);
	}
	
	public static String getRundomNumb(int n) {
		return getRundomStr1(n,numb);
	}

	
}