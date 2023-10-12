package com.dongxin.scm.trustedthird.utils;


import com.dongxin.scm.trustedthird.impl.ECCEncryptServiceImpl;
import com.dongxin.scm.trustedthird.impl.RSAEncryptServiceImpl;
import com.dongxin.scm.trustedthird.impl.SM2EncryptServiceImpl;

public class EncryptFactory {

    public static ITrustedEncryptService getEncrypt(Integer type){

        switch (type){
            case 1:
                return new RSAEncryptServiceImpl();
            case 2:
                return new SM2EncryptServiceImpl();
            case 3:
                return new ECCEncryptServiceImpl();
            default:
                return null;
        }
    }

}
