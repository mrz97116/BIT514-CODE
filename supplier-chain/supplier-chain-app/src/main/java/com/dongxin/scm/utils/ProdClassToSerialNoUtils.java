package com.dongxin.scm.utils;

import com.dongxin.scm.enums.ProdClassCodeEnum;
import com.dongxin.scm.enums.SerialNoEnum;

public class ProdClassToSerialNoUtils {

    //产品大类开提单
    public static String generateSalesOrderNoWithProdClassPrefix(String prodClassCode) {
        String serialNo = null;
        if (queryWhichProdClass(prodClassCode)) {
            serialNo = SerialNoUtil.getSerialNo(SerialNoEnum.JZ_SALESORDER_NO);
        } else {
            serialNo = SerialNoUtil.getSerialNo(SerialNoEnum.GY_SALESORDER_NO);
        }
        return serialNo;
    }

    //产品大类装车单
    public static String generateStackNoWithProdClassPrefix(String prodClassCode) {
        String serialNo = null;
        if (queryWhichProdClass(prodClassCode)) {
            serialNo = SerialNoUtil.getSerialNo(SerialNoEnum.JZ_STACK_NO);
        } else {
            serialNo = SerialNoUtil.getSerialNo(SerialNoEnum.GY_STACK_NO);
        }
        return serialNo;
    }

    //产品大类结算单
    public static String generateSettleNoWithProdClassPrefix(String prodClassCode) {
        String serialNo = null;
        if (queryWhichProdClass(prodClassCode)) {
            serialNo = SerialNoUtil.getSerialNo(SerialNoEnum.JZ_SETTLE_NO);
        } else {
            serialNo = SerialNoUtil.getSerialNo(SerialNoEnum.GY_SETTLE_NO);
        }
        return serialNo;
    }

    public static String gdGenerateExtendBillNo(String prodClassCode) {
        if (ProdClassCodeEnum.F.getValue().equalsIgnoreCase(prodClassCode)) {
            return SerialNoUtil.getSerialNo(SerialNoEnum.GD_EXTEND_NO_RZ);
        } else if (ProdClassCodeEnum.G.getValue().equalsIgnoreCase(prodClassCode)) {
            return SerialNoUtil.getSerialNo(SerialNoEnum.GD_EXTEND_NO_LZ);
        } else if (ProdClassCodeEnum.H.getValue().equalsIgnoreCase(prodClassCode)) {
            return SerialNoUtil.getSerialNo(SerialNoEnum.GD_EXTEND_NO_ZB);
        }
        return "";
    }



    //判断产品大类
    public static Boolean queryWhichProdClass(String prodClassCode){
        return ProdClassCodeEnum.A.getValue().equals(prodClassCode)
                || ProdClassCodeEnum.B.getValue().equals(prodClassCode)
                || ProdClassCodeEnum.C.getValue().equals(prodClassCode)
                || ProdClassCodeEnum.D.getValue().equals(prodClassCode)
                || ProdClassCodeEnum.E.getValue().equals(prodClassCode);
    }
}
