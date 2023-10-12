package com.dongxin.scm.sm.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.mapper.ProcessMapper;
import com.dongxin.scm.sm.vo.ProcessPage;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 物料信息表
 * @Author: jeecg-boot
 * @Date: 2020-11-18
 * @Version: V1.0
 */
@Service
public class ProcessService extends BaseService<ProcessMapper, Mat> {

    @Autowired
    MatService matService;


    //废弃方法
    @Transactional(rollbackFor = Exception.class)
    @Deprecated
    public boolean addPocessMat(ProcessPage processPage) {
        //查询加工完成的材料是否有库存
        for (Mat processMat : processPage.getMatDetList()) {
            if (processMat.getMatThick() <= 0) {
                throw new ScmException("材料厚度必须大于0!");
            }
            Mat searchMat = new Mat();
            //中文品名、别名、产品大类、长、宽、厚、原材料号
            searchMat.setProdCname(processMat.getProdCname()).setProdCnameOther(processMat.getProdCnameOther())
                    .setProdClassCode(processMat.getProdClassCode()).setMatLen(processMat.getMatLen())
                    .setMatWidth(processMat.getMatWidth()).setMatThick(processMat.getMatThick())
                    .setRawMaterialNo(processPage.getRawMaterialNo());
            QueryWrapper<Mat> queryWrapper = QueryGenerator.initQueryWrapper(searchMat, null);
            List<Mat> existMatList = baseMapper.selectList(queryWrapper);
            //判断加工完成的材料是否有库存
            if (!existMatList.isEmpty()) {
                //有,只对第一条记录进行操作
                for (Mat existMat : existMatList) {
                    UpdateWrapper<Mat> updateWrapper = new UpdateWrapper();
                    //有,增加库存量、可销售重量
                    if (BeanUtil.isEmpty(existMat.getMatNetWt())) existMat.setMatNetWt(0.0);
                    if (BeanUtil.isEmpty(existMat.getAvailableWeight())) existMat.setAvailableWeight(0.0);
                    existMat.setMatNetWt(existMat.getMatNetWt() + processMat.getMatNetWt());
                    existMat.setAvailableWeight(existMat.getAvailableWeight() + processMat.getMatNetWt());
                    updateWrapper.lambda().set(Mat::getMatNetWt, existMat.getMatNetWt()).set(Mat::getAvailableWeight, existMat.getAvailableWeight())
                            .eq(Mat::getId, existMat.getId());
                    update(updateWrapper);
                    break;
                }
            } else {
                //无，新增加工完成的材料
                processMat.setRawMaterialNo(processPage.getRawMaterialNo());
                processMat.setId(null);
                save(processMat);
            }
        }
        //删除需要加工的材料
        matService.removeById(processPage.getMatId());
        return true;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(Mat entity) {
        if (entity.getMatThick() <= 0) {
            throw new ScmException("材料厚度必须大于0!");
        }
        return super.updateById(entity);
    }
}
