package com.autohome.car.api.common;

import org.springframework.util.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

public class DESUtil {

    /**
     * 偏移变量，固定占8位字节
     */
    private final static String IV_PARAMETER = "12345678";
    private final static byte[] IV_PARAMETER_BYTES = new byte[]{(byte) 0x12, (byte)0x34, (byte)0x56, (byte)0x78, (byte)0x90, (byte)0xAB, (byte)0xCD, (byte)0xEF};
    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "DES";
    /**
     * 加密/解密算法-工作模式-填充模式
     */
    private static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";
    /**
     * 默认编码
     */
    private static final String CHARSET = "utf-8";
    private static final String PASSWORD = "lizhongw";
    /**
     * 生成key
     *
     * @param password
     * @return
     * @throws Exception
     */
    private static Key generateKey(String password) throws Exception {
        DESKeySpec dks = new DESKeySpec(password.getBytes(CHARSET));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        return keyFactory.generateSecret(dks);
    }

    /**
     * DES解密字符串
     *
     * @param password 解密密码，长度不能够小于8位
     * @param data 待解密字符串
     * @return 解密后内容
     */
    public static String decrypt(String data) {
//        if (password== null || password.length() < 8) {
//            throw new RuntimeException("加密失败，key不能小于8位");
//        }
        if (data == null)
            return null;
        try {
            Key secretKey = generateKey(PASSWORD);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
//            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(CHARSET));
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER_BYTES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return new String(cipher.doFinal(Base64.getDecoder().decode(data.getBytes(CHARSET))), CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * DES加密字符串
     *
     * @param password 加密密码，长度不能够小于8位
     * @param data 待加密字符串
     * @return 加密后内容
     */
    public static String encrypt(String password, String data) {
        if (password== null || password.length() < 8) {
            password = PASSWORD;
//            throw new RuntimeException("加密失败，key不能小于8位");
        }
        if (data == null)
            return null;
        try {
            Key secretKey = generateKey(password);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
//            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(CHARSET));
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER_BYTES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] bytes = cipher.doFinal(data.getBytes(CHARSET));
            return new String(Base64.getEncoder().encode(bytes));

        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }


}
