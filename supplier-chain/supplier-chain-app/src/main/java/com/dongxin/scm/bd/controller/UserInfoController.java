package com.dongxin.scm.bd.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.bd.entity.UserInfo;
import com.dongxin.scm.bd.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 用户信息
 * @Author: jeecg-boot
 * @Date:   2021-08-26
 * @Version: V1.0
 */
@Api(tags="用户信息")
@RestController
@RequestMapping("/bd/userInfo")
@Slf4j
public class UserInfoController extends BaseController<UserInfo, UserInfoService> {

     @Autowired
     private UserInfoService userInfoService;

     /**
      *   添加
      *
      * @param param
      * @return
      */
     @AutoLog(value = "添加")
     @ApiOperation(value="添加", notes="添加")
     @PostMapping(value = "/add")
     public Result<?> add(@RequestBody UserInfo param) {
      userInfoService.save(param);
      return Result.OK("添加成功！");
     }

}
