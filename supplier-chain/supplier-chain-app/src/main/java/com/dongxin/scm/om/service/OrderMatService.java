package com.dongxin.scm.om.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.om.entity.OrderMat;
import com.dongxin.scm.om.mapper.OrderMatMapper;
import com.dongxin.scm.sm.entity.Mat;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 订单物料信息表
 * @Author: jeecg-boot
 * @Date: 2020-12-01
 * @Version: V1.0
 */
@Service
public class OrderMatService extends BaseService<OrderMatMapper, OrderMat> {

    @Resource
    private OrderMatMapper orderMatMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(OrderMat entity) {
        if (entity.getMatThick() <= 0 && !StrUtil.contains(entity.getSgSign().trim(), "混")) {
            if (!StrUtil.equals(entity.getProdCname(), "热轧槽钢")) {
                throw new ScmException("材料厚度必须大于0!");
            }

        }
        OrderMat queryOrderMat = new OrderMat();
        queryOrderMat.setProdCname(entity.getProdCname())
                .setProdCnameOther(entity.getProdCnameOther())
                .setProdClassCode(entity.getProdClassCode())
                .setMatKind(entity.getMatKind())
                .setSgSign(entity.getSgSign())
                .setWtMethodCode(entity.getWtMethodCode())
                .setMatWidth(entity.getMatWidth())
                .setMatThick(entity.getMatThick())
                .setMatLen(entity.getMatLen());
        //热板
        if (entity.getProdCname().equals("热轧开平板") && entity.getPieceWeight() == null
                && entity.getMatLen() != null && entity.getMatWidth() != null) {
            Double pieceWight = queryOrderMat.getMatThick() * (queryOrderMat.getMatWidth() / 1000) * (queryOrderMat.getMatLen() / 1000) * 7.85 / 1000;
            BigDecimal bigDecimal = new BigDecimal(pieceWight);
            Double pieceWight1 = bigDecimal.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
            entity.setPieceWeight(pieceWight1);
        } else {
            queryOrderMat.setProdCnameOther(entity.getProdCnameOther());
        }
        QueryWrapper queryWrapper = QueryGenerator.initQueryWrapper(queryOrderMat, null);
        List exist = orderMatMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(exist)) {
            return super.save(entity);
        } else {
            throw new ScmException("已存在相同规格的材料!");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(OrderMat entity) {
        if (entity.getMatThick() <= 0) {
            throw new ScmException("材料厚度必须大于0!");
        }
        return super.updateById(entity);
    }

    /**
     * 材料入库，添加物料基础信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOrderMat(Mat mat) {
        OrderMat queryOrderMat = new OrderMat();

        queryOrderMat.setProdCname(mat.getProdCname())
                .setProdCnameOther(mat.getProdCnameOther())
                .setProdClassCode(mat.getProdClassCode())
                .setMatKind(mat.getMatKind())
                .setSgSign(mat.getSgSign())
                .setWtMethodCode(mat.getWtMode())
                .setMatWidth(mat.getMatWidth())
                .setMatThick(mat.getMatThick());

        QueryWrapper queryWrapper = QueryGenerator.initQueryWrapper(queryOrderMat, null);
        List exist = orderMatMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(exist)) {
            queryOrderMat.setMatLen(mat.getMatLen())
                    .setMatWidth(mat.getMatWidth())
                    .setMatThick(mat.getMatThick())
                    .setWeight(mat.getMatNetWt())
                    .setPrice(BigDecimal.ZERO)
                    .setPieceWeight(mat.getPieceWeight());
            save(queryOrderMat);
        }

    }
}
