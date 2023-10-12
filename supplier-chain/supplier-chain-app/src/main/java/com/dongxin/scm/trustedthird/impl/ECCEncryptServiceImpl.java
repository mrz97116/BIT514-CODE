package com.dongxin.scm.trustedthird.impl;

import com.dongxin.scm.trustedthird.utils.ITrustedEncryptService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ECCEncryptServiceImpl implements ITrustedEncryptService {


    @Override
    public Map<String, String> genKeyPair() throws Exception {
        return null;
    }

    @Override
    public String sign(String privateKey, String data) throws Exception {
        return null;
    }

    @Override
    public boolean verify(String publicKey, String data, String sign) throws Exception {
        return false;
    }

    @Override
    public byte[] decryptByPrivateKey(String privateKey, String encryptedData) throws Exception {
        return new byte[0];
    }

    @Override
    public String encryptByPublicKey(String publicKey, byte[] data) throws Exception {
        return null;
    }

    @Override
    public String getPrivateKey(Map<String, Object> keyMap) throws Exception {
        return null;
    }

    @Override
    public String getPublicKey(Map<String, Object> keyMap) throws Exception {
        return null;
    }
}
