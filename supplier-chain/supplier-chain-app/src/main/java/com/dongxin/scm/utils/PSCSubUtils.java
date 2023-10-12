package com.dongxin.scm.utils;

import cn.hutool.core.util.StrUtil;

public class PSCSubUtils {

    //获取产品规范码的第一位 返回产品大类代码
    public static String pscSub(String psc) {
        if (StrUtil.isBlank(psc)) {
            return "";
        }
        return psc.substring(0, 1);
    }
}
