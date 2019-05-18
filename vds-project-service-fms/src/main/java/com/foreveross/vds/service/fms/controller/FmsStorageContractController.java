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
import com.foreveross.vds.service.fms.service.FmsStorageContractService;
import com.foreveross.vds.util.tools.FileEncodeUtil;
import com.foreveross.vds.vo.fms.FmsStorageContract;
import com.foreveross.vds.vo.fms.FmsStorageContractV;
import com.foreveross.vds.vo.fms.StorageContractRequest;

@Controller
@RequestMapping("/fms/storage_contract")
public class FmsStorageContractController {

	@Autowired
	private FmsStorageContractService fmsStorageContractService;
	
	@RequestMapping("/save")
	@ResponseBody
	public BaseResponse save(
			@RequestBody FmsStorageContract fmsStorageContract
			) {
		return fmsStorageContractService.customSave(fmsStorageContract);
	}
	
	@RequestMapping(value = "/query_page", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponse queryPage(
			@RequestBody StorageContractRequest storageContractRequest
			) {
		BasePageResponse<FmsStorageContract> queryPage = fmsStorageContractService.queryPage(storageContractRequest);
		return new DetailResponse<>(queryPage);
	}
	
	@RequestMapping("/delete")
	@ResponseBody
	public BaseResponse delete(
			@RequestBody FmsStorageContract fmsStorageContract
			) {
		int delete = fmsStorageContractService.deleteByPrimaryKey(fmsStorageContract.getStorageContractId());
		return new DetailResponse<>(delete);
	}
	
	@RequestMapping("/export")
	@ResponseBody
	public BaseResponse export(
			@RequestBody FmsStorageContract fmsStorageContract
			) {
		List<FmsStorageContractV> export = fmsStorageContractService.export(fmsStorageContract);
		return new DetailResponse<>(export);
	}
	
	@RequestMapping("/import")
	@ResponseBody
	public BaseResponse _import(
			@RequestBody Map<String, Object> params
			) {
		BaseResponse baseResponse = new BaseResponse();
		try {
			Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String sessionId = String.valueOf(params.get("sessionId"));
            String fileName = String.valueOf(params.get("fileName"));
            String fileString = String.valueOf(params.get("file"));
            Workbook workbook = FileEncodeUtil.parseWorkBoook(fileString, fileName);
            int result = fmsStorageContractService.importStorageContract(workbook, userId, sessionId);
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
