/**
 * 
 */
package dotc.android.happybuy.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
public class Md5Util {

	private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

	private static byte[] getHash(final String algorithm, final byte[] data) {
		try {
			final MessageDigest digest = MessageDigest.getInstance(algorithm);
			digest.update(data);
			return digest.digest();
		} catch (final Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

	public static final byte[] md5(byte[] data) {
		final byte[] md5 = getHash("MD5", data);
		return md5;
	}

	public static final String md5Hex(String data) {
		final byte[] md5 = getHash("MD5", data.getBytes());
		return bytesToHex(md5);
	}

	public static final String md5Hex(byte[] data) {
		final byte[] md5 = getHash("MD5", data);
		return bytesToHex(md5);
	}

	private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}
	/**
	 * MD5UpperCase
	 * @param str
	 * @return
	 */
	public static String getMD5UpperCase(String str) {  
	    MessageDigest messageDigest = null;

		try {
	        messageDigest = MessageDigest.getInstance("MD5");
	        messageDigest.reset();
	        messageDigest.update(str.getBytes("UTF-8"));  
	    } catch (NoSuchAlgorithmException e) {  
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();  
	    }  

	    byte[] byteArray = messageDigest.digest();  

	    StringBuffer md5StrBuff = new StringBuffer();  

	    for (int i = 0; i < byteArray.length; i++) {              
	        if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)  
	            md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));  
	        else  
	            md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));  
	    }  

	    return md5StrBuff.toString().toUpperCase();  
	}
	
	/**
	 * MD5LowerCase
	 * @param str 
	 * @return
	 */
	public static String getMD5LowerCase(String str) { 
	    return getMD5UpperCase(str).toLowerCase();
	}

	
}
