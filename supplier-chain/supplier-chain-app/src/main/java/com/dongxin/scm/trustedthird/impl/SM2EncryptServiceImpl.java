package com.dongxin.scm.trustedthird.impl;


import com.dongxin.scm.trustedthird.sm2.SM2EncDecUtils;
import com.dongxin.scm.trustedthird.utils.HexUtil;
import com.dongxin.scm.trustedthird.utils.ITrustedEncryptService;
import com.dongxin.scm.trustedthird.sm2.SM2EncDecUtils;
import com.dongxin.scm.trustedthird.sm2.SM2SignVerUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SM2EncryptServiceImpl implements ITrustedEncryptService {
    @Override
    public Map<String, String> genKeyPair() throws Exception {
        return SM2EncDecUtils.generateKeyPair();
    }

    @Override
    public String sign( String privateKey, String data) throws Exception {
        return SM2SignVerUtils.Sign2SM2(HexUtil.hexToByte(privateKey),data.getBytes());
    }

    @Override
    public boolean verify(String publicKey,String data,  String sign) throws Exception {
        return SM2SignVerUtils.VerifySignSM2(HexUtil.hexToByte(publicKey),data.getBytes(), HexUtil.hexToByte(sign));
    }

    @Override
    public byte[] decryptByPrivateKey(String privateKey,String encryptedData) throws Exception {
        return SM2EncDecUtils.decrypt(HexUtil.hexToByte(privateKey), HexUtil.hexToByte(encryptedData));
    }

//    @Override
//    public byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {
//        return new byte[0];
//    }

    @Override
    public String encryptByPublicKey(String publicKey,byte[] data ) throws Exception {
        return SM2EncDecUtils.encrypt(HexUtil.hexToByte(publicKey),data);
    }

//    @Override
//    public byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
//        return new byte[0];
//    }

    @Override
    public String getPrivateKey(Map<String, Object> keyMap) throws Exception {
        return null;
    }

    @Override
    public String getPublicKey(Map<String, Object> keyMap) throws Exception {
        return null;
    }

    public static void main(String[] args){
        Map<String,String> keyPair = SM2EncDecUtils.generateKeyPair();
        String signData = "签名数据";
        String encryptData = "加密数据";
        SM2EncryptServiceImpl impl = new SM2EncryptServiceImpl();
        try {
            String encrypted = impl.encryptByPublicKey(keyPair.get(ITrustedEncryptService.PUBLIC_KEY),encryptData.getBytes());
            byte[] decrypt = impl.decryptByPrivateKey(keyPair.get(ITrustedEncryptService.PRIVATE_KEY),encrypted);
            System.out.println("解密数据："+new String(decrypt));

            String signed = impl.sign(keyPair.get(ITrustedEncryptService.PRIVATE_KEY), signData);
            System.out.println("验签结果："+impl.verify(keyPair.get(ITrustedEncryptService.PUBLIC_KEY),signData,signed));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
