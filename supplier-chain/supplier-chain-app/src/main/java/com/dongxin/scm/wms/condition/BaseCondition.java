package com.dongxin.scm.wms.condition;

import cn.hutool.json.JSONUtil;

public class BaseCondition {

    public String toJson() {
        return JSONUtil.toJsonStr(this);
    }
}

