package com.foreveross.vds.service.fms.controller;

import com.alibaba.fastjson.JSONObject;
import com.foreveross.vds.constants.CommonErrorCode;
import com.foreveross.vds.dto.BaseResponse;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.*;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.fms.service.FmsBmpService;
import com.foreveross.vds.service.fms.service.FmsExpenseAccountService;
import com.foreveross.vds.service.fms.service.FmsExpenseSettledRelateService;
import com.foreveross.vds.util.tools.ExcelExportUtil;
import com.foreveross.vds.util.tools.FileEncodeUtil;
import com.github.pagehelper.Page;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 费用审批流程控制类
 *
 * @author foreveross
 */
@Controller
public class FmsBmpController {

    private static Logger logger = LoggerFactory.getLogger(FmsBmpController.class);

    @Autowired
    private FmsBmpService fmsBmpService;
    @Autowired
    private FmsExpenseAccountService fmsExpenseAccountService;
    @Autowired
    private FmsExpenseSettledRelateService fmsExpenseSettledRelateService;

    /**
     * 分页查询费用批次视图
     *
     * @param fmsBmpV
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "query_fms_bmpv_page")
    public BasePageResponse<?> queryFmsBmpVPage(@RequestBody FmsBmpV fmsBmpV) {
        BasePageResponse<FmsBmpV> basePageResponse = new BasePageResponse<FmsBmpV>();

        Page<FmsBmpV> page = fmsBmpService.queryFmsBmpVByPage(fmsBmpV);

        basePageResponse.setRows(page.getResult());
        basePageResponse.setTotal(page.getTotal());
        return basePageResponse;
    }

    /**
     * 发起费用审批流程
     *
     * @param fmsExpenseAccount
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "start_fms_fLow")
    public RecRequest startFmsFLow(@RequestBody FmsExpenseAccount fmsExpenseAccount) {
        RecRequest recRequest = new RecRequest();
        recRequest.setReCode(0);
        try {
            recRequest = fmsBmpService.startFmsFlow(fmsExpenseAccount);
            recRequest = fmsBmpService.updateSettledStatus(fmsExpenseAccount);
        } catch (BusinessServiceException e) {
            recRequest.setReCode(1);
            recRequest.setMessage(e.getMessage());
        }
        return recRequest;
    }

    /**
     * 查询费用明细
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/query_detail")
    public BasePageResponse<FmsExpenseDetail> queryDetail(@RequestBody Long expenseAccountId) {
        BasePageResponse<FmsExpenseDetail> basePageResponse = new BasePageResponse<FmsExpenseDetail>();
        List<FmsExpenseDetail> list = fmsExpenseAccountService.queryFmsExpenseDetailByExpenseAccountId(expenseAccountId);
        basePageResponse.setRows(list);
        basePageResponse.setTotal(Long.valueOf(list.size()));
        return basePageResponse;
    }

    /**
     * 查询合同明细
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/query_contract_detail")
    public BasePageResponse<FmsExpenseContractDetail> queryContractDetail(@RequestBody Long expenseAccountId) {
        BasePageResponse<FmsExpenseContractDetail> basePageResponse = new BasePageResponse<>();
        List<FmsExpenseContractDetail> list = fmsExpenseAccountService.queryFmsContractExpenseDetailByExpenseAccountId(expenseAccountId);
        basePageResponse.setRows(list);
        basePageResponse.setTotal(Long.valueOf(list.size()));
        return basePageResponse;
    }

    /**
     * 根据流程ID查询报销单号
     *
     * @param proInstId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "get_expenseaccount_proInstId")
    public FmsExpenseAccount getFmsExpenseAccount(@RequestBody String proInstId) {
        return fmsExpenseAccountService.getFmsExpenseAccountByProInstId(proInstId);
    }

    /**
     * 根据合同ID和合同类型获取合同信息
     *
     * @param fmsExpenseAccount
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "get_contbyidandlookupid")
    public Contarct getContByIdAndLookupId(@RequestBody FmsExpenseAccount fmsExpenseAccount) {
        return fmsExpenseAccountService.getContByIdAndLookupId(fmsExpenseAccount.getContractHeaderId(), fmsExpenseAccount.getContractType());
    }

    /**
     * 分页查询费用审批流程视图
     *
     * @param fmsExpenseAccount
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "query_fms_flow_page")
    public BasePageResponse<?> queryFmsFlowPage(@RequestBody FmsExpenseAccount fmsExpenseAccount) {
        BasePageResponse<FmsExpenseAccount> basePageResponse = new BasePageResponse<FmsExpenseAccount>();

        Page<FmsExpenseAccount> page = fmsExpenseAccountService.queryFmsExpenseAccountByPage(fmsExpenseAccount);

        basePageResponse.setRows(page.getResult());
        basePageResponse.setTotal(page.getTotal());
        return basePageResponse;
    }

    /**
     * 查询费用流程审核时报销单号结算批次关联表视图
     *
     * @param proInstId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "query_fms_expense_settledrelatev_list")
    public BasePageResponse<?> queryFmsExpenseSettledRelateVList(@RequestBody String proInstId) {
        BasePageResponse<FmsExpenseSettledRelateV> basePageResponse = new BasePageResponse<FmsExpenseSettledRelateV>();
        proInstId = proInstId.replaceAll("\"", "");
        List<FmsExpenseSettledRelateV> list = fmsBmpService.queryFmsExpenseSettledRelateVList(proInstId);

        basePageResponse.setRows(list);
        basePageResponse.setTotal(Long.parseLong(list.size() + ""));
        return basePageResponse;
    }

    /**
     * 通过费用审批流程
     *
     * @param fmsProApprovalRecord
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "adopt_fms_flow")
    public RecRequest adoptFmsFlow(@RequestBody FmsProApprovalRecord fmsProApprovalRecord) {
        RecRequest recRequest = new RecRequest();
        recRequest.setReCode(0);
        try {
            fmsBmpService.rejOrAdoptFmsFlow("审核通过", fmsProApprovalRecord);
        } catch (Exception e) {
            e.printStackTrace();
            recRequest.setReCode(1);
            recRequest.setMessage("审核通过失败");
        }
        return recRequest;
    }

    /**
     * 驳回费用审批流程
     *
     * @param fmsProApprovalRecord
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "reject_fms_flow")
    public RecRequest rejectFmsFlow(@RequestBody FmsProApprovalRecord fmsProApprovalRecord) {
        RecRequest recRequest = new RecRequest();
        recRequest.setReCode(0);
        try {
            fmsBmpService.rejOrAdoptFmsFlow("驳回", fmsProApprovalRecord);
        } catch (Exception e) {
            e.printStackTrace();
            recRequest.setReCode(1);
            recRequest.setMessage("驳回通过失败");
        }
        return recRequest;
    }

    /**
     * 撤销费用审批流程
     *
     * @param fmsExpenseAccount
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "stop_fms_flow")
    public FmsExpenseAccount rejectFmsFlow(@RequestBody FmsExpenseAccount fmsExpenseAccount) throws BusinessServiceException {
        fmsExpenseAccount = fmsBmpService.stopFmsFlow(fmsExpenseAccount);
        return fmsExpenseAccount;
    }

    /**
     * 根据流程ID获取审批记录
     *
     * @param proInstId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "get_proapprovalrecordbyproinstId")
    public List<FmsProApprovalRecord> queryFmsProApprovalRecordByProInstId(@RequestBody String proInstId) {
        proInstId = proInstId.replace("\"", "");
        return fmsBmpService.queryFmsProApprovalRecordByProInstId(proInstId);
    }

    /**
     * 根据报销单号获取审批记录
     *
     * @param expenseAccountId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "get_proapprovalrecordbyexpenseaccountId")
    public BasePageResponse<FmsProApprovalRecord> queryFmsProApprovalRecordByExpenseAccountId(@RequestBody Long expenseAccountId) {
        BasePageResponse<FmsProApprovalRecord> basePageResponse = new BasePageResponse<FmsProApprovalRecord>();
        List<FmsProApprovalRecord> list = fmsBmpService.queryFmsProApprovalRecordByExpenseAccountId(expenseAccountId);
        basePageResponse.setRows(list);
        basePageResponse.setTotal(Long.valueOf(list.size()));
        return basePageResponse;
    }

    /**
     * 生成报销单号
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "get_expenseaccountnumber")
    public FmsExpenseAccount getExpenseAccountNumber(@RequestBody FmsExpenseAccount fmsExpenseAccount) {
        String expenseAccountNumber = fmsBmpService.getExpenseAccountNumber();
        fmsExpenseAccount.setExpenseAccountNumber(expenseAccountNumber);
        return fmsExpenseAccount;
    }


    @ResponseBody
    @RequestMapping(value = "is_relate_confirm")
    public FmsExpenseAccount isRelateConfirm(@RequestBody FmsExpenseAccount fmsExpenseAccount) {
        try {
            String retStr = fmsBmpService.isRelateConfirm(fmsExpenseAccount);
            fmsExpenseAccount.setAttribute1(retStr);
            return fmsExpenseAccount;
        } catch (BusinessServiceException e) {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping("/export_fms_flow.do")
    public Object exportFmsFlow(@RequestBody Map<String, Object> params) {
        DetailResponse detailResponse = new DetailResponse();
        try {
            FmsBmpV fmsBmpV = JSONObject.parseObject(JSONObject.toJSONString(params.get("whereClause")), FmsBmpV.class);

            List<FmsBmpV> list = fmsBmpService.queryFmsBmpV(fmsBmpV);
            fmsBmpService.translate(list);
            detailResponse.setDetail(ExcelExportUtil.exportToString(list));
        } catch (Exception e) {
            detailResponse.setStatus(0);
            logger.error(e.getMessage(), e);
        }
        return detailResponse;
    }

    @ResponseBody
    @RequestMapping(value = "get_import_flow_data.do")
    public BaseResponse getImportFlowData(@RequestBody Map<String, Object> params) {
        DetailResponse detailResponse = new DetailResponse();
        try {
            Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String sessionId = String.valueOf(params.get("sessionId"));
            String fileName = String.valueOf(params.get("fileName"));
            String fileString = String.valueOf(params.get("file"));

            Workbook workbook = FileEncodeUtil.parseWorkBoook(fileString, fileName);
            List<FmsBmpV> list = fmsBmpService.getImportFlowData(userId, sessionId, workbook);
            detailResponse.setDetail(list);
        } catch (BusinessServiceException e) {
            logger.error(e.getMessage(), e);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setStatus(1);
            detailResponse.setMessage(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setStatus(0);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }
}
