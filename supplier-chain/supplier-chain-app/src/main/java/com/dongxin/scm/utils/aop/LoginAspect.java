package com.dongxin.scm.utils.aop;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.encryption.AesEncryptUtil;
import org.jeecg.modules.system.model.SysLoginModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Slf4j
@Aspect
@Component
public class LoginAspect {

    @Value("${aes.key}")
    private String aesKey;

    @Value("${aes.iv}")
    private String aesIv;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Pointcut("execution(public * org.jeecg.modules.system.controller.LoginController.login(..))")
    public void loginPointCut() {}

    /**
     * 对登录方法返回值进行修改
     * 首先是判断是否是正式环境，判断依据是看spring加载的哪个profile文件，如果是prod就判断为正式环境
     * 其次是判断是否为弱口令，弱口令的判断正则是：长度在6之内的纯数字
     * 向返回结果写入两个状态变量，用于让前端显示提示用户修改密码的对话框
     */
    @AfterReturning(value = "loginPointCut()", argNames = "ret", returning = "ret")
    public void loginAround(JoinPoint joinPoint, Object ret) throws Throwable {
        if (ret instanceof Result) {
            Result<JSONObject> result = (Result<JSONObject>) ret;
            if (result.isSuccess()) {
                Object[] args = joinPoint.getArgs();
                if (args.length != 1) return;
                SysLoginModel sysLoginModel = (SysLoginModel) args[0];
                String password = AesEncryptUtil.desEncrypt(sysLoginModel.getPassword(), aesKey, aesIv);
                JSONObject jsonObject = result.getResult();
                jsonObject.put("isProductionEnvironment", StrUtil.equals("prod", activeProfile));
                jsonObject.put("isWeakPassword", Pattern.matches("\\d{0,6}", password));
                result.setResult(jsonObject);
            }
        }
    }

}
