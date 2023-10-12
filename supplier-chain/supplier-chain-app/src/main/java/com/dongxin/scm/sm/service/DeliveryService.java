package com.dongxin.scm.sm.service;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.om.entity.SalesOrderDet;
import com.dongxin.scm.om.service.SalesOrderDetService;
import com.dongxin.scm.sm.dto.SalesOrderDTO;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.mapper.DeliveryMapper;
import com.dongxin.scm.sm.vo.SalesOrderVO;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 仓库信息表
 * @Author: jeecg-boot
 * @Date: 2020-09-17
 * @Version: V1.0
 */
@Service
public class DeliveryService extends BaseService<DeliveryMapper, SalesOrderDTO> {

    @Autowired
    private MatService matService;
    @Autowired
    private SalesOrderDetService salesOrderDetService;
    @Autowired
    private CustomerProfileService customerProfileService;

    /*
    查询销售单
     */
    public List<SalesOrderVO> queryPage(Page<SalesOrderVO> page, SalesOrderVO salesOrderVO) {
        List<SalesOrderDTO> salesOrderDtoList = baseMapper.querySalesOrderDTOList(page, salesOrderVO);
        List<SalesOrderVO> salesOrderVos = new ArrayList<>();
        for (SalesOrderDTO salesOrderDto : salesOrderDtoList) {
            SalesOrderVO salesOrderVo = new SalesOrderVO();
            BeanUtil.copyProperties(salesOrderDto, salesOrderVo);
            salesOrderVos.add(salesOrderVo);
        }

        //显示顾客名称
        List<String> customerIds = newArrayList();
        for (SalesOrderVO record : salesOrderVos) {
            customerIds.add(record.getCustomerId());
        }
        Map<String, String> customerName = customerProfileService.getNameMap(customerIds);
        for (SalesOrderVO record : salesOrderVos) {
            record.setCustomerId(customerName.get(record.getCustomerId()));
        }

        return salesOrderVos;
    }

    /*
    根据销售单匹配材料信息
     */
    public List<Mat> queryMatList(String id) {
        SalesOrderDet salesOrderDet = salesOrderDetService.getBaseMapper().selectById(id);
        Mat mat = new Mat();
        mat.setProdClassCode(salesOrderDet.getProdClassCode());
        mat.setMatLen(salesOrderDet.getOrderLen());
        mat.setMatWidth(salesOrderDet.getOrderWidth());
        mat.setMatThick(salesOrderDet.getOrderThick());
        mat.setSgSign(salesOrderDet.getSgSign());
        mat.setProdCname(salesOrderDet.getProdCname());
        mat.setProdCnameOther(salesOrderDet.getProdCnameOther());
        return matService.queryMatList(mat);
    }

}
