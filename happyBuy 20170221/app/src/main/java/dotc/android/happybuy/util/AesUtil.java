package dotc.android.happybuy.util;

import android.util.Base64;

import java.security.Key;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by wangjun on 16/3/28.
 */
public class AesUtil {

    private static final String UTF8 = "UTF-8";

    private static final IvParameterSpec IV = new IvParameterSpec(new byte[]{
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private static byte[] getHash(final String algorithm, final String text) {
        try {
            return getHash(algorithm, text.getBytes(UTF8));
        } catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private static byte[] getHash(final String algorithm, final byte[] data) {
        try {
            final MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(data);
            return digest.digest();
        } catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
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

    public static byte[] hex2byte(String str) {
        str = str.trim();
        int len = str.length();
        if (len == 0 || len % 2 == 1)
            return null;
        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[i / 2] = (byte) Integer.decode("0x" + str.substring(i, i + 2)).intValue();
            }
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    public static String encrypt(final String str, String key) {
        try {
            return encrypt(str.getBytes(UTF8), key);
        } catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private static String encrypt(final byte[] data, String key) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            Key secretKey = new SecretKeySpec(getHash("MD5", key), ALGORITHM);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, IV);
            final byte[] encryptData = cipher.doFinal(data);

            final byte[] base64En = Base64.encode(encryptData, Base64.NO_WRAP);

            return new String(base64En, UTF8);
        } catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static String encrypt(final String str, byte[] key) {
        try {
            return encrypt(str.getBytes(UTF8), key);
        } catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static String encrypt(final byte[] data, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            Key secretKey = new SecretKeySpec(key, ALGORITHM);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, IV);
            final byte[] encryptData = cipher.doFinal(data);

            final byte[] base64En = Base64.encode(encryptData, Base64.NO_WRAP);

            return new String(base64En, UTF8);
        } catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static String decrypt(final String str, String key) {
        try {
            return decrypt(str.getBytes(UTF8), key);
        } catch (final Exception ex) {

            return null;
        }
    }

    private static String decrypt(final byte[] data, String key) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            Key secretKey = new SecretKeySpec(getHash("MD5", key), ALGORITHM);

            final byte[] base64De = Base64.decode(data, Base64.NO_WRAP);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, IV);
            final byte[] decryptData = cipher.doFinal(base64De);

            return new String(decryptData, UTF8);
        } catch (final Exception ex) {
            return null;
        }
    }


    public static String decrypt(final String str, final byte[] key) {
        try {
            return decrypt(str.getBytes(UTF8), key);
        } catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private static String decrypt(final byte[] data, final byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            Key secretKey = new SecretKeySpec(key, ALGORITHM);

            final byte[] base64De = Base64.decode(data, Base64.NO_WRAP);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, IV);
            final byte[] decryptData = cipher.doFinal(base64De);

            return new String(decryptData, UTF8);
        } catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }


}
