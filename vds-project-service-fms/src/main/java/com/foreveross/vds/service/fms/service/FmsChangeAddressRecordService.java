package com.foreveross.vds.service.fms.service;

import java.io.InputStream;
import java.util.List;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.fms.DealerRecieveAddressVRequest;
import com.foreveross.vds.dto.fms.FmsChangeAddressRecordRequest;
import com.foreveross.vds.dto.fms.FmsExportChangeAddressAttachments;

public interface FmsChangeAddressRecordService {

	int updateSettleFlag(FmsChangeAddressRecordRequest fmsChangeAddressRecordRequest);

	int insertApply(FmsChangeAddressRecordRequest fmsChangeAddressRecordRequest);

	List<DealerRecieveAddressVRequest> selectByNames(DealerRecieveAddressVRequest dealerRecieveAddressVRequest);

	DealerRecieveAddressVRequest selectIdByName(String nowAddress);

	String importChangeSettleTransportDir(InputStream inputStream, String fileName, Long userId, String sessionId,
			Long sendcarHeaderId,String uploadUrl);

	BasePageResponse pageFmsSettleTransportChargeTree(Integer page, Integer rows,
			FmsChangeAddressRecordRequest whereClause);

	String approveChangeAddress(List<Long> sendcarHeaderIdArray, List<Long> changeAddressIdArray, Long userId,
			String sessionId);

	BasePageResponse pageFmsChangeAttachmentsExport(Integer page, Integer rows,
			FmsExportChangeAddressAttachments whereClause);

}
