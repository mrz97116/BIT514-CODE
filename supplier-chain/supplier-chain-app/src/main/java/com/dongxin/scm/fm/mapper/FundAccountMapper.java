package com.dongxin.scm.fm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.fm.entity.Capital;
import com.dongxin.scm.fm.entity.FundAccount;
import com.dongxin.scm.fm.entity.FundDetail;
import com.dongxin.scm.fm.vo.FundAccountVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 资金账户表
 * @Author: jeecg-boot
 * @Date: 2020-11-23
 * @Version: V1.0
 */
public interface FundAccountMapper extends BaseMapper<FundAccount> {

    List<Capital> getFundAccountList(@Param("customerId") String customerId);

    /**查询客户资金汇总明细*/
    List<FundAccountVO> getFundAccountSummaryList();


    List<FundAccountVO> selectUnSettleAccountAndStackSettleAccount();

    List<FundDetail> getUnSettleTotalAmount(@Param("customerId") String customerId);
}
