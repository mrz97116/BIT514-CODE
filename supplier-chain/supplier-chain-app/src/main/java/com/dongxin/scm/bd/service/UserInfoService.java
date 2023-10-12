package com.dongxin.scm.bd.service;

import cn.hutool.core.collection.CollectionUtil;
import com.dongxin.scm.bd.entity.UserInfo;
import com.dongxin.scm.bd.mapper.UserInfoMapper;
import com.dongxin.scm.exception.ScmException;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;

import java.util.Collection;
import java.util.List;

/**
 * @Description: 用户信息
 * @Author: jeecg-boot
 * @Date:   2021-08-26
 * @Version: V1.0
 */
@Service
public class UserInfoService extends BaseService<UserInfoMapper, UserInfo> {

    public boolean save(UserInfo userInfo) {
        List<UserInfo> list = list();
        if(CollectionUtil.isNotEmpty(list)) {
            throw new ScmException("已经存在记录，编辑数据即可");
        }
        return save(userInfo);
    }

}
