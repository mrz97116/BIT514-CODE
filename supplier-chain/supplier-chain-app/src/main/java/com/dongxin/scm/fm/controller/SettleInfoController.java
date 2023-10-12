package com.dongxin.scm.fm.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.common.service.LockService;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.entity.SettleInfo;
import com.dongxin.scm.fm.service.SettleInfoService;
import com.dongxin.scm.om.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.config.mybatis.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 结算单信息
 * @Author: jeecg-boot
 * @Date: 2020-10-27
 * @Version: V1.0
 */
@Api(tags = "结算单信息")
@RestController
@RequestMapping("/fm/settleInfo")
@Slf4j
public class SettleInfoController extends BaseController<SettleInfo, SettleInfoService> {

    @Autowired
    private SettleInfoService settleInfoService;

    @Autowired
    private LockService lockService;

    /**
     *
     * @param stackIds 装车id
     * @param discount 优惠
     * @param remark
     * @return
     */
    @AutoLog(value = "按装车单明细结算")
    @ApiOperation(value = "装车单明细结算", notes = "装车单明细结算")
    @GetMapping(value = "/settle")
    public Result<?> settle(@RequestParam(name = "stackIds") String stackIds,
                            @RequestParam(name = "discount") BigDecimal discount,
                            @RequestParam(name = "remark") String remark) {

        if (StringUtils.isEmpty(stackIds)) {
            return Result.error("请选择装车单");
        }

        List<String> stackIdList = Arrays.asList(stackIds.split(","));


        String tenantId = TenantContext.getTenant();

        //用入库字段作为锁头，同一租户只有一个线程会被执行
        LockInfo lockInfo = lockService.lock(Constants.SETTLE_LOCK_HEADER + tenantId);

        try {
            settleInfoService.settle(stackIdList, discount, remark);
            return Result.OK("结算完成");
        } finally {
            lockService.releaseLock(lockInfo);
        }
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
    public Result<?> queryPageList(SettleInfo param,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<SettleInfo> queryWrapper = QueryGenerator.initQueryWrapper(param, req.getParameterMap());
        queryWrapper.lambda().orderByDesc(SettleInfo::getSettleNo);

        //产品大类模糊查询
        String prodCode = req.getParameter("prodCode1");
        if (StrUtil.isNotBlank(prodCode)) {
            queryWrapper.lambda().like(SettleInfo::getProdCode, prodCode);
        }


        List<SettleInfo> settleInfoList = settleInfoService.list(queryWrapper);
        List<String> ids = settleInfoList.stream().map(SettleInfo::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(ids)) {
            queryWrapper.lambda().in(SettleInfo::getId, ids);
        } else {
            queryWrapper.lambda().eq(SettleInfo::getId, null);
        }
        Page<SettleInfo> page = new Page<SettleInfo>(pageNo, pageSize);
        IPage<SettleInfo> pageList = settleInfoService.page(page, queryWrapper);
        IPage<SettleInfo> settleInfoIPage = settleInfoService.translate(pageList);
        return Result.OK(settleInfoIPage);
    }


    /**
     * 编辑结算单页面备注
     *
     * @param settleInfo
     * @return
     */
    @AutoLog(value = "编辑")
    @ApiOperation(value = "编辑", notes = "编辑")
    @PutMapping(value = "/editRemark")
    public Result<?> editRemark(@RequestBody SettleInfo settleInfo) {
        settleInfoService.editRemark(settleInfo);
        return Result.OK("编辑成功!");
    }


}
