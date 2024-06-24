package com.bin.webmonitor.common.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
@SuppressWarnings("all")
public class DesHelper {

    public static final String KEY_ALGORITHM = "DES";

    public static final String CIPHER_ALGORITHM_ECB = "DES/ECB/PKCS5Padding";

    public static final String CIPHER_ALGORITHM_CBC = "DES/CBC/PKCS5Padding";

    public static final String CHAR_ENCODING = "utf-8";



    private static Key toKey(byte[] key)
            throws Exception {
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");

        SecretKey secretKey = skf.generateSecret(dks);
        return secretKey;
    }

    private static byte[] initkey()
            throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("DES");
        kg.init(56);
        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }

    public static String initkeyString()
            throws Exception {

        return Base64.getEncoder().encodeToString(initkey());
    }

    public static String initkeyString(String key)
            throws Exception {
        return Base64.getEncoder().encodeToString(initkey(key));
    }

    public static byte[] initkey(String key)
            throws Exception {
        if ((key == null) || (key.getBytes().length < 8)) {
            throw new Exception("key不合法, 长度必须大于8个字节!");
        }
        byte[] bufKey = key.getBytes("utf-8");
        DESKeySpec dks = new DESKeySpec(bufKey);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(dks);
        return securekey.getEncoded();
    }

    public static byte[] encrypt(byte[] data, byte[] key)
            throws Exception {
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(1, k);
        return cipher.doFinal(data);
    }

    public static String encryptString(byte[] data, byte[] key)
            throws Exception {

        return Base64.getEncoder().encodeToString(encrypt(data, key));
    }

    public static String encryptString(String data, String key)
            throws Exception {
        return encryptString(data.getBytes("utf-8"), key.getBytes("utf-8"));
    }

    public static byte[] decrypt(byte[] data, byte[] key)
            throws Exception {
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(2, k);
        return cipher.doFinal(data);
    }

    public static String decrypt(String data, String key)
            throws Exception {
        return new String(decrypt(Base64.getDecoder().decode(data), key.getBytes("utf-8")));
    }

    public static byte[] encryptNetByte(String data, String key)
            throws Exception {
        keyLength(key);
        return encryptNet(data.getBytes("utf-8"), key.getBytes("utf-8"));
    }

    public static String encryptNetString(String data, String key)
            throws Exception {
        keyLength(key);
        return Base64.getEncoder().encodeToString(encryptNetByte(data, key));
    }

    private static byte[] encryptNet(byte[] data, byte[] key)
            throws Exception {
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key);
        cipher.init(1, secretKey, iv);
        return cipher.doFinal(data);
    }

    private static byte[] decryptNet(byte[] data, byte[] key)
            throws Exception {
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key);
        cipher.init(2, secretKey, iv);
        return cipher.doFinal(data);
    }

    public static String decryptNetString(String data, String key)
            throws Exception {
        keyLength(key);
        return new String(decryptNet(Base64.getDecoder().decode(data), key.getBytes("utf-8")));
    }

    public static String decryptNetString(byte[] data, byte[] key)
            throws Exception {
        keyLength(key);
        return new String(decryptNet(data, key));
    }

    private static void keyLength(String key) {
        keyLength(key.getBytes(StandardCharsets.UTF_8));
    }

    private static void keyLength(byte[] key) {
        if (key.length != 8) {
            throw new IllegalArgumentException("key length must be 8 bytes long!");
        }
    }


}
