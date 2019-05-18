package com.foreveross.vds.service.fms.service;

import java.math.BigDecimal;
import java.util.List;

import com.foreveross.vds.annotation.CustTx;
import com.foreveross.vds.dto.fms.FmsTransportSettledHeader;
import com.foreveross.vds.dto.fms.FmsTransportSettledLine;
import com.foreveross.vds.dto.fms.SettleTransportChargeV;
import com.foreveross.vds.service.common.exception.BusinessServiceException;

public interface FmsCreateSettleTransportChargeService {
	/**
	 * 获取待生成运费结算批次的数据
	 * @param settleTransportChargeV
	 * @return
	 */
	List<SettleTransportChargeV> getSettleTransportChargeV(SettleTransportChargeV settleTransportChargeV);
	/**
	 * 添加运费结算批次行数据
	 * @param fmsTransportSettledLine
	 */
	void addFmsTransportSettledLine(FmsTransportSettledLine fmsTransportSettledLine);
	/**
	 * 添加运费结算批次头数据
	 * @param fmsTransportSettledHeader
	 */
	FmsTransportSettledHeader addFmsTransportSettledHeader(FmsTransportSettledHeader fmsTransportSettledHeader);
	/**
	 * 根据送车交接单头ID获取金额
	 * @param sendcarHeaderId
	 * @return
	 */
	BigDecimal getPrice(Long sendcarHeaderId);
	/**
	 * 修改送车交接单结算标识
	 * @param sendcarHeaderId
	 */
	void updateSendCarHeader(Long sendcarHeaderId);
	/**
	 * 根据规则获取结算批次码
	 * @param sendcarHeaderId
	 * @return
	 */
	String getBatchCode(FmsTransportSettledHeader fmsTransportSettledHeader);

	@CustTx
	void relateData(FmsTransportSettledLine fmsTransportSettledLine) throws BusinessServiceException;
}
