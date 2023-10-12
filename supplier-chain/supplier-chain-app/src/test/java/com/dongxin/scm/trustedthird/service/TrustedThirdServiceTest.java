package com.dongxin.scm.trustedthird.service;

import com.alibaba.fastjson.JSONObject;
import com.dongxin.scm.Application;
import com.dongxin.scm.trustedthird.entity.*;
import com.dongxin.scm.trustedthird.utils.Result;
import com.dongxin.scm.trustedthird.utils.SendHttpRequestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class TrustedThirdServiceTest {

    @Autowired
    TrustedThirdService trustedThirdService;

    @Value("${trusted-third.key.publicKey}")
    private String publicKey;

    @Value("${trusted-third.key.privateKey}")
    private String privateKey;

    @Value("${trusted-third.client-name}")
    private String clientName;

    @Value("${trusted-third.api.templateApi}")
    private String api;

    @Value("${trusted-third.api.templateApi}")
    private String templateApi;



    @Test
    public void t () {
        SignatureInfo signatureInfo = new SignatureInfo();
        signatureInfo.setTheme("第三方签署");
        signatureInfo.setFinishDate("2021-8-1");
        signatureInfo.setSignMode("1");
        signatureInfo.setEnterpriseName("广西柳钢东信科技有限公司");
        signatureInfo.setIsTemplateUse("0");

        List<SignatureSigner> signatureSigners = new ArrayList<>();
        SignatureSigner signatureSigner = new SignatureSigner();
        signatureSigner.setEnterpriseName("广西柳钢东信科技有限公司");
        signatureSigner.setIdentity("452227199310223312");
        signatureSigners.add(signatureSigner);

        signatureInfo.setSinatrueSignerPushList(signatureSigners);

        List<SignatureFile> signatureFiles = new ArrayList<>();
        SignatureFile signatureFile = new SignatureFile();
        signatureFile.setFileUrl("/group1/trusted/dev/temp/工商快照.pdf");
        signatureFile.setFileName("工商快照.pdf");
        signatureFiles.add(signatureFile);

        signatureInfo.setSinatrueSignFileList(signatureFiles);

        String signatureInfoString = JSONObject.toJSONString(signatureInfo);
        JSONObject signatureInfoStringObject = JSONObject.parseObject(signatureInfoString);

        try {
            Result<JSONObject> jsonObjectResult = SendHttpRequestUtil.doPost(api, signatureInfoStringObject,
                    privateKey,clientName);
            System.out.println(jsonObjectResult.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void signInOrderSendPdf() throws Exception {
        trustedThirdService.signInOrderSendPdf("ZC202105270005", "6");
    }

    @Test
    public void ttt()throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("identity", "450681199705270330");
        params.put("enterpriseName", "广西柳钢东信科技有限公司");
        Result<JSONObject> jsonObjectResult = SendHttpRequestUtil.doGet(templateApi, params ,privateKey , clientName);
        System.out.println(jsonObjectResult);
    }
}
