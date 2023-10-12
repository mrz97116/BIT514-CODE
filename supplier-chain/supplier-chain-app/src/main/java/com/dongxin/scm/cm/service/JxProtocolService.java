package com.dongxin.scm.cm.service;

import com.dongxin.scm.cm.entity.JxProtocol;
import com.dongxin.scm.cm.entity.JxProtocolTitle;
import com.dongxin.scm.cm.mapper.JxProtocolMapper;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 经销年度协议
 * @Author: jeecg-boot
 * @Date: 2020-12-29
 * @Version: V1.0
 */
@Service
public class JxProtocolService extends BaseService<JxProtocolMapper, JxProtocol> {

    public void setLevel() {
        List<JxProtocolTitle> jxProtocolTitleList = baseMapper.queryTitle();

        List<JxProtocol> jxProtocolList = list();

        int i = 1;
        for (JxProtocol jxProtocol : jxProtocolList) {
//            jxProtocol.setNum(getNum(i++));
            for (JxProtocolTitle jxProtocolTitle : jxProtocolTitleList) {
                if (jxProtocolTitle.str.contains(jxProtocol.getBranchCompany())) {
                    jxProtocol.setLevel(jxProtocolTitle.level);
                }
            }
        }

        saveOrUpdateBatch(jxProtocolList);
    }

    public static String getNum(int i) {
        if (i < 10) {
            return "00" + i;
        } else if (i < 100) {
            return "0" + i;
        } else {
            return String.valueOf(i);
        }
    }

    public List<String> queryAllYrmCompany() {
        return baseMapper.queryAllCompany();
    }


}
