package com.dongxin.scm.trustedthird.entity;

import lombok.Data;

@Data
public class SignatureSigner {

    private String identity;//身份证号
    private String enterpriseName;//签署人所在的公司
    private String signerName;//签署人姓名
    private String phoneNumber;//签署人电话

}
