package com.sicao.smartwine.xhttp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	private static MessageDigest messageDigest = null;

	// private static Logger log = Logger.getLogger(MD5.class);

	public static String Encode(final String Source) {
		return Encode(Source, null);
	}

	public static String Encode(final String Source, final String charset) {
		if (Source == null)
			return null;
		if (messageDigest == null) {
			try {
				messageDigest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				return null;
			}
		}
		if (charset == null || charset.trim().length() <= 0)
			messageDigest.update(Source.getBytes());
		else {
			try {
				messageDigest.update(Source.getBytes(charset));
			} catch (Exception e) {
				messageDigest.update(Source.getBytes());
			}
		}
		byte[] digesta = messageDigest.digest();
		return Bytes2Hex(digesta);
	}

	public static byte[] EncodeBytes(byte[] Source) {
		if (messageDigest == null) {
			try {
				messageDigest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				return null;
			}
		}
		messageDigest.update(Source);
		byte[] ret = messageDigest.digest();
		return ret;
	}

	public static String Bytes2Hex(final byte[] Source) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < Source.length; n++) {
			stmp = (Integer.toHexString(Source[n] & 0xFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs;
	}

}
