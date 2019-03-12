package com.lq186.fabric.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public final class Utils {

	private static final SimpleDateFormat DATE_FORMATER = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public static String formatDate(Date date) {
		return DATE_FORMATER.format(date);
	}

	public static String grpcTLSify(boolean useTLS, String location) {
		location = location.trim();
		Exception e = org.hyperledger.fabric.sdk.helper.Utils.checkGrpcUrl(location);
		if (e != null) {
			throw new RuntimeException(String.format("Bad TEST parameters for grpc url %s", location), e);
		}
		return useTLS ? location.replaceFirst("^grpc://", "grpcs://") : location;

	}

	public static String httpTLSify(boolean useCATLS, String location) {
		location = location.trim();
		return useCATLS ? location.replaceFirst("^http://", "https://") : location;
	}

	public static boolean isNumeric(String str) {
		if (null == str) {
			return false;
		}
		return Pattern.matches("[0-9]*", str);
	}
}
