package com.dongxin.scm.fm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.fm.dto.StackDetDeductionDTO;
import com.dongxin.scm.fm.entity.SettleInfo;
import com.dongxin.scm.fm.vo.IdAndAccountVO;
import com.dongxin.scm.fm.vo.SettledAccountAndFundDetailUnequalVO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description: 结算单信息
 * @Author: jeecg-boot
 * @Date: 2020-10-27
 * @Version: V1.0
 */
public interface SettleInfoMapper extends BaseMapper<SettleInfo> {

    Long settleMaxNum(String numSeq);

    /*List<CustomerProfile> select_company_name(List<String> arr1);*/

    List<StackDetDeductionDTO> selectStackDetDeduction(List<String> stackDetIds);

    List<SettleInfo> getSettleInfoList(Date thisMonthNumberOne);

    List<SettleInfo> selectSettleInfo(Date date);

    List<SettledAccountAndFundDetailUnequalVO> selectSettledAccountAndFundDetailUnequal();

    List<IdAndAccountVO> updateStackSettleAccount();

    List<IdAndAccountVO> updateSettleMainSettleAccount();

    List<IdAndAccountVO> errorFundDetSettleAccount();

    BigDecimal settleAccount(String customerId);
}
