package com.zackmurry.cardtown.util;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class EncryptionUtils {

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * takes a user's plain-text password and generates and encryption key
     * @param password user's plain-text password
     * @return a hex-encoded SHA-256 hash of the password
     */
    public static String getEncryptionKeyHex(String password) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        byte[] encodedHash = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedHash);
    }

    /**
     * takes a user's plain-text password and generates and encryption key
     * @param password user's plain-text password
     * @return a SHA-256 hash of the password
     */
    public static byte[] getEncryptionKey(String password) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * generates a strong AES key
     * @param keySize number of bits to generate
     * @return the new AES key
     */
    public static SecretKey generateStrongAESKey(final int keySize) {
        final KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException("AES key generator should always be available in a Java runtime", e);
        }
        final SecureRandom rng;
        try {
            rng = SecureRandom.getInstanceStrong();
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException("No strong secure random available to generate strong AES key", e);
        }
        // already throws IllegalParameterException for wrong key sizes
        keyGenerator.init(keySize, rng);

        return keyGenerator.generateKey();
    }

    public static byte[] encryptAES(byte[] value, byte[] secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey, "AES"));
        return cipher.doFinal(value);
    }

    public static byte[] decryptAES(byte[] cipherText, byte[] secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        final SecretKeySpec skc = new SecretKeySpec(secretKey, "AES");
        cipher.init(Cipher.DECRYPT_MODE, skc);
        return cipher.doFinal(cipherText);
    }

}
