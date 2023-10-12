package com.dongxin.scm.sm.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.entity.OldProdCname;
import com.dongxin.scm.sm.entity.OldSystemDataConversion;
import com.dongxin.scm.sm.mapper.OldProdCnameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @Description: 旧系统产品名称
 * @Author: jeecg-boot
 * @Date: 2021-03-02
 * @Version: V1.0
 */
@Service
public class OldProdCnameService extends BaseService<OldProdCnameMapper, OldProdCname> {

    @Autowired
    OldSystemDataConversionService oldSystemDataConversionService;

    public void queryProdName(Mat mat, int count) {

//        mat.setTechnicalStandard(mat.getTechnicalStandard().replace(" ",""));
//        //根据技术标准、产品大类、牌号、材料厚度匹配旧系统产品名称表
//        QueryWrapper<OldProdCname> oldProdCnameQueryWrapper = new QueryWrapper<>();
//
//        oldProdCnameQueryWrapper.lambda().eq(OldProdCname::getSgSign, mat.getSgSign())
//                .eq(OldProdCname::getTechnicalStandard, mat.getTechnicalStandard())
//                .eq(OldProdCname::getProdClassCode, mat.getProdClassCode())
//                .eq(OldProdCname::getCustMatSpecs, BigDecimal.valueOf(mat.getMatThick()));
//
//        List<OldProdCname> oldProdCnameList = list(oldProdCnameQueryWrapper);

        QueryWrapper<OldSystemDataConversion> oldSystemDataConversionQueryWrapper = new QueryWrapper<>();
        oldSystemDataConversionQueryWrapper.lambda().eq(OldSystemDataConversion::getSgSign,mat.getSgSign())
                .eq(OldSystemDataConversion::getProdCname, mat.getProdCname())
                .eq(OldSystemDataConversion::getProdClassCode, mat.getProdClassCode());
        List<OldSystemDataConversion> oldSystemDataConversionList = oldSystemDataConversionService.list(oldSystemDataConversionQueryWrapper);
        if (CollectionUtil.isEmpty(oldSystemDataConversionList)) {
            log.error(StrUtil.format("无法匹配的数据:sgSign:{},ProdCname:{},ProdClassCode:{}",
                    mat.getSgSign(), mat.getProdCname(), mat.getProdClassCode()));
            throw new ScmException(StrUtil.format("第{}条数据匹配失败！请到旧系统产品名称中维护", count));
        }
        mat.setOldProdCname(oldSystemDataConversionList.get(0).getOldProdCname());

//        if (ObjectUtil.isNull(oldProdCnameList.get(0))) {
//            throw new ScmException(StrUtil.format("旧系统产品中文名为空！第{}条数据：请到旧系统产品名称中维护", count));
//        }


    }
}
