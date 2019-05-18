package com.foreveross.vds.service.fms.mapper;

import java.util.List;

import com.foreveross.vds.dto.fms.FmsRailwayPrepare;
import com.foreveross.vds.dto.fms.FmsRailwayPrepareV;
import com.foreveross.vds.service.common.mapper.BaseMapper;

public interface FmsRailwayPrepareMapper extends BaseMapper<FmsRailwayPrepareMapper, Long> {
	/**
	 * 获取铁路前端倒运费用视图
	 * @param fmsRailwayPrepareV
	 * @return
	 */
	List<FmsRailwayPrepareV> queryFmsRailwayPrepareV(FmsRailwayPrepareV fmsRailwayPrepareV);
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
