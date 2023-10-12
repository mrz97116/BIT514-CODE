package com.dongxin.scm.trustedthird.sm2;


import com.dongxin.scm.trustedthird.utils.HexUtil;
import com.dongxin.scm.trustedthird.utils.ITrustedEncryptService;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class SM2EncDecUtils {
    //生成随机秘钥对
    public static Map<String,String> generateKeyPair(){
        SM2 sm2 = SM2.Instance();
        AsymmetricCipherKeyPair key = null;
        while (true){
            key=sm2.ecc_key_pair_generator.generateKeyPair();
            if(((ECPrivateKeyParameters) key.getPrivate()).getD().toByteArray().length==32){
                break;
            }
        }
        ECPrivateKeyParameters ecpriv = (ECPrivateKeyParameters) key.getPrivate();
        ECPublicKeyParameters ecpub = (ECPublicKeyParameters) key.getPublic();
        BigInteger privateKey = ecpriv.getD();
        ECPoint publicKey = ecpub.getQ();
//        SM2KeyVO sm2KeyVO = new SM2KeyVO();
//        sm2KeyVO.setPublicKey(publicKey);
//        sm2KeyVO.setPrivateKey(privateKey);
        //System.out.println("公钥: " + HexUtil.byteToHex(publicKey.getEncoded()));
        //System.out.println("私钥: " + HexUtil.byteToHex(privateKey.toByteArray()));
        Map<String,String> map = new HashMap<>();
        map.put(ITrustedEncryptService.PUBLIC_KEY, HexUtil.byteToHex(publicKey.getEncoded()));
        map.put(ITrustedEncryptService.PRIVATE_KEY, HexUtil.byteToHex(privateKey.toByteArray()));
        return map;
    }

    //数据加密
    public static String encrypt(byte[] publicKey, byte[] data) throws IOException
    {
        if (publicKey == null || publicKey.length == 0)
        {
            return null;
        }

        if (data == null || data.length == 0)
        {
            return null;
        }

        byte[] source = new byte[data.length];
        System.arraycopy(data, 0, source, 0, data.length);

        Cipher cipher = new Cipher();
        SM2 sm2 = SM2.Instance();
        ECPoint userKey = sm2.ecc_curve.decodePoint(publicKey);

        ECPoint c1 = cipher.Init_enc(sm2, userKey);
        cipher.Encrypt(source);
        byte[] c3 = new byte[32];
        cipher.Dofinal(c3);

//      System.out.println("C1 " + HexUtil.byteToHex(c1.getEncoded()));
//      System.out.println("C2 " + HexUtil.byteToHex(source));
//      System.out.println("C3 " + HexUtil.byteToHex(c3));
        //C1 C2 C3拼装成加密字串
        // C1 | C2 | C3
        //return HexUtil.byteToHex(c1.getEncoded()) + HexUtil.byteToHex(source) + HexUtil.byteToHex(c3);
        // C1 | C3 | C2
        return HexUtil.byteToHex(c1.getEncoded()) + HexUtil.byteToHex(c3) + HexUtil.byteToHex(source);
    }

    //数据解密
    public static byte[] decrypt(byte[] privateKey, byte[] encryptedData) throws IOException
    {
        if (privateKey == null || privateKey.length == 0)
        {
            return null;
        }

        if (encryptedData == null || encryptedData.length == 0)
        {
            return null;
        }
        //加密字节数组转换为十六进制的字符串 长度变为encryptedData.length * 2
        String data = HexUtil.byteToHex(encryptedData);
        /***分解加密字串 C1 | C2 | C3
         * （C1 = C1标志位2位 + C1实体部分128位 = 130）
         * （C3 = C3实体部分64位  = 64）
         * （C2 = encryptedData.length * 2 - C1长度  - C2长度）

        byte[] c1Bytes = HexUtil.hexToByte(data.substring(0,130));
        int c2Len = encryptedData.length - 97;
        byte[] c2 = HexUtil.hexToByte(data.substring(130,130 + 2 * c2Len));
        byte[] c3 = HexUtil.hexToByte(data.substring(130 + 2 * c2Len,194 + 2 * c2Len));
        */
        /***分解加密字串 C1 | C3 | C2
         * （C1 = C1标志位2位 + C1实体部分128位 = 130）
         * （C3 = C3实体部分64位  = 64）
         * （C2 = encryptedData.length * 2 - C1长度  - C2长度）
         */
        byte[] c1Bytes = HexUtil.hexToByte(data.substring(0,130));
        int c2Len = encryptedData.length - 97;
        byte[] c3 = HexUtil.hexToByte(data.substring(130,130 + 64));
        byte[] c2 = HexUtil.hexToByte(data.substring(194,194 + 2 * c2Len));

        SM2 sm2 = SM2.Instance();
        BigInteger userD = new BigInteger(1, privateKey);

        //通过C1实体字节来生成ECPoint
        ECPoint c1 = sm2.ecc_curve.decodePoint(c1Bytes);
        Cipher cipher = new Cipher();
        cipher.Init_dec(userD, c1);
        cipher.Decrypt(c2);
        cipher.Dofinal(c3);

        //返回解密结果
        return c2;
    }

/*    public static BigInteger[] Sm2Sign(byte[] md, AsymmetricCipherKeyPair keypair)
    {
        SM3Digest sm3 = new SM3Digest();

        ECPublicKeyParameters ecpub = (ECPublicKeyParameters)keypair.getPublic();

        byte[] z = SM2CryptoServiceProvider.Sm2GetZ(Encoding.Default.GetBytes(SM2CryptoServiceProvider.userId), ecpub.getQ());
        sm3.update(z, 0, z.length);

        byte[] p = md;
        sm3.update(p, 0, p.length);

        byte[] hashData = new byte[32];
        sm3.doFinal(hashData, 0);

        // e
        BigInteger e = new BigInteger(1, hashData);
        // k
        BigInteger k = null;
        ECPoint kp = null;
        BigInteger r = null;
        BigInteger s = null;
        BigInteger userD = null;

        do
        {
            do
            {

                ECPrivateKeyParameters ecpriv = (ECPrivateKeyParameters)keypair.getPrivate();
                k = ecpriv.getD();
                kp = ecpub.getQ();

                userD = ecpriv.getD();

                // r
                r = e.add(kp.getX().toBigInteger());
                r = r.mod(ecc_n);
            }
            while (r.equals(BigInteger.ZERO) || r.add(k).equals(ecc_n));

            // (1 + dA)~-1
            BigInteger da_1 = userD.add(BigInteger.ONE);
            da_1 = da_1.modInverse(ecc_n);
            // s
            s = r.multiply(userD);
            s = k.subtract(s).mod(ecc_n);
            s = da_1.multiply(s).mod(ecc_n);
        }
        while (s.equals(BigInteger.ZERO));

        byte[] btRS = new byte[64];
        byte[] btR = r.toByteArray();
        byte[] btS = s.toByteArray();
        Array.Copy(btR, btR.length - 32, btRS, 0, 32);
        Array.Copy(btS, btS.length - 32, btRS, 32, 32);

        return new BigInteger[] { r, s };
    }*/

    public static void main(String[] args) throws Exception
    {
        String plainText = "ILoveYou11";
        //SM3测试
        //生成密钥对
        //generateKeyPair();
        byte[] sourceData = plainText.getBytes();

        //下面的秘钥可以使用generateKeyPair()生成的秘钥内容
        // 国密规范正式私钥
        //String prik = "3690655E33D5EA3D9A4AE1A1ADD766FDEA045CDEAA43A9206FB8C430CEFE0D94";
        // 国密规范正式公钥
        //String pubk = "04F6E0C3345AE42B51E06BF50B98834988D54EBC7460FE135A48171BC0629EAE205EEDE253A530608178A98F1E19BB737302813BA39ED3FA3C51639D7A20C7391A";

        String prik = "4cf170068e9c47ebdb521fb9fc62c4a55a5773fb9da33b0acf8129e28d09d205";
        String pubk = "04aabda53043e8dcb86d42f690b61a4db869821dadf9f851ec3c5c43d0c8f95a6677fdba984afc3bb010a8436b1d17cefc2011a34e01e9e801124d29ffa928d803";
        String publicKey ="04BB34D657EE7E8490E66EF577E6B3CEA28B739511E787FB4F71B7F38F241D87F18A5A93DF74E90FF94F4EB907F271A36B295B851F971DA5418F4915E2C1A23D6E";
        String privatekey = "0B1CE43098BC21B8E82B5C065EDB534CB86532B1900A49D49F3C53762D2997FA";
        prik=privatekey;
        pubk=publicKey;
        System.out.println("加密: ");
        String cipherText = SM2EncDecUtils.encrypt(HexUtil.hexToByte(pubk), sourceData);
        //cipherText = "0452ba81cf5119c9f29c81c2be9c4a49ad8c0a33ed899b60548d21a62971a8e994cafc0e9fbc710a0a220b055804bb890833b50ac04ec4e130a5db75338c0c1d49a52a6d373076a5db370564a5cebb5300f79877003c52adf49dac16370e51e14e0754110547bb3b";
        System.out.println(cipherText);
        System.out.println("解密: ");
//        plainText = new String(SM2EncDecUtils.decrypt(HexUtil.hexToByte(prik), HexUtil.hexToByte(cipherText)));
        plainText = new String(SM2EncDecUtils.decrypt(HexUtil.hexToByte(prik), HexUtil.hexToByte("e7d2ad4e5b46357d825a389b3e5f942c0d4ffd3643d07f304d19b74e284ab94e8cdb4cc6e564a7aa6274d63fb01a5ffe9f5dae4b0a224386df161b13c2f8328774fcc44c46bc22dcd3a4709f889e112dc2c01ef1c535f6eed7cf29b2c6a439b31a7611e98bc122309859f3b07a7ff433")));
        System.out.println(plainText);

    }
}
