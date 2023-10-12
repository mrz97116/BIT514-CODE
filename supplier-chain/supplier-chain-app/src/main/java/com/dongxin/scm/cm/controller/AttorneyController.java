package com.dongxin.scm.cm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.cm.entity.Attorney;
import com.dongxin.scm.cm.service.AttorneyService;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.enums.SerialNoEnum;
import com.dongxin.scm.utils.SerialNoUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 委托书
 * @Author: jeecg-boot
 * @Date: 2020-10-20
 * @Version: V1.0
 */
@Api(tags = "委托书")
@RestController
@RequestMapping("/cm/attorney")
@Slf4j
public class AttorneyController extends BaseController<Attorney, AttorneyService> {

    @Autowired
    AttorneyService service;
    @Autowired
    CustomerProfileService customerProfileService;


    @AutoLog(value = "委托书-添加")
    @ApiOperation(value = "委托书-添加", notes = "委托书-添加")
    @PostMapping(value = "/add")
    @Override
    public Result<?> add(@RequestBody Attorney attorney) {

        attorney.setDelegateNo(SerialNoUtil.getSerialNo(SerialNoEnum.ATTORNEY_NO));
        //把数据添加到数据库
        service.save(attorney);
        return Result.OK("添加成功！");
    }

    /**
     * 分页列表查询
     *
     * @param param
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */

    @AutoLog(value = "分页列表查询")
    @ApiOperation(value = "分页列表查询", notes = "分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(Attorney param,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<Attorney> queryWrapper = QueryGenerator.initQueryWrapper(param, req.getParameterMap());
        Page<Attorney> page = new Page<Attorney>(pageNo, pageSize);
        IPage<Attorney> pageList = service.page(page, queryWrapper);

        List<String> DelegateUnitNameIds = newArrayList();
        for (Attorney record : pageList.getRecords()) {
            DelegateUnitNameIds.add(record.getDelegateUnitName());
        }

        Map<String, String> customerName = customerProfileService.getNameMap(DelegateUnitNameIds);
        //1.收集所有的顾客id，去customerProfileService中查询，生成一个map，其中key为customerProfile的id，value为customerProfile的name
        for (Attorney record : pageList.getRecords()) {
            //2. 对record的 customerNameText进行赋值，通过map对customerNameText进行赋值
            record.setDelegateUnitName(customerName.get(record.getDelegateUnitName()));
        }

        return Result.OK(pageList);
    }

    /**
     * 复制新增
     *
     * @param attorneyIdList
     * @return
     */
    @AutoLog(value = "复制新增")
    @ApiOperation(value = "复制新增", notes = "复制新增")
    @PostMapping(value = "/copyAdd")
    public Result<?> copyAdd(@RequestBody List<String> attorneyIdList) {
        service.setCopyData(attorneyIdList);
        return Result.OK("添加成功！");
    }
}
