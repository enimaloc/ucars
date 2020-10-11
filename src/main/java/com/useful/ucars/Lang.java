package com.useful.ucars;

public class Lang {
	public static String get(String key) {
		String val = getRaw(key);
		val = UCars.colorise(val);
		return val;
	}

	public static String getRaw(String key) {
		if (!UCars.lang.contains(key)) {
			return key;
		}
		return UCars.lang.getString(key);
	}
}
