package com.dongxin.scm.fm;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.Application;
import com.dongxin.scm.fm.controller.FinanceCheckController;
import com.dongxin.scm.fm.entity.FinanceMatCode;
import com.dongxin.scm.fm.entity.FundPool;
import com.dongxin.scm.fm.service.FinanceMatCodeService;
import com.dongxin.scm.fm.service.FundPoolService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class FinanceMatCodeTest {
    @Autowired
    private FinanceMatCodeService financeMatCodeService;

    @Test
    public void addFinanceMatCodeData(){
        QueryWrapper<FinanceMatCode> financeMatCodeQueryWrapper = new QueryWrapper<>();
        financeMatCodeQueryWrapper.lambda().eq(FinanceMatCode::getTenantId,2);
        List<FinanceMatCode> financeMatCodeList = financeMatCodeService.list(financeMatCodeQueryWrapper);

        List<FinanceMatCode> addFinanceMatCodeList = newArrayList();
        for(FinanceMatCode financeMatCode : financeMatCodeList){
            FinanceMatCode addFinanceMatCode = new FinanceMatCode();
            BeanUtils.copyProperties(financeMatCode,addFinanceMatCode);
            addFinanceMatCode.setId(null);
            addFinanceMatCode.setCreateBy(null);
            addFinanceMatCode.setCreateTime(null);
            addFinanceMatCode.setUpdateBy(null);
            addFinanceMatCode.setUpdateTime(null);
            addFinanceMatCode.setSysOrgCode(null);
            addFinanceMatCode.setTenantId(11);

            addFinanceMatCodeList.add(addFinanceMatCode);
        }

        financeMatCodeService.saveBatch(addFinanceMatCodeList);
    }
}