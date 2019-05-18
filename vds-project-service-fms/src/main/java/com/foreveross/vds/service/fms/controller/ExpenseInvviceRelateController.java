package com.foreveross.vds.service.fms.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.vds.dto.BaseResponse;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.FmsExpenseAccount;
import com.foreveross.vds.dto.fms.InvMidVatMain;
import com.foreveross.vds.service.fms.service.ExpenseInvviceRelateService;
import com.foreveross.vds.service.fms.service.FmsExpenseAccountService;
import com.foreveross.vds.util.tools.FileEncodeUtil;
import com.github.pagehelper.Page;

/**
 * 关联票号维护
 *
 * @author foreveross
 */
@Controller
public class ExpenseInvviceRelateController {
    @Autowired
    private FmsExpenseAccountService fmsExpenseAccountService;
    @Autowired
    private ExpenseInvviceRelateService expenseInvviceRelateService;

    /**
     * 根据报销单ID获取发票信息
     *
     * @param expenseAccountId
     * @return
     */
    @ResponseBody
    @RequestMapping("query_invmidbatmain_expenseaccountId")
    private BasePageResponse<?> queryInvMidVatMainByExpenseAccountId(@RequestBody Long expenseAccountId) {
        BasePageResponse<InvMidVatMain> basePageResponse = new BasePageResponse<>();
        List<InvMidVatMain> list = expenseInvviceRelateService.queryInvMidVatMainByExpenseAccountId(expenseAccountId);
        basePageResponse.setRows(list);
        basePageResponse.setTotal(Long.valueOf(list.size()));
        return basePageResponse;
    }

    /**
     * 分页查询报销单号
     *
     * @param fmsExpenseAccount
     * @return
     */
    @ResponseBody
    @RequestMapping("query_expenseinvvicerelate_page")
    private BasePageResponse<?> queryExpenseInvvicereLatePage(@RequestBody FmsExpenseAccount fmsExpenseAccount) {
        BasePageResponse<FmsExpenseAccount> basePageResponse = new BasePageResponse<FmsExpenseAccount>();
        Page<FmsExpenseAccount> page = fmsExpenseAccountService.queryFmsExpenseAccountByPage(fmsExpenseAccount);
        basePageResponse.setRows(page.getResult());
        basePageResponse.setTotal(page.getTotal());
        return basePageResponse;
    }

    /**
     * 关联票号
     *
     * @param fmsExpenseAccount
     * @return
     */
    @ResponseBody
    @RequestMapping("add_fmsexpenseinvoicerelate")
    public RecRequest addFmsExpenseInvoiceRelate(@RequestBody FmsExpenseAccount fmsExpenseAccount) {
        RecRequest recRequest = new RecRequest();
        recRequest.setReCode(0);
        try {
            recRequest = expenseInvviceRelateService.addFmsExpenseInvoiceRelate(fmsExpenseAccount);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            recRequest.setReCode(1);
            recRequest.setMessage("票号关联失败");
        }
        return recRequest;
    }

    /**
     * 关联票号提交
     *
     * @param fmsExpenseAccount
     * @return
     */
    @ResponseBody
    @RequestMapping("submit_fmsexpenseinvoicerelate")
    public RecRequest submitFmsExpenseInvoiceRelate(@RequestBody FmsExpenseAccount fmsExpenseAccount) {
        RecRequest recRequest = new RecRequest();
        recRequest.setReCode(0);
        try {
            recRequest = expenseInvviceRelateService.submitFmsExpenseInvoiceRelate(fmsExpenseAccount);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            recRequest.setReCode(1);
            recRequest.setMessage("票号关联提交失败");
        }
        return recRequest;
    }

    /**
     * 关联票号复核
     *
     * @param fmsExpenseAccount
     * @return
     */
    @ResponseBody
    @RequestMapping("audit_fmsexpenseinvoicerelate")
    public RecRequest auditFmsExpenseInvoiceRelate(@RequestBody FmsExpenseAccount fmsExpenseAccount) {
        RecRequest recRequest = new RecRequest();
        recRequest.setReCode(0);
        try {
            recRequest = expenseInvviceRelateService.auditFmsExpenseInvoiceRelate(fmsExpenseAccount);
        } catch (Exception e) {
            e.printStackTrace();
            recRequest.setReCode(1);
            recRequest.setMessage("票号关联提交失败");
        }
        return recRequest;
    }

    /**
     * 分页查询发票信息
     *
     * @param invMidVatMain
     * @return
     */
    @ResponseBody
    @RequestMapping("query_invmidVatmain_page")
    private BasePageResponse<?> queryInvMidVatMainPage(@RequestBody InvMidVatMain invMidVatMain) {
        String[] nums = invMidVatMain.getInvNum().split(",");
        List<String> numList = new ArrayList<>();
        for (String str : nums) {
            numList.add(str);
        }

        invMidVatMain.setNumLists(numList);

        BasePageResponse<InvMidVatMain> basePageResponse = new BasePageResponse<InvMidVatMain>();
        Page<InvMidVatMain> page = expenseInvviceRelateService.queryInvMidVatMainPage(invMidVatMain);
        basePageResponse.setRows(page.getResult());
        basePageResponse.setTotal(page.getTotal());
        return basePageResponse;
    }

    /**
     * 查询发票信息
     *
     * @param invMidVatMain
     * @return
     */
    @ResponseBody
    @RequestMapping("query_invmidVatmain")
    private BasePageResponse queryInvMidVatMain(@RequestBody InvMidVatMain invMidVatMain) {
        BasePageResponse<InvMidVatMain> basePageResponse = new BasePageResponse<InvMidVatMain>();
        List<InvMidVatMain> page = expenseInvviceRelateService.queryInvMidVatMain(invMidVatMain);
        basePageResponse.setRows(page);
        basePageResponse.setTotal(Long.valueOf(page.size()));
        return basePageResponse;
    }

    /**
     * 导入
     */
    @RequestMapping("/import_invmidVatmain")
    @ResponseBody
    public BaseResponse importInvmidVatmain(@RequestBody Map<String, Object> params) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            String fileName = String.valueOf(params.get("fileName"));
            String fileString = String.valueOf(params.get("file"));
            Workbook workbook = FileEncodeUtil.parseWorkBoook(fileString, fileName);
            List<InvMidVatMain> list = expenseInvviceRelateService.importInvmidVatmain(workbook);
            baseResponse = new DetailResponse<>(list);
        } catch (Exception e) {
            e.printStackTrace();
            baseResponse.setError("0");
            baseResponse.setStatus(0);
            baseResponse.setMessage(e.getMessage());
        }
        return baseResponse;
    }

    /**
     * 取消发票关联
     *
     * @param list
     * @return
     */
    @ResponseBody
    @RequestMapping("del_related_invoice")
    public BaseResponse delRelatedInvoice(@RequestBody List<InvMidVatMain> list) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            expenseInvviceRelateService.delRelatedInvoice(list);
        } catch (Exception e) {
           baseResponse.setStatus(0);
           baseResponse.setMessage("票号关联失败");
        }
        return baseResponse;
    }
}
