package com.example.xkfeng.mycat.Util;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * Created by initializing on 2018/10/7.
 */
/*
-----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC5KjANS4Ro64qfeneKVbik+rla
QuyMtZ3K3bRJS7fXkZPHi3PVC4PNZ7woiPTcROByJNt4kXHQ34NGjc8w5KWGSEj1
eu4NSYkuHotSF/gxo2F570fFyPKJmGmWvEkpP7P8I08i6uYjwCSUv23+Aq+gkwjJ
3nQsPWYLPnWoaWeRdwIDAQAB
-----END PUBLIC KEY-----

-----BEGIN PRIVATE KEY-----
MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALkqMA1LhGjrip96
d4pVuKT6uVpC7Iy1ncrdtElLt9eRk8eLc9ULg81nvCiI9NxE4HIk23iRcdDfg0aN
zzDkpYZISPV67g1JiS4ei1IX+DGjYXnvR8XI8omYaZa8SSk/s/wjTyLq5iPAJJS/
bf4Cr6CTCMnedCw9Zgs+dahpZ5F3AgMBAAECgYAlhUb3arYKDLCqYEaY4YXo6GVm
xOjqGmM/GG3P7Gf0ZqtrY/a01QCAPuUB0b7MY6iXeKCAbfiCOsh/I304ixx/ISOp
mxbhBxVUxE14x2K9jktKbKJrL1Y//nNSFxK0oXUhMr/2BThzZZI19zecD+L2V+Kz
a2jtdFOt2vBNBPczAQJBAN8BXj7aqBjoHGWPi8hQzGqvg3oL9wBLc+lQ5g+Qs0Bp
oHi/wmucIhJ9WA1EZT3uByIViiNv5Yx3xwxKBF7WJCsCQQDUj46fYc/yguw3Ze6f
UPBOHXAi8VMRalYOVqzF629wmx3T3iq62KUvtEcAPjLXR/TWQCOzEV4vXeCZOQ73
XSXlAkEAk7ybTDadcEqtLVdIun9UvUTjbEJq82YJN0Oh9iPdrMmNRxF64sGADRG+
+KMcE0gSr7DnYAysXT+ovWp3tMBXGwJADRIe1RIwtVrzp5xvBgD2JfeNc7ifQQzm
6c7OpQULP0NqnCKnQgIcdkiBrTQl8DMOjTY4e8RvpD4Dl6h4kSF7OQJAGopxrHVv
qNgphA0tbqucHvUzo+6V1GfMgiG5yJ+UnF6Km8YnbcK6KH7smuNUft8TPnT9QxRL
gmmpHhUS6fjuTQ==
-----END PRIVATE KEY-----

 */
public class RSAEncrypt {

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALkqMA1LhGjrip96\n" +
            "d4pVuKT6uVpC7Iy1ncrdtElLt9eRk8eLc9ULg81nvCiI9NxE4HIk23iRcdDfg0aN\n" +
            "zzDkpYZISPV67g1JiS4ei1IX+DGjYXnvR8XI8omYaZa8SSk/s/wjTyLq5iPAJJS/\n" +
            "bf4Cr6CTCMnedCw9Zgs+dahpZ5F3AgMBAAECgYAlhUb3arYKDLCqYEaY4YXo6GVm\n" +
            "xOjqGmM/GG3P7Gf0ZqtrY/a01QCAPuUB0b7MY6iXeKCAbfiCOsh/I304ixx/ISOp\n" +
            "mxbhBxVUxE14x2K9jktKbKJrL1Y//nNSFxK0oXUhMr/2BThzZZI19zecD+L2V+Kz\n" +
            "a2jtdFOt2vBNBPczAQJBAN8BXj7aqBjoHGWPi8hQzGqvg3oL9wBLc+lQ5g+Qs0Bp\n" +
            "oHi/wmucIhJ9WA1EZT3uByIViiNv5Yx3xwxKBF7WJCsCQQDUj46fYc/yguw3Ze6f\n" +
            "UPBOHXAi8VMRalYOVqzF629wmx3T3iq62KUvtEcAPjLXR/TWQCOzEV4vXeCZOQ73\n" +
            "XSXlAkEAk7ybTDadcEqtLVdIun9UvUTjbEJq82YJN0Oh9iPdrMmNRxF64sGADRG+\n" +
            "+KMcE0gSr7DnYAysXT+ovWp3tMBXGwJADRIe1RIwtVrzp5xvBgD2JfeNc7ifQQzm\n" +
            "6c7OpQULP0NqnCKnQgIcdkiBrTQl8DMOjTY4e8RvpD4Dl6h4kSF7OQJAGopxrHVv\n" +
            "qNgphA0tbqucHvUzo+6V1GfMgiG5yJ+UnF6Km8YnbcK6KH7smuNUft8TPnT9QxRL\n" +
            "gmmpHhUS6fjuTQ==" ;

    public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC5KjANS4Ro64qfeneKVbik+rla\n" +
            "QuyMtZ3K3bRJS7fXkZPHi3PVC4PNZ7woiPTcROByJNt4kXHQ34NGjc8w5KWGSEj1\n" +
            "eu4NSYkuHotSF/gxo2F570fFyPKJmGmWvEkpP7P8I08i6uYjwCSUv23+Aq+gkwjJ\n" +
            "3nQsPWYLPnWoaWeRdwIDAQAB" ;

    /**
     * 将base64编码后的公钥字符串转成PublicKey实例
     *
     * @param publicKey 公钥字符
     * @return publicKEY
     * @throws Exception exception
     */
    public static PublicKey getPublicKey(String publicKey) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey, Base64.NO_WRAP);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 将base64编码后的私钥字符串转成PrivateKey实例
     *
     * @param privateKey 私钥字符串
     * @return 私钥对象
     * @throws Exception exception
     */
    public static PrivateKey getPrivateKey(String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey, Base64.NO_WRAP);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * RSA加密
     *
     * @param content   待加密文本
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception exception
     */
    public static String encrypt(String content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");//java默认"RSA"="RSA/ECB/PKCS1Padding"
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] data = content.getBytes();
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return new String(Base64.encode(encryptedData, Base64.NO_WRAP));
    }

    /**
     * RSA解密
     *
     * @param content    密文
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception exception
     */
    public static String decrypt(String content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedData = Base64.decode(content, Base64.NO_WRAP);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData);
    }
}
