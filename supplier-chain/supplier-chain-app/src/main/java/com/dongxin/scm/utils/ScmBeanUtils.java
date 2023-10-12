package com.dongxin.scm.utils;

import org.springframework.beans.BeansException;

/**
 * desc org.springframework.beans.BeanUtils.copyProperties方法会根据字段进行拷贝，但是在jeecg平台中大部分表存在以下相同字段，
 * 如进行直接拷贝会导致Id,创建时间等数据错误，因此重写该方法，将部分字段进行屏蔽。
 * @author ：melon
 * @date ：Created in 2021/3/6 10:17
 */
public class ScmBeanUtils {
    public static void copyProperties(Object source, Object target) throws BeansException {
        org.springframework.beans.BeanUtils.copyProperties(source, target,
                "id","createBy","createTime", "updateBy","updateTime", "sysOrgCode","saleBillNo");
    }

    public static void copySysOutsideProperties(Object source, Object target) throws BeansException {
        org.springframework.beans.BeanUtils.copyProperties(source, target,
                "id","createBy","createTime", "updateBy","updateTime", "sysOrgCode");
    }
}
