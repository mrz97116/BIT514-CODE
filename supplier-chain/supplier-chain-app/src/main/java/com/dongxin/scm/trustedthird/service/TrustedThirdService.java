package com.dongxin.scm.trustedthird.service;

import ch.qos.logback.core.spi.ScanException;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.bd.service.TenantReportService;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.om.util.TenantText;
import com.dongxin.scm.sm.entity.Receiving;
import com.dongxin.scm.sm.service.ReceivingService;
import com.dongxin.scm.trustedthird.entity.SignatureFile;
import com.dongxin.scm.trustedthird.entity.SignatureInfo;
import com.dongxin.scm.trustedthird.entity.SignatureSigner;
import com.dongxin.scm.trustedthird.utils.Result;
import com.dongxin.scm.trustedthird.utils.SendHttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.apache.poi.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.CommonUtils;
import org.jeecg.config.mybatis.TenantContext;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.system.entity.SysTenant;
import org.jeecg.modules.system.mapper.SysTenantMapper;
import org.jeecg.modules.system.service.impl.SysTenantServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;


import javax.annotation.Resource;
import java.io.*;
import java.util.*;

@Service
@Slf4j
public class TrustedThirdService {

    @Autowired
    TenantReportService tenantReportService;

    @Autowired
    SalesOrderService salesOrderService;

    @Autowired
    ReceivingService receivingService;

    @Autowired
    BaseCommonService baseCommonService;
    @Autowired
    SysTenantServiceImpl sysTenantService;
    @Autowired
    CustomerProfileService customerProfileService;

    @Value("${jeecg.path.upload}")
    private String uploadpath;

    /**
     * 本地：local minio：minio 阿里：alioss
     */
    @Value(value = "${jeecg.uploadType}")
    private String uploadType;

    @Value("${trusted-third.key.publicKey}")
    private String publicKey;

    @Value("${trusted-third.key.privateKey}")
    private String privateKey;

    @Value("${trusted-third.client-name}")
    private String clientName;

    @Value("${trusted-third.api.pushApi}")
    private String pushApi;

    @Value("${trusted-third.api.viewApi}")
    private String viewApi;

    @Value("${trusted-third.api.templateApi}")
    private String templateApi;

    @Resource
    SysTenantMapper sysTenantMapper;

    private static final String PDF_SUFFIX = ".pdf";

    /**
     * 发起签收单可信数据签署
     *
     * @param params
     * @param tenantId
     * @throws Exception
     */
    public void signInOrderSendPdf(String params, String tenantId) throws Exception {
//        String tenantId = TenantContext.getTenant();
//        String reportName = tenantReportService.queryCompanyReportName(Integer.getInteger(tenantId));
        //替换 报表路径
        String reportUrl = "http://172.16.5.88/webroot/decision/view/report?viewlet=SMS_ZX%252FSM%252FYRM%252FsignInOrder.cpt";

        String relUrl = StrUtil.format("{}{}{}{}{}{}", reportUrl, "&tenant_id=", tenantId, "&id=", params, "&format=pdf");

        //获取帆软报表对应的pdf
        HttpRequest req = HttpUtil.createGet(relUrl);
        HttpResponse response = req
                .contentType("application/json")
                .execute();
        //file 转换成MultipartFile
        DiskFileItem fileItem = (DiskFileItem) new DiskFileItemFactory().createItem("file",
                MediaType.ALL_VALUE, true, params);

        try (InputStream input = response.bodyStream(); OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copy(input, os);

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file: " + e, e);
        }

        MultipartFile multi = new CommonsMultipartFile(fileItem);

        //上传pdf至文件服务器
        String savePath = CommonUtils.upload(multi, tenantId, uploadType);

        //向可信数据发起签署
        reqTrustedThird(savePath, tenantId, params);

    }

