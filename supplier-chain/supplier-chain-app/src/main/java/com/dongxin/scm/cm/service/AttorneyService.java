package com.dongxin.scm.cm.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.dongxin.scm.cm.entity.Attorney;
import com.dongxin.scm.cm.mapper.AttorneyMapper;
import com.dongxin.scm.enums.SerialNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.utils.SerialNoUtil;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Description: 委托书
 * @Author: jeecg-boot
 * @Date: 2020-10-20
 * @Version: V1.0
 */
@Service
public class AttorneyService extends BaseService<AttorneyMapper, Attorney> {
    @Autowired
    AttorneyService service;

    /*
        起始时间，终止时间检验
     */
    public void checkData(List<Attorney> cmAttorneyList, Attorney attorney) {
        Date yesterday = DateUtil.yesterday();

        if (attorney.getCommissionStartTime().before(yesterday)) {
            System.out.println("起始时间:" + attorney.getCommissionStartTime());
            System.out.println("当前时间:" + new Date());
            throw new ScmException("开始时间不能小于当前时间！");
        }

        if (attorney.getCommissionStartTime().after(attorney.getTerminationTimeOfEntrustment())) {
            throw new ScmException("结束时间不能小于开始时间！");
        }

        if (CollectionUtil.isNotEmpty(cmAttorneyList)) {
            for (Attorney attorneyTmp : cmAttorneyList) {
                if (!(attorney.getCommissionStartTime().after(attorneyTmp.getTerminationTimeOfEntrustment())
                        || attorneyTmp.getCommissionStartTime().after(attorney.getTerminationTimeOfEntrustment())))
                    throw new ScmException("时间存在重叠！");
            }
        }
    }

    public void setCopyData(List<String> attorneyIdList) {
        List<Attorney> attorneyList = service.listByIds(attorneyIdList);

        for (Attorney attorney : attorneyList) {
            Attorney copyAttorney = new Attorney();
            BeanUtil.copyProperties(attorney, copyAttorney);

            //把开始时间赋为当年那日
            Date date = DateUtil.date();
            String format = DateUtil.format(date, "yyyy");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
            String dateStr = simpleDateFormat.format(attorney.getCommissionStartTime());
            String str1 = format + "-" + dateStr;
            Date CopyCommissionStartTime = DateUtil.parse(str1);
            copyAttorney.setCommissionStartTime(CopyCommissionStartTime);

            //把结束时间赋为当年最后一日
            String str2 = format + "-12-31";
            Date CopyTerminationTimeOfEntrustment = DateUtil.parse(str2);
            copyAttorney.setTerminationTimeOfEntrustment(CopyTerminationTimeOfEntrustment);

            copyAttorney.setId(null);
            copyAttorney.setDelegateNo(SerialNoUtil.getSerialNo(SerialNoEnum.ATTORNEY_NO));
            service.save(copyAttorney);
        }
    }
}
