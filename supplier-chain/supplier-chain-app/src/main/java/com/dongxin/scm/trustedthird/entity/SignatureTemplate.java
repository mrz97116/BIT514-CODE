package com.dongxin.scm.trustedthird.entity;

import lombok.Data;

@Data
public class SignatureTemplate {

    String id;
    String createBy;
    String ver;
    Integer signerCount;
    String createTime;
    String templateName;
    Integer tenantId;
    String enterpriseId;
    Integer delFlag;

}
