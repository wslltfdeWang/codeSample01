package com.foreveross.vds.service.fms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.foreveross.vds.dto.fms.DealerRecieveAddressVRequest;
import com.foreveross.vds.dto.fms.FmsChangeAddressRecordRequest;
import com.foreveross.vds.dto.fms.FmsExportChangeAddressAttachments;
import com.foreveross.vds.vo.fms.FmsUploadDir;
import com.foreveross.vds.vo.fms.FmsUploadFiles;
import com.github.pagehelper.Page;

@Mapper
public interface FmsChangeAddressRecordMapper {

	int updateSettleFlag(FmsChangeAddressRecordRequest fmsChangeAddressRecordRequest);

	int insertApply(FmsChangeAddressRecordRequest fmsChangeAddressRecordRequest);

	List<DealerRecieveAddressVRequest> selectNames(DealerRecieveAddressVRequest dealerRecieveAddressVRequest);

	DealerRecieveAddressVRequest selectIdByName(String nowAddress);

	List<FmsChangeAddressRecordRequest> selectByWhereClause(FmsChangeAddressRecordRequest whereClause);

	Long insertUploadDir(FmsUploadDir fmsUploadDir);

	void updateUploadDirId(FmsChangeAddressRecordRequest fmsChangeAddressRecordRequest);

	void insertUploadFiles(FmsUploadFiles fmsUploadFiles);

	FmsChangeAddressRecordRequest selectById(Long changeAddressId);

	int approve(FmsChangeAddressRecordRequest fmsChangeAddressRecordRequest);

	int approveSendcar(FmsChangeAddressRecordRequest fmsChangeAddressRecordRequest);

	List<FmsExportChangeAddressAttachments> selectExportById(FmsExportChangeAddressAttachments whereClause);
}
