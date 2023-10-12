package com.dongxin.scm.trustedthird.entity;

import lombok.Data;

import java.util.List;

@Data
public class SignatureInfo {
     private String theme; //标题
     private String finishDate;//截止时间
     private String signMode;//签署模式 1-并行签署 2-串行签署
     private String sponsor; //发起用户
     private String enterpriseName;//发起企业
     private String isTemplateUse;//是否使用模板 默认为0
     private String templateId;//isTemplateUse传1时,templateId必传
     private String launch;//0-不发起 1-自动发起

     private List<SignatureSigner> sinatrueSignerPushList; //签署人列表

     private List<SignatureFile> sinatrueSignFileList; //签署人文件列表


}
