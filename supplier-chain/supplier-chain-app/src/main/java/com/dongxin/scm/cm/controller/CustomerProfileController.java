package com.dongxin.scm.cm.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.bd.service.CompanyTenantDetService;
import com.dongxin.scm.cm.entity.CustomerAddress;
import com.dongxin.scm.cm.entity.CustomerBank;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.entity.CustomerRate;
import com.dongxin.scm.cm.service.*;
import com.dongxin.scm.enums.YesNoEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.mybatis.TenantContext;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 客户基础信息
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
@Api(tags = "客户基础信息")
@RestController
@RequestMapping("/cm/customerProfile")
@Slf4j
public class CustomerProfileController extends JeecgController<CustomerProfile, CustomerProfileService> {

    @Autowired
    private CustomerProfileService customerProfileService;  //客户档案服务

    @Autowired
    private CustomerBankService customerBankService;    //客户银行服务

    @Autowired
    private CustomerAddressService customerAddressService;  //客户地址服务

    @Autowired
    private CustomerRateService customerRateService;    //客户评价服务

    @Autowired
    private CompanyTenantDetService companyTenantDetService;    //公司租户检测服务


    /**
     * 添加
     * <p>
     * /**
     * 分页列表查询
     *
     * @param customerProfile
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "客户基础信息-分页列表查询")
    @ApiOperation(value = "客户基础信息-分页列表查询", notes = "客户基础信息-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(CustomerProfile customerProfile,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<CustomerProfile> queryWrapper = QueryGenerator.initQueryWrapper(customerProfile, req.getParameterMap());
        if (StrUtil.isNotBlank(req.getParameter("companyNames"))) {
            String companyNames = req.getParameter("companyNames").replace("\t","");
            String[] str = companyNames.split("\n");
            queryWrapper.lambda().in(CustomerProfile::getCompanyName, str);
        }
        String editD = req.getParameter("editDealWayStatus");
        if (req.getParameter("editDealWayStatus").equals("0")){
            queryWrapper.lambda().isNull(CustomerProfile::getDealWay);
        }else if (req.getParameter("editDealWayStatus").equals("1")){
            queryWrapper.lambda().isNotNull(CustomerProfile::getDealWay);
        }
        Page<CustomerProfile> page = new Page<CustomerProfile>(pageNo, pageSize);
        IPage<CustomerProfile> pageList = customerProfileService.page(page, queryWrapper);

        return Result.ok(pageList);
    }

    @AutoLog(value = "通过id查询顾客，选择alias传来alias的value（value为顾客id）")
    @GetMapping(value = "/selectCustomerByAlias")
    public Result<?> selectCustomerByAlias(@RequestParam("id") String id) {
        CustomerProfile customer = customerProfileService.getById(id);
        return Result.OK(customer);
    }


    /**
     * 添加
     *
     * @param customerProfile
     * @return
     */
    @AutoLog(value = "客户基础信息-添加")
    @ApiOperation(value = "客户基础信息-添加", notes = "客户基础信息-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody CustomerProfile customerProfile) {
        //YRM下的一个公司新增一条客户信息，YRM的其他公司也新增一条
        String tenantId = TenantContext.getTenant();
        List<Integer> tenantIds = companyTenantDetService.queryTenantIds(Integer.valueOf(tenantId));
//        if (CollectionUtil.isNotEmpty(tenantIds)){
//
//            for(int i=0;i<tenantIds.size();++i){
//                CustomerProfile copy = new CustomerProfile();
//                BeanUtils.copyProperties(customerProfile,copy);
//                copy.setTenantId(tenantIds.get(i));
//                customerProfileService.save(copy);
//            }
//            return Result.ok("添加成功！");
//        }
        if (customerProfile.getOrderMaxDueDays() == null) {
            customerProfile.setOrderMaxDueDays(3);
        }
        if (customerProfile.getCanLadingBill() == null) {
            customerProfile.setCanLadingBill(YesNoEnum.YES.getCode());
        }
        customerProfileService.save(customerProfile);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param customerProfile
     * @return
     */
    @AutoLog(value = "客户基础信息-编辑")
    @ApiOperation(value = "客户基础信息-编辑", notes = "客户基础信息-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody CustomerProfile customerProfile) {
        //YRM下的一个公司修改客户信息，YRM的其他公司也修改对应的客户信息
//        String tenantId = TenantContext.getTenant();
//        List<Integer> tenantIds = companyTenantDetService.queryTenantIds(Integer.valueOf(tenantId));
//        if (CollectionUtil.isNotEmpty(tenantIds)){
//            List<CustomerProfile> customers = customerService.queryCustomerIds(customerProfile.getCompanyName(), tenantIds);
//            for(CustomerProfile customer:customers){
//                CustomerProfile copy = new CustomerProfile();
//                BeanUtils.copyProperties(customerProfile,copy);
//                copy.setTenantId(customer.getTenantId());
//                copy.setId(customer.getId());
//                customerProfileService.save(copy);
//            }
//            return Result.ok("添加成功！");
//        }
        customerProfileService.updateById(customerProfile);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "客户基础信息-通过id删除")
    @ApiOperation(value = "客户基础信息-通过id删除", notes = "客户基础信息-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        customerProfileService.delMain(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "客户基础信息-批量删除")
    @ApiOperation(value = "客户基础信息-批量删除", notes = "客户基础信息-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.customerProfileService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 导出
     *
     * @return
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CustomerProfile customerProfile) {
        return super.exportXls(request, customerProfile, CustomerProfile.class, "客户基础信息");
    }

    /**
     * 导入
     *
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CustomerProfile.class);
    }
    /*---------------------------------主表处理-end-------------------------------------*/


    /*--------------------------------子表处理-顾客银行卡信息-begin----------------------------------------------*/

    /**
     * 通过主表ID查询
     *
     * @return
     */
    @AutoLog(value = "顾客银行卡信息-通过主表ID查询")
    @ApiOperation(value = "顾客银行卡信息-通过主表ID查询", notes = "顾客银行卡信息-通过主表ID查询")
    @GetMapping(value = "/listCustomerBankByMainId")
    public Result<?> listCustomerBankByMainId(CustomerBank customerBank,
                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                              HttpServletRequest req) {
        QueryWrapper<CustomerBank> queryWrapper = QueryGenerator.initQueryWrapper(customerBank, req.getParameterMap());
        Page<CustomerBank> page = new Page<CustomerBank>(pageNo, pageSize);
        IPage<CustomerBank> pageList = customerBankService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param customerBank
     * @return
     */
    @AutoLog(value = "顾客银行卡信息-添加")
    @ApiOperation(value = "顾客银行卡信息-添加", notes = "顾客银行卡信息-添加")
    @PostMapping(value = "/addCustomerBank")
    public Result<?> addCustomerBank(@RequestBody CustomerBank customerBank) {
        customerBankService.save(customerBank);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param customerBank
     * @return
     */
    @AutoLog(value = "顾客银行卡信息-编辑")
    @ApiOperation(value = "顾客银行卡信息-编辑", notes = "顾客银行卡信息-编辑")
    @PutMapping(value = "/editCustomerBank")
    public Result<?> editCustomerBank(@RequestBody CustomerBank customerBank) {
        customerBankService.updateById(customerBank);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "顾客银行卡信息-通过id删除")
    @ApiOperation(value = "顾客银行卡信息-通过id删除", notes = "顾客银行卡信息-通过id删除")
    @DeleteMapping(value = "/deleteCustomerBank")
    public Result<?> deleteCustomerBank(@RequestParam(name = "id", required = true) String id) {
        customerBankService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "顾客银行卡信息-批量删除")
    @ApiOperation(value = "顾客银行卡信息-批量删除", notes = "顾客银行卡信息-批量删除")
    @DeleteMapping(value = "/deleteBatchCustomerBank")
    public Result<?> deleteBatchCustomerBank(@RequestParam(name = "ids", required = true) String ids) {
        this.customerBankService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 导出
     *
     * @return
     */
    @RequestMapping(value = "/exportCustomerBank")
    public ModelAndView exportCustomerBank(HttpServletRequest request, CustomerBank customerBank) {
        // Step.1 组装查询条件
        QueryWrapper<CustomerBank> queryWrapper = QueryGenerator.initQueryWrapper(customerBank, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // Step.2 获取导出数据
        List<CustomerBank> pageList = customerBankService.list(queryWrapper);
        List<CustomerBank> exportList = null;

        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        } else {
            exportList = pageList;
        }

        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "顾客银行卡信息"); //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.CLASS, CustomerBank.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("顾客银行卡信息报表", "导出人:" + sysUser.getRealname(), "顾客银行卡信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }

    /**
     * 导入
     *
     * @return
     */
    @RequestMapping(value = "/importCustomerBank/{mainId}")
    public Result<?> importCustomerBank(HttpServletRequest request, HttpServletResponse response, @PathVariable("mainId") String mainId) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(false);
            try {
                List<CustomerBank> list = ExcelImportUtil.importExcel(file.getInputStream(), CustomerBank.class, params);
                for (CustomerBank temp : list) {
                    temp.setCustomerId(mainId);
                }
                long start = System.currentTimeMillis();
                customerBankService.saveBatch(list);
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                return Result.ok("文件导入成功！数据行数：" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    /*--------------------------------子表处理-顾客银行卡信息-end----------------------------------------------*/

    /*--------------------------------子表处理-顾客评价信息-begin----------------------------------------------*/

    /**
     * 通过主表ID查询
     *
     * @return
     */
    @AutoLog(value = "顾客星级评分-通过主表ID查询")
    @ApiOperation(value = "顾客星级评分-通过主表ID查询", notes = "顾客星级评分-通过主表ID查询")
    @GetMapping(value = "/listCustomerRateByMainId")
    public Result<?> listCustomerRateByMainId(CustomerRate customerRate,
                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                              HttpServletRequest req) {
        QueryWrapper<CustomerRate> queryWrapper = QueryGenerator.initQueryWrapper(customerRate, req.getParameterMap());
        Page<CustomerRate> page = new Page<CustomerRate>(pageNo, pageSize);
        IPage<CustomerRate> pageList = customerRateService.page(page, queryWrapper);
        return Result.ok(pageList);
    }


    /**
     * 添加
     *
     * @param customerRate
     * @return
     */
    @AutoLog(value = "顾客评价信息-添加")
    @ApiOperation(value = "顾客评价信息-添加", notes = "顾客评价信息-添加")
    @PostMapping(value = "/addCustomerRate")
    public Result<?> addCustomerRate(@RequestBody CustomerRate customerRate) {
        customerRateService.save(customerRate);
        return Result.ok("添加成功！");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "顾客星级评分-通过id删除")
    @ApiOperation(value = "顾客星级评分-通过id删除", notes = "顾客星级评分-通过id删除")
    @DeleteMapping(value = "/deleteCustomerRate")
    public Result<?> deleteCustomerRate(@RequestParam(name = "id", required = true) String id) {
        customerRateService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "顾客星级评分-批量删除")
    @ApiOperation(value = "顾客星级评分-批量删除", notes = "顾客星级评分-批量删除")
    @DeleteMapping(value = "/deleteBatchCustomerRate")
    public Result<?> deleteBatchCustomerRate(@RequestParam(name = "ids", required = true) String ids) {
        this.customerRateService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }


    /*--------------------------------子表处理-顾客评价信息-end----------------------------------------------*/


    /*--------------------------------子表处理-顾客收货地址信息-begin----------------------------------------------*/

    /**
     * 通过主表ID查询
     *
     * @return
     */
    @AutoLog(value = "顾客收货地址信息-通过主表ID查询")
    @ApiOperation(value = "顾客收货地址信息-通过主表ID查询", notes = "顾客收货地址信息-通过主表ID查询")
    @GetMapping(value = "/listCustomerAddressByMainId")
    public Result<?> listCustomerAddressByMainId(CustomerAddress customerAddress,
                                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                 HttpServletRequest req) {
        QueryWrapper<CustomerAddress> queryWrapper = QueryGenerator.initQueryWrapper(customerAddress, req.getParameterMap());
        Page<CustomerAddress> page = new Page<CustomerAddress>(pageNo, pageSize);
        IPage<CustomerAddress> pageList = customerAddressService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param customerAddress
     * @return
     */
    @AutoLog(value = "顾客收货地址信息-添加")
    @ApiOperation(value = "顾客收货地址信息-添加", notes = "顾客收货地址信息-添加")
    @PostMapping(value = "/addCustomerAddress")
    public Result<?> addCustomerAddress(@RequestBody CustomerAddress customerAddress) {
        customerAddressService.save(customerAddress);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param customerAddress
     * @return
     */
    @AutoLog(value = "顾客收货地址信息-编辑")
    @ApiOperation(value = "顾客收货地址信息-编辑", notes = "顾客收货地址信息-编辑")
    @PutMapping(value = "/editCustomerAddress")
    public Result<?> editCustomerAddress(@RequestBody CustomerAddress customerAddress) {
        customerAddressService.updateById(customerAddress);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "顾客收货地址信息-通过id删除")
    @ApiOperation(value = "顾客收货地址信息-通过id删除", notes = "顾客收货地址信息-通过id删除")
    @DeleteMapping(value = "/deleteCustomerAddress")
    public Result<?> deleteCustomerAddress(@RequestParam(name = "id", required = true) String id) {
        customerAddressService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "顾客收货地址信息-批量删除")
    @ApiOperation(value = "顾客收货地址信息-批量删除", notes = "顾客收货地址信息-批量删除")
    @DeleteMapping(value = "/deleteBatchCustomerAddress")
    public Result<?> deleteBatchCustomerAddress(@RequestParam(name = "ids", required = true) String ids) {
        this.customerAddressService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 导出
     *
     * @return
     */
    @RequestMapping(value = "/exportCustomerAddress")
    public ModelAndView exportCustomerAddress(HttpServletRequest request, CustomerAddress customerAddress) {
        // Step.1 组装查询条件
        QueryWrapper<CustomerAddress> queryWrapper = QueryGenerator.initQueryWrapper(customerAddress, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // Step.2 获取导出数据
        List<CustomerAddress> pageList = customerAddressService.list(queryWrapper);
        List<CustomerAddress> exportList = null;

        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        } else {
            exportList = pageList;
        }

        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "顾客收货地址信息"); //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.CLASS, CustomerAddress.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("顾客收货地址信息报表", "导出人:" + sysUser.getRealname(), "顾客收货地址信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }

    @AutoLog(value = "通过id查询顾客是否可开单")
    @GetMapping(value = "/selectCanLadingBill")
    public Result<?> selectCanLadingBill(@RequestParam("id") String id) {
        CustomerProfile customerProfile = customerProfileService.getById(id);
        if (customerProfile.getCanLadingBill().equalsIgnoreCase(YesNoEnum.NO.getCode())){
            return Result.error("该顾客可开单状态为不可开单，请检查是否超出最大超期天数");
        }
        return Result.OK();

    }

    /**
     * 导入
     *
     * @return
     */
    @RequestMapping(value = "/importCustomerAddress/{mainId}")
    public Result<?> importCustomerAddress(HttpServletRequest request, HttpServletResponse response, @PathVariable("mainId") String mainId) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(false);
            try {
                List<CustomerAddress> list = ExcelImportUtil.importExcel(file.getInputStream(), CustomerAddress.class, params);
                for (CustomerAddress temp : list) {
                    temp.setCustomerId(mainId);
                }
                long start = System.currentTimeMillis();
                customerAddressService.saveBatch(list);
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                return Result.ok("文件导入成功！数据行数：" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    /*--------------------------------子表处理-顾客收货地址信息-end----------------------------------------------*/


}
