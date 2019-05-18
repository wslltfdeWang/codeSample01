package com.foreveross.vds.service.fms.controller;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.vds.dto.BaseResponse;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.fms.DistanceLineRequest;
import com.foreveross.vds.service.fms.service.DistanceLineService;
import com.foreveross.vds.service.fms.service.FmsDistanceVService;
import com.foreveross.vds.util.tools.FileEncodeUtil;
import com.foreveross.vds.vo.fms.FmsDistanceLine;
import com.foreveross.vds.vo.fms.FmsDistanceV;

/**
 * 预算-运距行表接口
 */
@Controller
@RequestMapping("/fms/distance/line")
public class DistanceLineController {
	
	@Autowired
	private DistanceLineService distanceLineServices;
	@Autowired
	private FmsDistanceVService fmsDistanceVService;
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponse save(@RequestBody FmsDistanceLine fmsDistanceLine){
		List<FmsDistanceLine> queryFlag = distanceLineServices.queryList(fmsDistanceLine);
		int quickSave = 0;
		if(queryFlag.size() == 0) {
			quickSave = distanceLineServices.quickSave(fmsDistanceLine);
			return new DetailResponse<>(quickSave);
		}else {
			 DetailResponse<Integer> detailResponse = new DetailResponse<>(quickSave);
			 detailResponse.setStatus(500);
			 detailResponse.setError("存在重复数据！");
			 return detailResponse;
		}
		
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponse delete(@RequestBody FmsDistanceLine fmsDistanceLine) {
		int delete = distanceLineServices.deleteByPrimaryKey(fmsDistanceLine.getDistanceLineId());
		return new DetailResponse<>(delete);
	}
	
	@RequestMapping(value = "/query_list", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponse queryList(@RequestBody DistanceLineRequest distanceLineRequest) {
		BasePageResponse<FmsDistanceLine> queryPage = distanceLineServices.queryPage(distanceLineRequest);
		return new DetailResponse<>(queryPage);
	}
	
	@RequestMapping("/export")
	@ResponseBody
	public BaseResponse export(@RequestBody FmsDistanceLine fmsDistanceLine) {
		List<FmsDistanceV> queryList = fmsDistanceVService.queryList(fmsDistanceLine);
		return new DetailResponse<>(queryList);
	}
	
	@RequestMapping("/import")
	@ResponseBody
	public BaseResponse _import(@RequestBody Map<String, Object> params) {
		BaseResponse baseResponse = new BaseResponse();
		try {
			Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String sessionId = String.valueOf(params.get("sessionId"));
            String fileName = String.valueOf(params.get("fileName"));
            String fileString = String.valueOf(params.get("file"));
            Workbook workbook = FileEncodeUtil.parseWorkBoook(fileString, fileName);
            int result = fmsDistanceVService.importDistance(workbook, userId, sessionId);
            baseResponse = new DetailResponse<>(result);
		} catch (Exception e) {
			e.printStackTrace();
			baseResponse.setError("0");
			baseResponse.setStatus(0);
			baseResponse.setMessage(e.getMessage());
		}
		return baseResponse;
	}
}
