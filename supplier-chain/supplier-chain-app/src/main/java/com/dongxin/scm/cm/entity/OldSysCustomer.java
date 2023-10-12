package com.dongxin.scm.cm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
@TableName("ch_init_customer")
public class OldSysCustomer {
    /**
     * 单位名称
     */
    @Excel(name = "单位名称", width = 15)
    @ApiModelProperty(value = "单位名称")
    private java.lang.String companyName;
    /**
     * 助记码
     */
    @Excel(name = "助记码", width = 15)
    @ApiModelProperty(value = "助记码")
    private java.lang.String alias;
    /**
     * 税号
     */
    @Excel(name = "税号", width = 15)
    @ApiModelProperty(value = "税号")
    private java.lang.String taxNo;
    /**
     * 地址
     */
    @Excel(name = "地址", width = 15)
    @ApiModelProperty(value = "地址")
    private java.lang.String address;
    /**
     * 开户行
     */
    @Excel(name = "开户行", width = 15)
    @Dict(dicCode = "id", dicText = "bank_name", dictTable = "fm_bank")
    @ApiModelProperty(value = "开户行")
    private java.lang.String bank;
    /**
     * 开户网点
     **/
//	@Excel(name = "开户网点", width = 15)
    @ApiModelProperty(value = "开户网点")
    private java.lang.String bankBranch;
    /**
     * 银行卡号
     */
    @Excel(name = "银行卡号", width = 15)
    @ApiModelProperty(value = "银行卡号")
    private java.lang.String bankCardNo;
    /**
     * 座机号
     */
    @Excel(name = "座机号", width = 15)
    @ApiModelProperty(value = "座机号")
    private java.lang.String landLineNo;
}
