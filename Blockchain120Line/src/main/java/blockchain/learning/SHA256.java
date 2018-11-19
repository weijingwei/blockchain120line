package blockchain.learning;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
	
	public static String crypt(String info) {
		MessageDigest messageDigest;
		String encodestr = "";
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(info.getBytes("UTF-8"));
			encodestr = byte2Hex(messageDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodestr;
	}
	
	private static String byte2Hex(byte[] bytes) {
		StringBuffer buffer = new StringBuffer();
		String temp = null;
		for (byte b : bytes) {
			temp = Integer.toHexString(b & 0xFF);
			if (temp.length() == 1) {
				buffer.append("0");
			}
			buffer.append(temp);
		}
		return buffer.toString();
	}

}