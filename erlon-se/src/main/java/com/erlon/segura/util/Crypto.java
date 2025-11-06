package com.erlon.segura.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Crypto {
    private static final String KEY = System.getenv().getOrDefault("TOKEN_KEY", "0123456789ABCDEF"); // 16 chars
    private static final String ALGO = "AES";

    public static String encrypt(String plain) throws Exception {
        SecretKeySpec sk = new SecretKeySpec(KEY.getBytes(), ALGO);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, sk);
        byte[] enc = c.doFinal(plain.getBytes());
        return Base64.getEncoder().encodeToString(enc);
    }

    public static String decrypt(String cipherText) throws Exception {
        SecretKeySpec sk = new SecretKeySpec(KEY.getBytes(), ALGO);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, sk);
        byte[] dec = c.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(dec);
    }
}
