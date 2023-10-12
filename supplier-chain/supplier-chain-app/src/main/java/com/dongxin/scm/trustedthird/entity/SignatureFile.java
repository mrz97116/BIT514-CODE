package com.dongxin.scm.trustedthird.entity;

import lombok.Data;

@Data
public class SignatureFile {
    private String fileUrl;//签署文件地址
    private String fileName;//签署文件名称
}
