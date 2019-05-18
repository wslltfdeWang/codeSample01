package com.foreveross.vds.service.fms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.fms.FmsStorageSettledArray;
import com.foreveross.vds.dto.fms.FmsStorageSettledDto;
import com.foreveross.vds.service.fms.service.FmsStorageSettledService;

@Controller
@RequestMapping("/fms/storage_settled")
public class FmsStorageSettledController {

	@Autowired
	private FmsStorageSettledService fmsStorageSettledService;
	
	@RequestMapping("/calculation_result")
	@ResponseBody
	public Object calculationResult(
			@RequestBody FmsStorageSettledDto fmsStorageSettledDto
			) {
		List<FmsStorageSettledDto> list = fmsStorageSettledService.calculationResult(fmsStorageSettledDto);
		return new DetailResponse<>(list);
	}
	
	@RequestMapping("/save")
	@ResponseBody
	public Object save(
			@RequestBody FmsStorageSettledArray fmsStorageSettledArray
			) {
		int result = fmsStorageSettledService.save(fmsStorageSettledArray);
		return new DetailResponse<>(result);
	}
	
}
