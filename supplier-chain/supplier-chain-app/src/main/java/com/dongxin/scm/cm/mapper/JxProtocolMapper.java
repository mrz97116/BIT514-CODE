package com.dongxin.scm.cm.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.cm.entity.JxProtocol;
import com.dongxin.scm.cm.entity.JxProtocolTitle;

import java.util.List;

/**
 * @Description: 经销年度协议
 * @Author: jeecg-boot
 * @Date: 2020-12-29
 * @Version: V1.0
 */
public interface JxProtocolMapper extends BaseMapper<JxProtocol> {
    List<JxProtocolTitle> queryTitle();

    List<String> queryAllCompany();

}
