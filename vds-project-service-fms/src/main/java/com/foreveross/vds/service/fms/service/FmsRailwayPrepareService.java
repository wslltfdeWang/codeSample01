package com.foreveross.vds.service.fms.service;

import com.foreveross.vds.dto.fms.FmsRailwayPrepare;
import com.foreveross.vds.dto.fms.FmsRailwayPrepareV;
import com.github.pagehelper.Page;

public interface FmsRailwayPrepareService {
	/**
	 * 分页查询铁路前端倒运费用视图
	 * @param fmsRailwayPrepareV
	 * @return
	 */
	Page<FmsRailwayPrepareV> queryFmsRailwayPrepareVByPage(FmsRailwayPrepareV fmsRailwayPrepareV);
	/**
	 * 添加铁路前度倒运费数据
	 * @param fmsRailwayPrepare
	 */
	void addFmsRailwayPrepare(FmsRailwayPrepare fmsRailwayPrepare);
	/**
	 * 修改铁路前度倒运费数据
	 * @param fmsRailwayPrepare
	 */
	void updateFmsRailwayPrepare(FmsRailwayPrepare fmsRailwayPrepare);
	/**
	 * 删除铁路前度倒运费数据
	 * @param fmsRailwayPrepare
	 */
	void delFmsRailwayPrepare(FmsRailwayPrepare fmsRailwayPrepare);
}
