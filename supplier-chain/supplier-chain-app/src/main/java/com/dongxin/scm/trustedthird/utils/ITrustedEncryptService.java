package com.dongxin.scm.trustedthird.utils;

import java.util.Map;

public interface ITrustedEncryptService {

    /** */
    /**
     * 获取公钥的key
     */
    public final String PUBLIC_KEY = "PublicKey";

    /** */
    /**
     * 获取私钥的key
     */
    public final String PRIVATE_KEY = "PrivateKey";

    /**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     *
     * @return
     * @throws Exception
     */
    public Map<String, String> genKeyPair() throws Exception;

    /**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     *
     * @param data
     *            需要签名的内容
     * @param privateKey
     *            私钥(十六进制字符串编码)
     *
     * @return
     * @throws Exception
     */
    public String sign(String privateKey, String data) throws Exception ;

    /**
     * <p>
     * 校验数字签名
     * </p>
     *
     * @param data
     *            需要加签的内容
     * @param publicKey
     *            公钥(十六进制字符串编码)
     * @param sign
     *            数字签名(十六进制字符串编码)
     *
     * @return
     * @throws Exception
     *
     */
    public boolean verify(String publicKey, String data, String sign) throws Exception;

    /**
     * <P>
     * 私钥解密
     * </p>
     *
     * @param encryptedData
     *            已加密数据
     * @param privateKey
     *            私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public byte[] decryptByPrivateKey(String privateKey, String encryptedData) throws Exception;

//    /**
//     * <p>
//     * 公钥解密
//     * </p>
//     *
//     * @param encryptedData
//     *            已加密数据
//     * @param publicKey
//     *            公钥(BASE64编码)
//     * @return
//     * @throws Exception
//     */
//    public byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception;

    /** */
    /**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data
     *            源数据
     * @param publicKey
     *            公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public String encryptByPublicKey(String publicKey, byte[] data) throws Exception;

//    /**
//     * <p>
//     * 私钥加密
//     * </p>
//     *
//     * @param data
//     *            源数据
//     * @param privateKey
//     *            私钥(BASE64编码)
//     * @return
//     * @throws Exception
//     */
//    public byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception;

    /**
     * <p>
     * 获取私钥
     * </p>
     *
     * @param keyMap
     *            密钥对
     * @return
     * @throws Exception
     */
    public String getPrivateKey(Map<String, Object> keyMap) throws Exception;

    /**
     * <p>
     * 获取公钥
     * </p>
     *
     * @param keyMap
     *            密钥对
     * @return
     * @throws Exception
     */
    public String getPublicKey(Map<String, Object> keyMap) throws Exception;

}
