package com.zackmurry.cardtown.util;


import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class EncryptionUtils {

//    public static String bytesToHex(byte[] hash) {
//        StringBuilder hexString = new StringBuilder(2 * hash.length);
//        for (byte b : hash) {
//            String hex = Integer.toHexString(0xff & b);
//            if (hex.length() == 1) {
//                hexString.append('0');
//            }
//            hexString.append(hex);
//        }
//        return hexString.toString();
//    }
//
//    public static byte[] hexToBytes(String s) {
//        int len = s.length();
//        byte[] data = new byte[len / 2];
//        for (int i = 0; i < len; i += 2) {
//            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
//                    + Character.digit(s.charAt(i+1), 16));
//        }
//        return data;
//    }

    /**
     * takes a user's plain-text text and generates and encryption key
     * @param text user's plain-text text
     * @return a hex-encoded SHA-256 hash of the text
     */
    public static String getSHA256HashBase64(String text) {
        byte[] bytes = getSHA256Hash(text);
        if (bytes == null) {
            return null;
        }
        return Base64.encodeBase64String(bytes);
    }

    /**
     * takes a user's plain-text text and generates and encryption key
     * @param text user's plain-text text
     * @return a SHA-256 hash of the text
     */
    public static byte[] getSHA256Hash(String text) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return messageDigest.digest(text.getBytes(StandardCharsets.UTF_8));
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

    // from https://gist.github.com/itarato/abef95871756970a9dad

    public static byte[] encryptAES(byte[] plainText, byte[] key) throws Exception {
        // Generating IV.
        int ivSize = 16;
        byte[] iv = new byte[ivSize];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Hashing key.
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(key);
        byte[] keyBytes = new byte[16];
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        // Encrypt.
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(plainText);

        // Combine IV and encrypted part.
        byte[] encryptedIVAndText = new byte[ivSize + encrypted.length];
        System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize);
        System.arraycopy(encrypted, 0, encryptedIVAndText, ivSize, encrypted.length);

        return encryptedIVAndText;
    }

    public static byte[] decryptAES(byte[] cipher, byte[] key) throws Exception {
        int ivSize = 16;
        int keySize = 16;

        // Extract IV.
        byte[] iv = new byte[ivSize];
        System.arraycopy(cipher, 0, iv, 0, iv.length);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Extract encrypted part.
        int encryptedSize = cipher.length - ivSize;
        byte[] encryptedBytes = new byte[encryptedSize];
        System.arraycopy(cipher, ivSize, encryptedBytes, 0, encryptedSize);

        // Hash key.
        byte[] keyBytes = new byte[keySize];
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(key);
        System.arraycopy(md.digest(), 0, keyBytes, 0, keyBytes.length);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        // Decrypt.
        Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        return cipherDecrypt.doFinal(encryptedBytes);
    }

    /**
     * converts a UTF-8 String to an AES-encrypted, hex-encoded String
     * @param plainText text to encrypt
     * @param secretKey secret key for AES
     * @return cipher in hex
     * @throws Exception if something goes wrong, like a bad secret key
     */
    public static String encryptStringAES(String plainText, byte[] secretKey) throws Exception {
        return Base64.encodeBase64String(encryptAES(plainText.getBytes(StandardCharsets.UTF_8), secretKey));
    }

    /**
     * uses AES encryption with hex byte encoding and switches to UTF-8 character encoding
     * @param cipher text to decrypt
     * @param secretKey AES secret key
     * @return output in hex
     */
    public static String decryptStringAES(String cipher, byte[] secretKey) throws Exception {
        return new String(decryptAES(Base64.decodeBase64(cipher), secretKey), StandardCharsets.UTF_8);
    }

}
