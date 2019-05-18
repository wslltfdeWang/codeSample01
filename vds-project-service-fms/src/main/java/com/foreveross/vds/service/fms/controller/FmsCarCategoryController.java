package com.foreveross.vds.service.fms.controller;

import com.alibaba.fastjson.JSONObject;
import com.foreveross.vds.constants.CommonErrorCode;
import com.foreveross.vds.dto.BaseResponse;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.Pagination;
import com.foreveross.vds.dto.fms.FmsCarCategoryImportTemp;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.fms.service.FmsCarCategoryService;
import com.foreveross.vds.util.tools.ExcelExportUtil;
import com.foreveross.vds.util.tools.FileEncodeUtil;
import com.foreveross.vds.vo.fms.FmsCarCategory;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class FmsCarCategoryController {

    private static Logger logger = LoggerFactory.getLogger(FmsCarCategoryController.class);

    @Autowired
    private FmsCarCategoryService fmsCarCategoryService;

    @ResponseBody
    @RequestMapping("/hello_fms_car_category.do")
    public String helloFmsCarCategory(){
        return "hello!";
    }

    @ResponseBody
    @RequestMapping("/page_fms_car_category.do")
    public Object pageFmsCarCategory(@RequestBody Map<String, Object> params){
        Pagination pagination = null;
        try {
            Integer page = (Integer) params.get("page");
            Integer rows = (Integer) params.get("rows");
            FmsCarCategory fmsCarCategory = JSONObject.parseObject(JSONObject.toJSONString(params.get("whereClause")), FmsCarCategory.class);
            pagination = fmsCarCategoryService.page(fmsCarCategory, page, rows);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return pagination;

    }

    @ResponseBody
    @RequestMapping("/get_all_car_category.do")
    public Object getAllCarCategory(@RequestBody Map<String, Object> params){
        DetailResponse detailResponse = new DetailResponse();
        try {
            FmsCarCategory fmsCarCategory = JSONObject.parseObject(JSONObject.toJSONString(params.get("whereClause")), FmsCarCategory.class);
            List<FmsCarCategory> fmsCarCategoryList = fmsCarCategoryService.query(fmsCarCategory);
            detailResponse.setDetail(fmsCarCategoryList);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return detailResponse;

    }

    @ResponseBody
    @RequestMapping("/insert_fms_car_category.do")
    public DetailResponse insertFmsCarCategory(@RequestBody FmsCarCategory fmsCarCategory){
        DetailResponse<Integer> detailResponse = new DetailResponse();
        detailResponse.setStatus(200);
        try {
            fmsCarCategoryService.checkParams(fmsCarCategory);
            int i = fmsCarCategoryService.insert(fmsCarCategory);
            detailResponse.setDetail(i);
        }catch (BusinessServiceException e){
            logger.warn(e.getMessage());
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setStatus(1);
            detailResponse.setMessage(e.getMessage());
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setStatus(0);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }

    @ResponseBody
    @RequestMapping("/update_fms_car_category.do")
    public DetailResponse updateFmsCarCategory(@RequestBody FmsCarCategory fmsCarCategory){
        DetailResponse<Integer> detailResponse = new DetailResponse();
        try {
            fmsCarCategoryService.checkParams(fmsCarCategory);
            int i = fmsCarCategoryService.update(fmsCarCategory);
            detailResponse.setDetail(i);
        }catch (BusinessServiceException e){
            logger.warn(e.getMessage());
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setStatus(1);
            detailResponse.setMessage(e.getMessage());
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setStatus(0);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }

    @ResponseBody
    @RequestMapping("/delete_fms_car_category.do")
    public DetailResponse deleteFmsCarCategory(@RequestBody Map<String, Object> params){
        DetailResponse<Integer> detailResponse = new DetailResponse();
        try {
            Long carCategoryId = Long.parseLong(String.valueOf(params.get("carCategoryId")));
            int i = fmsCarCategoryService.delete(carCategoryId);
            detailResponse.setDetail(i);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setStatus(0);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;

    }

    @ResponseBody
    @RequestMapping("/get_fms_car_category.do")
    public DetailResponse getFmsCarCategory(@RequestBody Map<String, Object> params){
        DetailResponse<FmsCarCategory> detailResponse = new DetailResponse();
        try {
            Long carCategoryId = (Long) params.get("carCategoryId");
            FmsCarCategory fmsCarCategory = fmsCarCategoryService.get(carCategoryId);
            detailResponse.setDetail(fmsCarCategory);
        }catch (Exception e){
            e.printStackTrace();
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setStatus(0);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }

    @ResponseBody
    @RequestMapping(value = "import_fms_car_category.do")
    public BaseResponse importFmsCarCategory(@RequestBody Map<String, Object> params){
        DetailResponse detailResponse = new DetailResponse();
        try {
            Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String sessionId = String.valueOf(params.get("sessionId"));
            String fileName = String.valueOf(params.get("fileName"));
            String fileString = String.valueOf(params.get("file"));

            Workbook workbook = FileEncodeUtil.parseWorkBoook(fileString, fileName);
            fmsCarCategoryService.importFmsCarCategory(userId, sessionId, workbook);
        }catch (BusinessServiceException e){
            logger.error(e.getMessage(), e);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setStatus(1);
            detailResponse.setMessage(e.getMessage());
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setStatus(0);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }

    @ResponseBody
    @RequestMapping("/export_fms_car_category.do")
    public Object exportFmsCarCategory(@RequestBody Map<String, Object> params) {
        DetailResponse detailResponse = new DetailResponse();
        try {
            FmsCarCategory fmsCarCategory = JSONObject.parseObject(JSONObject.toJSONString(params.get("whereClause")), FmsCarCategory.class);

            List<FmsCarCategory> list = fmsCarCategoryService.listByWhereCause(fmsCarCategory);
            fmsCarCategoryService.translate(list);
            detailResponse.setDetail(ExcelExportUtil.exportToString(list));
        } catch (Exception e) {
            detailResponse.setStatus(0);
            logger.error(e.getMessage(), e);
        }
        return detailResponse;
    }

    @ResponseBody
    @RequestMapping("/export_fms_car_category_import_temp.do")
    public Object exportFmsCarCategoryImportTemp(@RequestBody Map<String, Object> params) {
        DetailResponse detailResponse = new DetailResponse();
        try {
            List list = new ArrayList<FmsCarCategoryImportTemp>();
            list.add(new FmsCarCategoryImportTemp());
            detailResponse.setDetail(ExcelExportUtil.exportToString(list));
        } catch (Exception e) {
            detailResponse.setStatus(0);
            logger.error(e.getMessage(), e);
        }
        return detailResponse;
    }



}
