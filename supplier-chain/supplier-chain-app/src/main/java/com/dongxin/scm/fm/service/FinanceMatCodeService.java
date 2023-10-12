package com.dongxin.scm.fm.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.entity.FinanceMatCode;
import com.dongxin.scm.fm.mapper.FinanceMatCodeMapper;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 财务物料编码
 * @Author: jeecg-boot
 * @Date: 2021-05-18
 * @Version: V1.0
 */
@Service
public class FinanceMatCodeService extends BaseService<FinanceMatCodeMapper, FinanceMatCode> {

    public void addParam(FinanceMatCode param) {
        QueryWrapper<FinanceMatCode> financeMatCodeQueryWrapper = new QueryWrapper<>();
        financeMatCodeQueryWrapper.lambda().eq(FinanceMatCode::getMatCode, param.getMatCode());
        List<FinanceMatCode> financeMatCodeList = list(financeMatCodeQueryWrapper);
        if (CollectionUtil.isNotEmpty(financeMatCodeList)) {
            throw new ScmException(StrUtil.format("系统已有此物料编码，" + param.getMatCode()));
        }
        save(param);
    }

    public void updateFinanceMatCode(FinanceMatCode param) {
        FinanceMatCode financeMatCode = getById(param.getId());
        if (!param.getMatCode().equals(financeMatCode.getMatCode())) {
            QueryWrapper<FinanceMatCode> financeMatCodeQueryWrapper = new QueryWrapper<>();
            financeMatCodeQueryWrapper.lambda().eq(FinanceMatCode::getMatCode, param.getMatCode());
            List<FinanceMatCode> financeMatCodeList = list(financeMatCodeQueryWrapper);
            if (CollectionUtil.isNotEmpty(financeMatCodeList)) {
                throw new ScmException(StrUtil.format("系统已有此物料编码，" + param.getMatCode()));
            }
        }

        BeanUtils.copyProperties(param, financeMatCode);
        updateById(financeMatCode);
    }
}
