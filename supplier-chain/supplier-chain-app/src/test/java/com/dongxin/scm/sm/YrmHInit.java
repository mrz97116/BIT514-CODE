package com.dongxin.scm.sm;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.Application;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.enums.ProdClassCodeEnum;
import com.dongxin.scm.enums.SerialNoEnum;
import com.dongxin.scm.sm.dto.MatDTO;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.mapper.MatMapper;
import com.dongxin.scm.sm.service.InventoryService;
import com.dongxin.scm.sm.service.MatService;
import com.dongxin.scm.utils.ScmNumberUtils;
import com.dongxin.scm.utils.SerialNoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.newArrayList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class YrmHInit {

    @Autowired
    MatService matService;

    @Autowired
    InventoryService inventoryService;

    @Resource
    MatMapper matMapper;

    @Autowired
    CustomerProfileService customerProfileService;

    /**
     * 阳蕊明 展旭中板初始化材料
     */
    @Test
    public void init() {
        //查询阳蕊明业务提供的中板库存数据
        List<MatDTO> list = matMapper.queryMatYrmH();

        List<Mat> matList = newArrayList();
        list.forEach(item -> {
            Mat mat = new Mat();
            String custMatSpecs = item.getCustMatSpecs();
            String[] str = custMatSpecs.split("\\*");
            String prodCname = item.getProdCname();
            String remark = item.getRemark();
            String matNo = item.getMatNo();
            String sgSign = item.getSgSign();
            String date = item.getTime().replaceAll("\\.", "\\-");
            int qty = Integer.parseInt(item.getQty());
            double weight = Double.parseDouble(item.getWeight());
            String cakeNum = item.getCakeNum();
            String planNo = item.getPlanNo();

            //厚
            String thick = str[0];
            //宽
            String width = str[1];
            //长
            String len = str[2];
            mat.setMatThick(Double.parseDouble(thick))
                    .setMatWidth(Double.parseDouble(width))
                    .setMatLen(Double.parseDouble(len))
                    .setOldProdCname(prodCname)
                    .setProdCname(prodCname)
                    .setProdCnameOther(prodCname)
                    .setProdClassCode("H")
                    .setRemarks(remark)
                    .setSgSign(sgSign)
                    .setMatNum(qty)
                    .setCustMatSpecs(custMatSpecs)
                    .setMatKind("HR")
                    .setTenantId(7)
                    .setMatNo(matNo)
                    .setStockHouseId("1365117459323314177")
                    .setCarLoadingTime(DateUtil.parse(date))
                    .setCakeNo(cakeNum)
                    .setPlanNo(planNo)
                    .setMatNetWt(weight);
            matList.add(mat);
        });
        System.out.println(matList);
        matService.saveBatch(matList);
    }

    /**
     * 阳蕊明展旭中板库存 初始化
     */
    @Test
    public void initInventory() {
        String stockNo = SerialNoUtil.getSerialNo(SerialNoEnum.STOCK_NO);
        //查询展旭中板材料
        List<Mat> matList = matMapper.queryByYrmH();
        for (Mat mat : matList) {
            Inventory initEntity = new Inventory();
            initEntity.setMatLen(mat.getMatLen());
            initEntity.setMatThick(mat.getMatThick());
            initEntity.setMatWidth(mat.getMatWidth());
            initEntity.setWeight(mat.getMatNetWt());
            initEntity.setProdClassCode(mat.getProdClassCode());
            initEntity.setProdCname(mat.getProdCname());
            initEntity.setProdCnameOther(mat.getProdCnameOther());
            initEntity.setSgSign(mat.getSgSign());
            initEntity.setOldProdCname(mat.getOldProdCname());
            initEntity.setMatNum(mat.getMatNum().doubleValue());
            initEntity.setMatNo(mat.getMatNo());
            initEntity.setCustMatSpecs(mat.getCustMatSpecs());
            if (ProdClassCodeEnum.F.getValue().equals(mat.getProdClassCode()) ||
                    ProdClassCodeEnum.G.getValue().equals(mat.getProdClassCode())) {
                initEntity.setCustMatSpecs(mat.getMatThick() + "*" + mat.getMatWidth() + "*C");
            }
            initEntity.setMatKind(mat.getMatKind());
            initEntity.setAvailableQty(mat.getMatNum().doubleValue());
            initEntity.setAvailableWeight(mat.getMatNetWt());
            initEntity.setStockHouseId(mat.getStockHouseId());
            //对 中板 设置单重  单重=理论厚度*钢板宽度*钢板长度*7.85/1000000000
            BigDecimal matThick = BigDecimal.valueOf(ScmNumberUtils.ifNullZero(mat.getMatThick()));
            BigDecimal matWidth = BigDecimal.valueOf(ScmNumberUtils.ifNullZero(mat.getMatWidth()));
            BigDecimal matLen = BigDecimal.valueOf(ScmNumberUtils.ifNullZero(mat.getMatLen()));
            BigDecimal formula = BigDecimal.valueOf(7.85).divide(BigDecimal.valueOf(1000000000));
            if (ProdClassCodeEnum.H.getValue().equals(mat.getProdClassCode())) {
                initEntity.setPieceWeight(matThick.multiply(matWidth).multiply(matLen).multiply(formula)
                        .setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
            initEntity.setWtMode(mat.getWtMode());
            initEntity.setTenantId(7);
            inventoryService.save(initEntity);
            //材料关联库存
            mat.setInventoryId(initEntity.getId());
            mat.setStockNo(stockNo);
        }
        matService.updateBatchById(matList);
        List<String> matIds = matList.stream().map(Mat::getId).collect(Collectors.toList());
        matService.warehouseWarrant(matIds);
    }

    /**
     * 初始化13号客户租客
     */
    @Test
    public void init13() {
        QueryWrapper<CustomerProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(CustomerProfile::getTenantId, Arrays.asList("5","6","7","8"));
        List<CustomerProfile> customerProfileList = customerProfileService.list(queryWrapper);
        //"5", "6", "7", "8"的租户存入map
        Map<String, CustomerProfile> customerProfile5678RemoveDumplicateMap = removeDuplicate(customerProfileList);

        List<String> name5678List = new ArrayList();

        customerProfile5678RemoveDumplicateMap.forEach((k, v) -> {
            name5678List.add(v.getCompanyName());
        });
        //查询13号底下的租户
        queryWrapper.clear();
        queryWrapper.lambda().eq(CustomerProfile::getTenantId, 13);
        List<CustomerProfile> customerProfileList13 = customerProfileService.list(queryWrapper);

        List<String> name13List = new ArrayList();

        for (CustomerProfile customerProfile : customerProfileList13) {
            name13List.add(customerProfile.getCompanyName());
        }
        name5678List.removeAll(name13List);

        for (String s : name5678List) {
            CustomerProfile customerProfile = new CustomerProfile();
            customerProfile.setTenantId(13);
            customerProfile.setCompanyName(s);
            customerProfileService.save(customerProfile);
        }
     }

    /**
     * 以CustomerProfile为判断重复条件移除重复项
     * @param customerProfileList
     * @return 移除重复项之后,以companyName为key,实体类为值的map
     */
    private Map<String, CustomerProfile> removeDuplicate(List<CustomerProfile> customerProfileList) {
        HashMap<String, CustomerProfile> map = new HashMap<>();
        for (CustomerProfile c : customerProfileList) {
            map.put(c.getCompanyName(), c);
        }
        return map;
    }





}
