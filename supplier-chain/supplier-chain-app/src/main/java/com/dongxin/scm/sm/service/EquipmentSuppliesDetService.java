package com.dongxin.scm.sm.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.mapper.CustomerProfileMapper;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.enums.SerialNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.enums.SettleStatusEnum;
import com.dongxin.scm.fm.service.FundAccountService;
import com.dongxin.scm.sm.dto.EquipmentSuppliesDTO;
import com.dongxin.scm.sm.entity.EquipmentSupplies;
import com.dongxin.scm.sm.entity.EquipmentSuppliesDet;
import com.dongxin.scm.sm.mapper.EquipmentSuppliesDetMapper;
import com.dongxin.scm.utils.SerialNoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 设备物资明细表
 * @Author: jeecg-boot
 * @Date: 2021-03-02
 * @Version: V1.0
 */
@Service
public class EquipmentSuppliesDetService extends BaseService<EquipmentSuppliesDetMapper, EquipmentSuppliesDet> {

    @Autowired
    EquipmentSuppliesService equipmentSuppliesService;

    @Autowired
    CustomerProfileService customerProfileService;

    private final String str = "(本部门小计)";

    private final String s = "(财务分类小计)";


    public void importExcel(List<EquipmentSuppliesDet> list) {
        list = list.stream().filter(l -> ObjectUtil.isNotNull(l.getProdCname())).collect(Collectors.toList());
        list = list.stream().filter(l -> !l.getProdCname().equals(str)).collect(Collectors.toList());
        list = list.stream().filter(l -> !l.getProdCname().equals(s)).collect(Collectors.toList());
        List<EquipmentSuppliesDet> list1 = list;

        Map<String, BigDecimal> result =
                list.stream().collect(Collectors.groupingBy(EquipmentSuppliesDet::getCustomerId,
                        Collectors.mapping(EquipmentSuppliesDet::getSalesAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        List<EquipmentSupplies> equipmentSuppliesList = CollectionUtil.newArrayList();
        for (Map.Entry<String, BigDecimal> entry : result.entrySet()) {
            customerProfileService.checkName(entry.getKey());
            EquipmentSupplies equipmentSupplies = new EquipmentSupplies();
            String orderNo = SerialNoUtil.getSerialNo(SerialNoEnum.EQUIPMENT_SUPPLIES_NO);
            equipmentSupplies.setTotalPrice(entry.getValue());
            equipmentSupplies.setOrderNo(orderNo);
            equipmentSupplies.setStatus(SettleStatusEnum.UNSETTLE.getCode());
            equipmentSupplies.setCustomer(entry.getKey());
            equipmentSuppliesList.add(equipmentSupplies);
        }
        equipmentSuppliesService.saveBatch(equipmentSuppliesList);

        Map<String, EquipmentSupplies> equipmentSuppliesMap = new HashMap<>();
        for (EquipmentSupplies supplies: equipmentSuppliesList ) {
            equipmentSuppliesMap.put(supplies.getCustomer(),supplies);
        }

        list1.forEach(item-> {
            EquipmentSupplies e = equipmentSuppliesMap.get(item.getCustomerId());
            if(ObjectUtil.isNotNull(e)) {
                item.setParentId(e.getId());
                item.setNewFlag("0");
            }
        });

        saveBatch(list1);
    }

//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    public static List<EquipmentSuppliesDTO> mapTransitionList(Map map) {
//        List list = new ArrayList();
//        Iterator iter = map.entrySet().iterator();
//        while (iter.hasNext()) {
//            Map.Entry entry = (Map.Entry) iter.next();
//            list.add(entry.getKey().toString());
//            list.add(new BigDecimal(entry.getValue().toString()));
//        }
//        return list;
//    }
}
