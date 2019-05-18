package com.foreveross.vds.service.fms.service.manualclearing;

import java.util.List;

import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledHeader;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledLine;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledLineV;
import com.github.pagehelper.Page;

public interface AuditManualSettledServce {
	/**
	 * 分页查询手工结算头数据
	 * @param fmsManualSettledHeader
	 * @return
	 */
	Page<FmsManualSettledHeader> queryFmsManualSettledHeaderPage(FmsManualSettledHeader fmsManualSettledHeader);
	/**
	 * 修改手工结算头数据
	 * @param manualSettledHeaderId
	 */
	void updateFmsManualSettledHeader(FmsManualSettledHeader fmsManualSettledHeader);
	/**
	 * 查询手工结算行数据
	 * @param fmsManualSettledLine
	 * @return
	 */
	List<FmsManualSettledLine> queryFmsManualSettledLine(FmsManualSettledLine fmsManualSettledLine);
	/**
	 * 修改手工结算行数据
	 * @param manualSettledLineId
	 */
	void updateFmsManualSettledLine(FmsManualSettledLine fmsManualSettledLine);
	/**
	 * 查询手工结算行数据视图
	 * @param fmsManualSettledLineV
	 * @return
	 */
	List<FmsManualSettledLineV> queryFmsManualSettledLineV(FmsManualSettledLineV fmsManualSettledLineV);
}
