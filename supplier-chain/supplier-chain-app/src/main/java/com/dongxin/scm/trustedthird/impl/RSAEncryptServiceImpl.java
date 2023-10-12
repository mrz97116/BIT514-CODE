package com.dongxin.scm.trustedthird.impl;

import com.dongxin.scm.trustedthird.rsa.RSAUtil;
import com.dongxin.scm.trustedthird.utils.ITrustedEncryptService;
import com.dongxin.scm.trustedthird.rsa.RSAUtil;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RSAEncryptServiceImpl implements ITrustedEncryptService {

    @Override
    public Map<String, String> genKeyPair() throws Exception {
        return RSAUtil.genKeyPair();
    }

    @Override
    public String sign(String privateKey, String data) throws Exception {
        return  RSAUtil.sign(data.getBytes(),privateKey);
    }

    @Override
    public boolean verify(String publicKey, String data, String sign) throws Exception {
        return RSAUtil.verify(data.getBytes(),publicKey,sign);
    }

    @Override
    public byte[] decryptByPrivateKey(String privateKey, String encryptedData) throws Exception {

        return RSAUtil.decryptByPrivateKey(Base64.decodeBase64(encryptedData),privateKey);
    }

    @Override
    public String encryptByPublicKey(String publicKey, byte[] data) throws Exception {

        return Base64.encodeBase64String(RSAUtil.encryptByPublicKey(data,publicKey));
    }

    @Override
    public String getPrivateKey(Map<String, Object> keyMap) throws Exception {
        return null;
    }

    @Override
    public String getPublicKey(Map<String, Object> keyMap) throws Exception {
        return null;
    }

    public static void main(String[] args){
//        Map<String,String> keyPair = SM2EncDecUtils.generateKeyPair();
        String signData = "签名数据";
        String encryptData = "加密数据";
        RSAEncryptServiceImpl rr = new RSAEncryptServiceImpl();

        try {
            Map<String,String> keyPair = rr.genKeyPair();
            String encrypted = rr.encryptByPublicKey(keyPair.get(ITrustedEncryptService.PUBLIC_KEY),encryptData.getBytes());
            byte[] decrypt = rr.decryptByPrivateKey(keyPair.get(ITrustedEncryptService.PRIVATE_KEY),encrypted);
            System.out.println("解密数据："+new String(decrypt));

            String signed = rr.sign(keyPair.get(ITrustedEncryptService.PRIVATE_KEY), signData);
            System.out.println("验签结果："+rr.verify(keyPair.get(ITrustedEncryptService.PUBLIC_KEY),signData,signed));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
