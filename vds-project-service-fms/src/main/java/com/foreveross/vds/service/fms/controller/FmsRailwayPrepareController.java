package com.foreveross.vds.service.fms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.FmsRailwayPrepare;
import com.foreveross.vds.dto.fms.FmsRailwayPrepareV;
import com.foreveross.vds.service.fms.service.FmsRailwayPrepareService;
import com.github.pagehelper.Page;

/**
 * 铁路前端倒运费用
 * @author foreveross
 *
 */
@Controller
public class FmsRailwayPrepareController {
	@Autowired
	private FmsRailwayPrepareService fmsRailwayPrepareService;
	/**
	 * 分页查询铁路前端倒运费用
	 * @param fmsRailwayPrepareV
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "query_railway_prepare_page")
	public BasePageResponse<FmsRailwayPrepareV> queryRailwayPreparePage(@RequestBody FmsRailwayPrepareV fmsRailwayPrepareV){
		BasePageResponse<FmsRailwayPrepareV> basePageResponse = new BasePageResponse<FmsRailwayPrepareV>();
		
		Page<FmsRailwayPrepareV> page = fmsRailwayPrepareService.queryFmsRailwayPrepareVByPage(fmsRailwayPrepareV);
		
		
		
		basePageResponse.setRows(page.getResult());
		basePageResponse.setTotal(page.getTotal());
		return basePageResponse;
	}
	/**
	 * 添加铁路前端倒运费用
	 * @param fmsRailwayPrepare
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "add_railway_prepare")
	public RecRequest addRailwayPrepare(@RequestBody FmsRailwayPrepare fmsRailwayPrepare){
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		try {
			fmsRailwayPrepareService.addFmsRailwayPrepare(fmsRailwayPrepare);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("数据添加失败，请联系管理员");
		}
		
		return recRequest;
	}
	/**
	 * 修改铁路前端倒运费用
	 * @param fmsRailwayPrepare
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "edit_railway_prepare")
	public RecRequest editRailwayPrepare(@RequestBody FmsRailwayPrepare fmsRailwayPrepare){
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		try {
			fmsRailwayPrepareService.updateFmsRailwayPrepare(fmsRailwayPrepare);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("数据修改失败，请联系管理员");
		}
		
		return recRequest;
	}
	/**
	 * 删除铁路前端倒运费用
	 * @param fmsRailwayPrepare
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "del_railway_prepare")
	public RecRequest delRailwayPrepare(@RequestBody FmsRailwayPrepare fmsRailwayPrepare){
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		try {
			fmsRailwayPrepareService.delFmsRailwayPrepare(fmsRailwayPrepare);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("数据删除失败，请联系管理员");
		}
		
		return recRequest;
	}
}
