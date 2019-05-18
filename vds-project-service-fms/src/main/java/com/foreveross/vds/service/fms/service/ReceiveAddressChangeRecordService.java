package com.foreveross.vds.service.fms.service;

import java.util.List;

import com.foreveross.vds.dto.fms.ReceiveAddressChangeV;
import com.foreveross.vds.dto.fms.SendcarHeaderChangeV;
import com.foreveross.vds.dto.pms.PmsDcsCrmDealerInter;
import com.github.pagehelper.Page;

public interface ReceiveAddressChangeRecordService {
	/**
	 * 分页查询待变更送车交接单数据
	 * @param sendcarHeaderChangeV
	 * @return
	 */
	Page<SendcarHeaderChangeV> querySendcarHeaderChangeV(SendcarHeaderChangeV sendcarHeaderChangeV);
	/**
	 * 分页查询变更记录
	 * @param receiveAddressChangeV
	 * @return
	 */
	Page<ReceiveAddressChangeV> queryReceiveAddressChangeV(ReceiveAddressChangeV receiveAddressChangeV);
	/**
	 * 查询收车单位
	 * @param pmsDcsCrmDealerInter
	 * @return
	 */
	List<PmsDcsCrmDealerInter> queryPmsDcsCrmDealerInter(PmsDcsCrmDealerInter pmsDcsCrmDealerInter);
	/**
	 * 变更收车地址
	 * @param receiveAddressChangeV
	 */
	void changeAdress(ReceiveAddressChangeV receiveAddressChangeV);
}
