package com.foreveross.vds.service.fms.controller;

import com.alibaba.fastjson.JSONObject;
import com.foreveross.vds.constants.CommonErrorCode;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.Pagination;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.fms.service.FmsStorageContractCategoryService;
import com.foreveross.vds.vo.fms.FmsStorageContractCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FmsStorageContractCategoryController {

    private static Logger logger = LoggerFactory.getLogger(FmsCarCategoryController.class);

    @Autowired
    private FmsStorageContractCategoryService fmsStorageContractCategoryService;

    @RequestMapping("/list_fms_storage_contract_category.do")
    @ResponseBody
    public DetailResponse listFmsStorageContractCategory(@RequestBody Map<String, Object> params) {
        DetailResponse detailResponse = new DetailResponse();
        try {
            FmsStorageContractCategory whereClause = JSONObject.parseObject(JSONObject.toJSONString(params.get("whereClause")), FmsStorageContractCategory.class);
            List<FmsStorageContractCategory> fmsStorageContractCategories = fmsStorageContractCategoryService.listByWhereCause(whereClause);
            detailResponse.setDetail(fmsStorageContractCategories);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return detailResponse;
    }

    @ResponseBody
    @RequestMapping("/page_fms_storage_contract_category.do")
    public Object pageFmsStorageContractCategory(@RequestBody Map<String, Object> params) {
        Pagination pagination = null;
        try {
            Integer page = (Integer) params.get("page");
            Integer rows = (Integer) params.get("rows");
            FmsStorageContractCategory fmsStorageContractCategory = JSONObject.parseObject(JSONObject.toJSONString(params.get("whereClause")), FmsStorageContractCategory.class);
            pagination = fmsStorageContractCategoryService.page(fmsStorageContractCategory, page, rows);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return pagination;

    }

    @ResponseBody
    @RequestMapping("/insert_fms_storage_contract_category.do")
    public DetailResponse insertFmsStorageContractCategory(@RequestBody FmsStorageContractCategory fmsStorageContractCategory) {
        DetailResponse<Integer> detailResponse = new DetailResponse();
        try {
            fmsStorageContractCategoryService.checkParams(fmsStorageContractCategory);
            int i = fmsStorageContractCategoryService.insert(fmsStorageContractCategory);
            detailResponse.setDetail(i);
        } catch (BusinessServiceException e) {
            logger.warn(e.getMessage());
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

    @ResponseBody
    @RequestMapping("/update_fms_storage_contract_category.do")
    public DetailResponse updateFmsStorageContractCategory(@RequestBody FmsStorageContractCategory fmsStorageContractCategory) {
        DetailResponse<Integer> detailResponse = new DetailResponse();
        try {
            fmsStorageContractCategoryService.checkParams(fmsStorageContractCategory);
            int i = fmsStorageContractCategoryService.updateByPrimaryKey(fmsStorageContractCategory);
            detailResponse.setDetail(i);
        } catch (BusinessServiceException e) {
            logger.warn(e.getMessage());
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

    @ResponseBody
    @RequestMapping("/delete_fms_storage_contract_category.do")
    public DetailResponse deleteFmsStorageContractCategory(@RequestBody Map<String, Object> params) {
        DetailResponse<Integer> detailResponse = new DetailResponse();
        try {
            Long contractCategoryId = Long.parseLong(String.valueOf(params.get("contractCategoryId")));
            int i = fmsStorageContractCategoryService.deleteByPrimaryKey(contractCategoryId);
            detailResponse.setDetail(i);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setStatus(0);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;

    }

    @ResponseBody
    @RequestMapping("/get_fms_storage_contract_category.do")
    public DetailResponse getFmsStorageContractCategory(@RequestBody Map<String, Object> params) {
        DetailResponse<FmsStorageContractCategory> detailResponse = new DetailResponse();
        try {
            Long contractCategoryId = Long.parseLong(String.valueOf(params.get("contractCategoryId")));
            FmsStorageContractCategory fmsStorageContractCategory = fmsStorageContractCategoryService.selectByPrimaryKey(contractCategoryId);
            detailResponse.setDetail(fmsStorageContractCategory);
        } catch (Exception e) {
            e.printStackTrace();
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setStatus(0);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }


}
