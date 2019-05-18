package com.foreveross.vds.service.fms.mapper.manualclearing;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.foreveross.vds.dto.fms.manualclearing.ContractAllV;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettled;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledHeader;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledLine;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledV;

public interface ManualSettledMapper {
	
	/**
	 * 修改不为空的数据
	 * @param fmsManualSettled
	 */
	void updateFmsManualSettledSelective(FmsManualSettled fmsManualSettled);
	/**
	 * 查询手工结算数据视图
	 * @param fmsManualSettledV
	 * @return
	 */
	List<FmsManualSettledV> queryFmsManualSettledV(FmsManualSettledV fmsManualSettledV);
	/**
	 * 根据ID获取结算数据
	 * @param manualSettledId
	 * @return
	 */
	FmsManualSettled getFmsManualSettledById(Long manualSettledId);
	/**
	 * 添加手工结算数据
	 * @param fmsManualSettled
	 */
	void addFmsManualSettled(FmsManualSettled fmsManualSettled);
	/**
	 * 修改手工结算数据
	 * @param fmsManualSettled
	 */
	void updateFmsManualSettled(FmsManualSettled fmsManualSettled);
	/**
	 * 删除手工结算数据
	 * @param manualSettledId
	 */
	void delFmsManualSettled(Long manualSettledId);
	/**
	 * 判断手工结算数据名称是否存在
	 * @param fmsManualSettled
	 * @return
	 */
	List<FmsManualSettled> checkSettledName(FmsManualSettled fmsManualSettled);
	/**
	 * 查询仓储合同和运输合同视图
	 * @param contractAllV
	 * @return
	 */
	List<ContractAllV> queryContractAllV(ContractAllV contractAllV);
	/**
	 * 获取结算批次编码
	 * @param manualSettledId
	 * @return
	 */
	String getBatchCode(Long manualSettledId);
	/**
	 * 获取批次序号
	 * @return
	 */
	String getLineNumber();
	/**
	 * 添加结算数据头数据
	 * @param fmsManualSettledHeader
	 */
	void addFmsManualSettledHeader(FmsManualSettledHeader fmsManualSettledHeader);
	/**
	 * 添加结算数据行数据
	 * @param fmsManualSettledLine
	 */
	void addFmsFmsManualSettledLine(FmsManualSettledLine fmsManualSettledLine);
}
