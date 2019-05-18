package com.foreveross.vds.service.fms.controller;

import com.alibaba.fastjson.JSONObject;
import com.foreveross.vds.constants.CommonErrorCode;
import com.foreveross.vds.dto.BaseResponse;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.Pagination;
import com.foreveross.vds.dto.fms.FmsCarCategoryRealationImportDto;
import com.foreveross.vds.dto.fms.FmsCarCategoryRealationImportTemp;
import com.foreveross.vds.dto.fms.FmsCarCategoryReleationRequest;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.fms.service.FmsCarCategoryReleationService;
import com.foreveross.vds.util.tools.ExcelExportUtil;
import com.foreveross.vds.util.tools.FileEncodeUtil;
import com.foreveross.vds.vo.fms.FmsCarCategoryReleation;
import com.foreveross.vds.vo.inv.InvCarSeries;
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
public class FmsCarCategoryReleationController {

    private static Logger logger = LoggerFactory.getLogger(FmsCarCategoryReleationController.class);

    @Autowired
    private FmsCarCategoryReleationService fmsCarCategoryReleationService;

    @ResponseBody
    @RequestMapping(value = "fms_car_category_releation_query.do")
    public Pagination fmsCarCategoryReleationQuery(@RequestBody Map<String, Object> params) {
        Pagination pagination = null;
        try {
            Integer page = (Integer) params.get("page");
            Integer rows = (Integer) params.get("rows");
            FmsCarCategoryReleationRequest fmsCarCategoryReleationRequest = JSONObject.parseObject(JSONObject.toJSONString(params.get("whereClause")), FmsCarCategoryReleationRequest.class);
            

            pagination = fmsCarCategoryReleationService.pageRealation(page, rows, fmsCarCategoryReleationRequest);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return pagination;
    }

    @ResponseBody
    @RequestMapping(value = "inv_car_series_left_query.do")
    public Pagination invCarSeriesLeftQuery(@RequestBody Map<String, Object> params) {
        Pagination pagination = null;
        try {
            Integer page = (Integer) params.get("page");
            Integer rows = (Integer) params.get("rows");
            String carSeriesCode = (String) params.get("carSeriesCode");
            Long carCategoryId = Long.valueOf(String.valueOf(params.get("carCategoryId")));

            pagination = fmsCarCategoryReleationService.leftQuery(page, rows, carSeriesCode, carCategoryId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return pagination;
    }

    @ResponseBody
    @RequestMapping("delete_fms_car_category_releation.do")
    public DetailResponse deleteFmsCarCategory(@RequestBody Map<String, Object> params){
        DetailResponse<Integer> detailResponse = new DetailResponse();
        try {
            Long carCategoryReleationId = Long.parseLong(String.valueOf(params.get("carCategoryReleationId")));
            System.out.println(carCategoryReleationId);
            int i = fmsCarCategoryReleationService.delete(carCategoryReleationId);
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
    @RequestMapping(value = "inv_car_series_right_query.do")
    public Pagination invCarSeriesRightQuery(@RequestBody Map<String, Object> params)  {
        Pagination pagination = null;
        try {
            Integer page = (Integer) params.get("page");
            Integer rows = (Integer) params.get("rows");
            Long carCategoryId = Long.valueOf(String.valueOf(params.get("carCategoryId")));
            pagination = fmsCarCategoryReleationService.rightQuery(page, rows, carCategoryId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return pagination;
    }

    @ResponseBody
    @RequestMapping(value = "add_fms_car_category_releation.do")
    public BaseResponse addFmsCarCategoryReleation(@RequestBody Map<String, Object> params) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String sessionId = (String) params.get("sessionId");
            FmsCarCategoryRealationImportDto fmsCarCategoryRealationImportDto = JSONObject.parseObject(JSONObject.toJSONString(params.get("fmsCarCategoryRealationImportDto")), FmsCarCategoryRealationImportDto.class);
            fmsCarCategoryReleationService.addRealationNew(userId, fmsCarCategoryRealationImportDto, sessionId);
        } catch (Exception e) {
            baseResponse.setStatus(0);
            logger.error(e.getMessage(), e);
        }
        return baseResponse;
    }

    @ResponseBody
    @RequestMapping(value = "import_fms_car_category_releation.do")
    public BaseResponse importFmsCarCategoryOrgRealation(@RequestBody Map<String, Object> params){
        DetailResponse detailResponse = new DetailResponse();
        try {
            Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String sessionId = String.valueOf(params.get("sessionId"));
            String fileName = String.valueOf(params.get("fileName"));
            String fileString = String.valueOf(params.get("file"));

            Workbook workbook = FileEncodeUtil.parseWorkBoook(fileString, fileName);
            fmsCarCategoryReleationService.importFmsCarCategoryReleation(userId, sessionId, workbook);
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
    @RequestMapping("/export_fms_car_category_releation.do")
    public Object exportFmsCarCategoryReleation(@RequestBody Map<String, Object> params) {
        DetailResponse detailResponse = new DetailResponse();
        try {
            FmsCarCategoryReleationRequest fmsCarCategoryOrgRealationRequest = JSONObject.parseObject(JSONObject.toJSONString(params.get("whereClause")), FmsCarCategoryReleationRequest.class);

            List<FmsCarCategoryReleation> fmsCarCategoryOrgRealations = fmsCarCategoryReleationService.selectRelationByWhereClause(fmsCarCategoryOrgRealationRequest);
            fmsCarCategoryReleationService.translation(fmsCarCategoryOrgRealations);
            detailResponse.setDetail(ExcelExportUtil.exportToString(fmsCarCategoryOrgRealations));
        } catch (Exception e) {
            detailResponse.setStatus(0);
            logger.error(e.getMessage(), e);
        }
        return detailResponse;
    }

    @ResponseBody
    @RequestMapping("/export_fms_car_category_releation_import_temp.do")
    public Object exportFmsCarCategoryReleationImportTemp(@RequestBody Map<String, Object> params) {
        DetailResponse detailResponse = new DetailResponse();
        try {
            List list = new ArrayList<FmsCarCategoryRealationImportTemp>();
            list.add(new FmsCarCategoryRealationImportTemp());
            detailResponse.setDetail(ExcelExportUtil.exportToString(list));
        } catch (Exception e) {
            detailResponse.setStatus(0);
            logger.error(e.getMessage(), e);
        }
        return detailResponse;
    }
}
