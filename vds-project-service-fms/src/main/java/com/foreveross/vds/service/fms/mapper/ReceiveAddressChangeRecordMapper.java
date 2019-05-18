package com.foreveross.vds.service.fms.mapper;

import java.util.List;

import com.foreveross.vds.dto.fms.FmsSendcarFeeRelate;
import com.foreveross.vds.dto.fms.ReceiveAddressChangeRecord;
import com.foreveross.vds.dto.fms.ReceiveAddressChangeV;
import com.foreveross.vds.dto.fms.SendcarHeaderChangeV;
import com.foreveross.vds.dto.pms.PmsDcsCrmDealerInter;
import com.foreveross.vds.dto.pms.TransportRuleHeader;
import com.foreveross.vds.dto.tms.TmsSendcarHeader;
import com.foreveross.vds.vo.fms.FmsTransportContractLine;

public interface ReceiveAddressChangeRecordMapper {
	/**
	 * 查询待变更送车交接单数据
	 * @param sendcarHeaderChangeV
	 * @return
	 */
	List<SendcarHeaderChangeV> querySendcarHeaderChangeV(SendcarHeaderChangeV sendcarHeaderChangeV);
	/**
	 * 查询变更记录
	 * @param receiveAddressChangeV
	 * @return
	 */
	List<ReceiveAddressChangeV> queryReceiveAddressChangeV(ReceiveAddressChangeV receiveAddressChangeV);
	/**
	 * 查询收车单位
	 * @param pmsDcsCrmDealerInter
	 * @return
	 */
	List<PmsDcsCrmDealerInter> queryPmsDcsCrmDealerInter(PmsDcsCrmDealerInter pmsDcsCrmDealerInter);
	/**
	 * 添加地址变更记录
	 * @param receiveAddressChangeRecord
	 */
	void addReceiveAddressChangeRecord(ReceiveAddressChangeRecord receiveAddressChangeRecord);
	/**
	 * 修改送车交接单头表
	 * @param tmsSendcarHeader
	 */
	void updateReceiveAddressChangeRecord(TmsSendcarHeader tmsSendcarHeader);
	/**
	 * 根据ID获取送车交接单头表
	 * @param tmsSendcarHeader
	 * @return
	 */
	TmsSendcarHeader queryTmsSendcarHeaderById(Long sendcarHeaderId);
	/**
	 * 修改送车交接单费用关联表
	 * @param fmsSendcarFeeRelate
	 */
	void updateFmsSendcarFeeRelate(FmsSendcarFeeRelate fmsSendcarFeeRelate);
	/**
	 * 根据ID送车交接单费用关联表
	 * @param fmsSendcarFeeRelate
	 * @return
	 */
	FmsSendcarFeeRelate queryFmsSendcarFeeRelateById(Long endcarFeeRelateId);
	/**
	 * 根据库存组织终点获取默认发运规则头
	 * @param transportRuleHeader
	 * @return
	 */
	TransportRuleHeader queryTransportRuleHeader(TransportRuleHeader transportRuleHeader);
	/**
	 * 修改送车交接单头表
	 * @param tmsSendcarHeader
	 */
	void updateTmsSendcarHeader(TmsSendcarHeader tmsSendcarHeader);
	/**
	 * 根据库存组织、终点省市县和发运方式获取合同头和行ID
	 * @param fmsTransportContractLine
	 * @return
	 */
	FmsTransportContractLine queryFmsTransportContractLine(FmsTransportContractLine fmsTransportContractLine);
	/**
	 * 根据送车交接单头ID修改送车交接单费用关联表中合同头和行ID
	 * @param fmsSendcarFeeRelate
	 */
	void updateFmsSendcarFeeRelateContract(FmsSendcarFeeRelate fmsSendcarFeeRelate);
}
