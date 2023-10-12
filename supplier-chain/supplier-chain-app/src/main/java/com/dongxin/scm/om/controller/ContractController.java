package com.dongxin.scm.om.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.om.entity.Contract;
import com.dongxin.scm.om.entity.ContractDet;
import com.dongxin.scm.om.entity.ContractProvision;
import com.dongxin.scm.om.service.ContractDetService;
import com.dongxin.scm.om.service.ContractProvisionService;
import com.dongxin.scm.om.service.ContractService;
import com.dongxin.scm.om.vo.ContractPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 合同主档表
 * @Author: jeecg-boot
 * @Date: 2020-12-14
 * @Version: V1.0
 */
@Api(tags = "合同主档表")
@RestController
@RequestMapping("/om/contract")
@Slf4j
public class ContractController {
    @Autowired
    private ContractService contractService;
    @Autowired
    private ContractDetService contractDetService;
    @Autowired
    private ContractProvisionService contractProvisionService;

    /**
     * 分页列表查询
     *
     * @param contract
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "合同主档表-分页列表查询")
    @ApiOperation(value = "合同主档表-分页列表查询", notes = "合同主档表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(Contract contract,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<Contract> queryWrapper = QueryGenerator.initQueryWrapper(contract, req.getParameterMap());
        Page<Contract> page = new Page<Contract>(pageNo, pageSize);
        IPage<Contract> pageList = contractService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param contractPage
     * @return
     */
    @AutoLog(value = "合同主档表-添加")
    @ApiOperation(value = "合同主档表-添加", notes = "合同主档表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ContractPage contractPage) {
        Contract contract = new Contract();
        BeanUtils.copyProperties(contractPage, contract);
        contractService.saveMain(contract, contractPage.getContractDetList(), contractPage.getContractProvisionList());
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param contractPage
     * @return
     */
    @AutoLog(value = "合同主档表-编辑")
    @ApiOperation(value = "合同主档表-编辑", notes = "合同主档表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ContractPage contractPage) {
        Contract contract = new Contract();
        BeanUtils.copyProperties(contractPage, contract);
        Contract contractEntity = contractService.getById(contract.getId());
        if (contractEntity == null) {
            return Result.error("未找到对应数据");
        }
        contractService.updateMain(contract, contractPage.getContractDetList(), contractPage.getContractProvisionList());
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "合同主档表-通过id删除")
    @ApiOperation(value = "合同主档表-通过id删除", notes = "合同主档表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        contractService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "合同主档表-批量删除")
    @ApiOperation(value = "合同主档表-批量删除", notes = "合同主档表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.contractService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "合同主档表-通过id查询")
    @ApiOperation(value = "合同主档表-通过id查询", notes = "合同主档表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        Contract contract = contractService.getById(id);
        if (contract == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(contract);

    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "合同明细表通过主表ID查询")
    @ApiOperation(value = "合同明细表主表ID查询", notes = "合同明细表-通主表ID查询")
    @GetMapping(value = "/queryContractDetByMainId")
    public Result<?> queryContractDetListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<ContractDet> contractDetList = contractDetService.selectByMainId(id);
        return Result.OK(contractDetList);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "合同商务条款通过主表ID查询")
    @ApiOperation(value = "合同商务条款主表ID查询", notes = "合同商务条款-通主表ID查询")
    @GetMapping(value = "/queryContractProvisionByMainId")
    public Result<?> queryContractProvisionListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<ContractProvision> contractProvisionList = contractProvisionService.selectByMainId(id);
        return Result.OK(contractProvisionList);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param contract
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Contract contract) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<Contract> queryWrapper = QueryGenerator.initQueryWrapper(contract, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //Step.2 获取导出数据
        List<Contract> queryList = contractService.list(queryWrapper);
        // 过滤选中数据
        String selections = request.getParameter("selections");
        List<Contract> contractList = new ArrayList<Contract>();
        if (oConvertUtils.isEmpty(selections)) {
            contractList = queryList;
        } else {
            List<String> selectionList = Arrays.asList(selections.split(","));
            contractList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        }

        // Step.3 组装pageList
        List<ContractPage> pageList = new ArrayList<ContractPage>();
        for (Contract main : contractList) {
            ContractPage vo = new ContractPage();
            BeanUtils.copyProperties(main, vo);
            List<ContractDet> contractDetList = contractDetService.selectByMainId(main.getId());
            vo.setContractDetList(contractDetList);
            List<ContractProvision> contractProvisionList = contractProvisionService.selectByMainId(main.getId());
            vo.setContractProvisionList(contractProvisionList);
            pageList.add(vo);
        }

        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "合同主档表列表");
        mv.addObject(NormalExcelConstants.CLASS, ContractPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("合同主档表数据", "导出人:" + sysUser.getRealname(), "合同主档表"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(false);
            try {
                List<ContractPage> list = ExcelImportUtil.importExcel(file.getInputStream(), ContractPage.class, params);
                for (ContractPage page : list) {
                    Contract po = new Contract();
                    BeanUtils.copyProperties(page, po);
                    contractService.saveMain(po, page.getContractDetList(), page.getContractProvisionList());
                }
                return Result.OK("文件导入成功！数据行数:" + list.size());
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
        return Result.OK("文件导入失败！");
    }

}
