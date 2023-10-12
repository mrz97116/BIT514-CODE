package com.dongxin.scm.wms;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.Application;
import com.dongxin.scm.sm.entity.LogisticsParkMatBasicInformation;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.service.LogisticsParkMatBasicInformationService;
import com.dongxin.scm.sm.service.MatService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class WMSBasicInformationTest {

    @Autowired
    private LogisticsParkMatBasicInformationService logisticsParkMatBasicInformationService;

    @Autowired
    private MatService matService;


    @Test
    @Transactional(rollbackFor = Exception.class)
    public void updateBasicInformation() {

        QueryWrapper<LogisticsParkMatBasicInformation> logisticsParkMatBasicInformationQueryWrapper = new QueryWrapper<>();
        logisticsParkMatBasicInformationQueryWrapper.lambda().eq(LogisticsParkMatBasicInformation::getTenantId, 2);
        List<LogisticsParkMatBasicInformation> logisticsParkMatBasicInformationList = logisticsParkMatBasicInformationService.list(logisticsParkMatBasicInformationQueryWrapper);

        int count = 0;
        for (LogisticsParkMatBasicInformation logisticsParkMatBasicInformation : logisticsParkMatBasicInformationList) {

            QueryWrapper<Mat> matQueryWrapper = new QueryWrapper<>();
            matQueryWrapper.lambda().ne(Mat::getOldProdCname , "抗震螺纹钢（新标）")
                    .eq(Mat::getSgSign, logisticsParkMatBasicInformation.getSteelGradeName())
                    .eq(Mat::getMatThick, logisticsParkMatBasicInformation.getThick())
                    .eq(Mat::getMatWidth, logisticsParkMatBasicInformation.getWidth())
                    .eq(Mat::getMatLen, logisticsParkMatBasicInformation.getLength())
                    .eq(Mat::getTenantId, 2)
                    .eq(Mat::getStockHouseId,"1")
                    //物流园理重0，实重1.
                    .eq(Mat::getWtMode, Math.abs(logisticsParkMatBasicInformation.getWeightMode() - 1))
                    .in(Mat::getProdClassCode, "B", "D");


            List<Mat> matList = matService.list(matQueryWrapper);
            if (ObjectUtil.isEmpty(matList)) {
                continue;
            }
            String id = matList.get(0).getId();

            Mat mat = matService.getById(id);

            logisticsParkMatBasicInformation.setSysProductName(mat.getOldProdCname())
                    .setSysLength(BigDecimal.valueOf(mat.getMatLen()))
                    .setSysWidth(BigDecimal.valueOf(mat.getMatWidth()))
                    .setSysThick(BigDecimal.valueOf(mat.getMatThick()))
                    .setSysSgSign(mat.getSgSign());

            logisticsParkMatBasicInformationService.updateById(logisticsParkMatBasicInformation);
            count++;

        }
        System.out.println(count + "============================================================================");

    }


}
