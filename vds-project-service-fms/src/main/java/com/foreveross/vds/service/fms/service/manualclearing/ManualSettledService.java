package com.foreveross.vds.service.fms.service.manualclearing;

import java.util.List;

import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.manualclearing.ContractAllV;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettled;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledHeader;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledLine;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledV;
import com.github.pagehelper.Page;

public interface ManualSettledService {
	/**
	 * 分页查询手工结算数据视图
	 * @param fmsManualSettledV
	 * @return
	 */
	Page<FmsManualSettledV> queryFmsManualSettledVByPage(FmsManualSettledV fmsManualSettledV);
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
	boolean checkSettledName(FmsManualSettled fmsManualSettled);
	/**
	 * 分页查询仓储合同和运输合同视图
	 * @param contractAllV
	 * @return
	 */
	Page<ContractAllV> queryContractAllVPage(ContractAllV contractAllV);
	/**
	 * 生成结算批次
	 * @param fmsManualSettledV
	 */
	RecRequest createBatchCode(List<FmsManualSettledV> fmsManualSettledVList);
	/**
	 * 仓储合同生成结算批次
	 * @param fmsManualSettledV
	 */
	RecRequest createStorageBatchCode(List<FmsManualSettledV> fmsManualSettledVList);
	/**
	 * 获取结算批次编码
	 * @param manualSettledId
	 * @return
	 */
	String getBatchCode(Long manualSettledId);
	/**
	 * 添加结算数据头数据
	 * @param fmsManualSettledHeader
	 */
	FmsManualSettledHeader addFmsManualSettledHeader(FmsManualSettledHeader fmsManualSettledHeader);
	/**
	 * 添加结算数据行数据
	 * @param fmsManualSettledLine
	 */
	void addFmsFmsManualSettledLine(FmsManualSettledLine fmsManualSettledLine);
	/**
	 * 根据ID获取结算数据
	 * @param manualSettledId
	 * @return
	 */
	FmsManualSettled getFmsManualSettledById(Long manualSettledId);
}
