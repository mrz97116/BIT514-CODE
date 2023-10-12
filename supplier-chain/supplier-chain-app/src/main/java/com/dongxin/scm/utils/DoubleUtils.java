package com.dongxin.scm.utils;

public class DoubleUtils {


    public static String doubleTrans(double d){
        if(Math.round(d)-d==0){
            return String.valueOf((long)d);
        }
        return String.valueOf(d);
    }

}
