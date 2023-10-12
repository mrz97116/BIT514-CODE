package com.dongxin.scm.common.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.dongxin.scm.exception.ScmException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ：melon
 * @date ：Created in 2021-9-9 9:57
 */
@Slf4j
@Service
public class LockService {

    @Autowired
    private LockTemplate lockTemplate;

    public LockInfo lock(String lockName) {

        if (StrUtil.isBlank(lockName)) {
            throw new ScmException("参数错误，lockName为空");
        }
        LockInfo lockInfo = lockTemplate.lock(lockName);

        if (lockInfo == null) {
            log.error("结算请求过于频繁，请稍后重试");
            throw new ScmException("请求过于频繁，请稍后重试");
        }


        return lockInfo;

    }

    public void releaseLock(LockInfo lockInfo) {
        lockTemplate.releaseLock(lockInfo);
    }
}
