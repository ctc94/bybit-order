package com.ctc.order.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class OrderUtil {

	static List<String> ret = null;
	static {
		ret = FileUtil.readFile(".api");
	}

	public static String getApiKey() {
		String line = ret.get(0);
		String key[] = line.split(":");
		return key[0];
	}

	public static String getSecret() {
		String line = ret.get(0);
		String key[] = line.split(":");
		return key[1];
	}

	public static String genQueryString(TreeMap<String, String> params, String secret) {
		Set<String> keySet = params.keySet();
		Iterator<String> iter = keySet.iterator();
		StringBuilder sb = new StringBuilder();
		while (iter.hasNext()) {
			String key = iter.next();
			sb.append(key + "=" + params.get(key));
			sb.append("&");
		}
		sb.deleteCharAt(sb.length() - 1);

		return sb + "&sign=" + getSign(secret, sb.toString());
	}

	public static TreeMap<String, String> getTreeMap() {
		return new TreeMap<String, String>(new Comparator<String>() {
			public int compare(String obj1, String obj2) {
				// sort in alphabet order
				return obj1.compareTo(obj2);
			}
		});
	}

	public static String getSign(String secret, String sb) {
		return bytesToHex(getByte(secret, sb));
	}

	private static byte[] getByte(String secret, String sb) {
		Mac sha256_HMAC = null;
		try {
			sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e1) {
			throw new RuntimeException(e1);
		}
		return sha256_HMAC.doFinal(sb.getBytes());
	}

	private static String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

}