    /**
     * 发起签署
     *
     * @param savePath
     * @param tenantId
     * @param params
     * @throws Exception
     */
    public void reqTrustedThird(String savePath, String tenantId, String params) throws Exception {
        //获取当前操作公司名称
        SysTenant sysTenant = sysTenantService.getById(tenantId);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SalesOrder salesOrder = salesOrderService.getBySalesBillNo(params);
        CustomerProfile customerProfile = customerProfileService.getById(salesOrder.getCustomerId());
        String enterpriseName = sysTenant.getName();
        SignatureInfo signatureInfo = new SignatureInfo();
        signatureInfo.setTheme(StrUtil.format("{} {}", customerProfile.getCompanyName(), params));
        String finisData = DateUtil.format(DateUtil.nextMonth(), "YYYY-MM-dd");
        signatureInfo.setFinishDate(finisData);
        signatureInfo.setSignMode("1");
        signatureInfo.setSponsor(sysUser.getRealname());
        signatureInfo.setEnterpriseName(enterpriseName);
        signatureInfo.setIsTemplateUse("0");
        signatureInfo.setTemplateId("");
        signatureInfo.setLaunch("1");

        List<SignatureSigner> signatureSigners = new ArrayList<>();
        SignatureSigner signatureSigner = new SignatureSigner();
        signatureSigner.setEnterpriseName("");
        //身份证号 ---必填
        //依据收料人名称查询签署人信息
        Receiving receiving = receivingService.queryReceiving(salesOrder.getReceivingName());
        signatureSigner.setIdentity(receiving.getIdCard());
        signatureSigner.setSignerName(receiving.getReceivingName());
        signatureSigner.setPhoneNumber(receiving.getPhone());
//        signatureSigner.setIdentity("450681199705270330");
//        signatureSigner.setSignerName("沈其胜");
//        signatureSigner.setPhoneNumber("13558205144");

//        signatureSigner.setIdentity("421281199009214937");
//        signatureSigner.setSignerName("张良");
//        signatureSigner.setPhoneNumber("18077228969");

        signatureSigners.add(signatureSigner);

        signatureInfo.setSinatrueSignerPushList(signatureSigners);

        List<SignatureFile> signatureFiles = new ArrayList<>();
        SignatureFile signatureFile = new SignatureFile();
        signatureFile.setFileUrl(savePath);
        signatureFile.setFileName(params);
        signatureFiles.add(signatureFile);

        signatureInfo.setSinatrueSignFileList(signatureFiles);

        String signatureInfoString = JSONObject.toJSONString(signatureInfo);
        JSONObject signatureInfoStringObject = JSONObject.parseObject(signatureInfoString);

        Result<JSONObject> jsonObjectResult = SendHttpRequestUtil.doPost(pushApi, signatureInfoStringObject,
                privateKey, clientName);

        if (200 != jsonObjectResult.getCode()) {
            throw new ScmException(jsonObjectResult.getMessage());
        }

        //提单记录signatureId
        Map<String, Object> map = JSONObject.parseObject(String.valueOf(jsonObjectResult.getResult()));
        salesOrder.setSignatureId(map.get("signatureId").toString());
        salesOrderService.updateById(salesOrder);
    }

    /**
     * 查看作准文件
     *
     * @param saleBillNo
     * @return
     * @throws Exception
     */
    public String viewTrustedThird(String saleBillNo) throws Exception {
        //查询提单发起签署时获取到的signatureId;
        SalesOrder salesOrder = salesOrderService.getBySalesBillNo(saleBillNo);
        if (StrUtil.isBlank(salesOrder.getSignatureId())) {
            throw new ScmException("未发起签署");
        }

        String fileUrl;

        Map<String, Object> params = new HashMap<>();
        params.put("signatureId", salesOrder.getSignatureId());
        //请求作准文件
        Result<JSONObject> jsonObjectResult = SendHttpRequestUtil.doGet(viewApi, params, privateKey, clientName);
        if (!jsonObjectResult.isSuccess()) {
            throw new ScmException(jsonObjectResult.getMessage());
        }
        List<Map<String, Object>> list = JSONObject.parseObject(JSON.toJSONString(jsonObjectResult.getResult()), List.class);
        fileUrl = list.get(0).get("finalFileUrl").toString();

        return fileUrl;
    }

//    public List<SignatureTemplate> getSignatureTemplateList() throws Exception {
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        QueryWrapper<SysTenant> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().eq(SysTenant::getId, TenantContext.getTenant());
//        List<SysTenant> tenantList = sysTenantMapper.selectList(queryWrapper);
//        String enterpriseName = tenantList.get(0).getName();
//        Map<String, Object> params = new HashMap<>();
//        params.put("identity", "450681199705270330");
//        params.put("enterpriseName", enterpriseName);
//        Result<JSONObject> jsonObjectResult = SendHttpRequestUtil.doGet(templateApi, params ,privateKey , clientName);
//        System.out.println(jsonObjectResult);
//        List<SignatureTemplate> list = JSON.parseArray(jsonObjectResult.getResult().toString(), SignatureTemplate.class);
//        return list;
//    }

}
