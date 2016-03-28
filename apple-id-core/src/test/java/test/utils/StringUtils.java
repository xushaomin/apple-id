package test.utils;

public class StringUtils {

	public static boolean isBlank(String str) {
		if(null != str || str == "")
			return true;
		else
			return false;
	}
}
