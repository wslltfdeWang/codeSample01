package com.foreveross.vds.service.fms.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.fms.DealerRecieveAddressVRequest;
import com.foreveross.vds.dto.fms.FmsChangeAddressRecordRequest;
import com.foreveross.vds.dto.fms.FmsExportChangeAddressAttachments;
import com.foreveross.vds.service.fms.mapper.FmsChangeAddressRecordMapper;
import com.foreveross.vds.service.fms.service.FmsChangeAddressRecordService;
import com.foreveross.vds.vo.fms.FmsSettleTransportChargeV;
import com.foreveross.vds.vo.fms.FmsUploadDir;
import com.foreveross.vds.vo.fms.FmsUploadFiles;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class FmsChangeAddressRecordServiceImpl implements FmsChangeAddressRecordService {

	@Autowired
	FmsChangeAddressRecordMapper fmsChangeAddressRecordMapper;

	@Override
	public int updateSettleFlag(FmsChangeAddressRecordRequest fmsChangeAddressRecordRequest) {
		return fmsChangeAddressRecordMapper.updateSettleFlag(fmsChangeAddressRecordRequest);
	}

	@Override
	public int insertApply(FmsChangeAddressRecordRequest fmsChangeAddressRecordRequest) {
		return fmsChangeAddressRecordMapper.insertApply(fmsChangeAddressRecordRequest);
	}

	@Override
	public List<DealerRecieveAddressVRequest> selectByNames(DealerRecieveAddressVRequest dealerRecieveAddressVRequest) {
		return fmsChangeAddressRecordMapper.selectNames(dealerRecieveAddressVRequest);
	}

	@Override
	public DealerRecieveAddressVRequest selectIdByName(String nowAddress) {
		return fmsChangeAddressRecordMapper.selectIdByName(nowAddress);
	}

	@Override
	@Transactional
	public String importChangeSettleTransportDir(InputStream inputStream, String fileName, Long userId,
			String sessionId, Long changeAddressId, String uploadUrl) {

		String urlUpLoadFile = uploadUrl + "changeAddressFile";

		File file = new File(urlUpLoadFile);
		if (!file.exists()) {
			file.mkdir();
		}

		File fileForTrans = new File(urlUpLoadFile + "\\" + changeAddressId);
		if (!fileForTrans.exists()) {
			fileForTrans.mkdir();
		}
		
		File fileNameFile = new File(urlUpLoadFile + "\\" + changeAddressId + "\\" + fileName);
		if(fileNameFile.exists()) {
			return "存储异常，文件已存在与路径"
					+ urlUpLoadFile + "\\" + changeAddressId;
		}
		
		try {
			
			FmsChangeAddressRecordRequest fmsChangeAddressRecordRequest = new FmsChangeAddressRecordRequest();
			fmsChangeAddressRecordRequest = fmsChangeAddressRecordMapper.selectById(changeAddressId);
			
			Date date = new Date();
			
			if(fmsChangeAddressRecordRequest.getUploadDirId() == null) {
				FmsUploadDir fmsUploadDir = new FmsUploadDir();
				fmsUploadDir.setDir(urlUpLoadFile + "\\" + changeAddressId);
				fmsUploadDir.setCreatedBy(userId);
				fmsUploadDir.setCreationDate(date);
				fmsUploadDir.setLastUpdateLogin(String.valueOf(userId));
				fmsUploadDir.setLastUpdatedBy(userId);
				fmsUploadDir.setLastUpdateDate(date);;
				Long id = fmsChangeAddressRecordMapper.insertUploadDir(fmsUploadDir);//插入文件夹的路径表
				fmsChangeAddressRecordRequest.setUploadDirId(fmsUploadDir.getUploadDirId());
			}
			
			fmsChangeAddressRecordRequest.setUploadDirId(fmsChangeAddressRecordRequest.getUploadDirId());
			fmsChangeAddressRecordRequest.setLastUpdateDate(date);
			fmsChangeAddressRecordRequest.setLastUpdatedBy(userId);
			fmsChangeAddressRecordRequest.setLastUpdateLogin(String.valueOf(userId));
			fmsChangeAddressRecordMapper.updateUploadDirId(fmsChangeAddressRecordRequest);//修改申请的存储文件保存路径
			
			FmsUploadFiles fmsUploadFiles = new FmsUploadFiles();
			fmsUploadFiles.setUploadDirId(fmsChangeAddressRecordRequest.getUploadDirId());
			fmsUploadFiles.setDir(urlUpLoadFile + "\\" + changeAddressId);
			fmsUploadFiles.setFileName(fileName);
			fmsUploadFiles.setUploadDate(date);
			fmsUploadFiles.setUploadedBy(userId);
			fmsUploadFiles.setCreatedBy(userId);
			fmsUploadFiles.setCreationDate(date);
			fmsUploadFiles.setLastUpdateLogin(String.valueOf(userId));
			fmsUploadFiles.setLastUpdatedBy(userId);
			fmsUploadFiles.setLastUpdateDate(date);;
			fmsChangeAddressRecordMapper.insertUploadFiles(fmsUploadFiles);//插入文件信息
			
			// 获取输出流
						System.out.println(urlUpLoadFile + "\\" + changeAddressId);
						OutputStream os = new FileOutputStream(urlUpLoadFile + "\\" + changeAddressId + "\\" + fileName);
						// 获取输入流 CommonsMultipartFile 中可以直接得到文件的流
						int temp;
						// 一个一个字节的读取并写入
						while ((temp = inputStream.read()) != (-1)) {
							os.write(temp);
						}
						os.flush();
						os.close();
						inputStream.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "文件异常，附件添加失败";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "存储异常，附件添加失败";
		}

		return "附件添加成功";
	}

	@Override
	public BasePageResponse pageFmsSettleTransportChargeTree(Integer pageIndex, Integer pageSize,
			FmsChangeAddressRecordRequest whereClause) {
		PageHelper.startPage(pageIndex, pageSize, whereClause.getOrderBy());

		List<FmsChangeAddressRecordRequest>  fmsChangeAddressRecordRequestList= fmsChangeAddressRecordMapper.selectByWhereClause(whereClause);
		Page<FmsChangeAddressRecordRequest> page = (Page<FmsChangeAddressRecordRequest>) fmsChangeAddressRecordRequestList;
		BasePageResponse basePageResponse = new BasePageResponse(page.getTotal(), page.getResult());
		return basePageResponse;
	}

	@Override
	@Transactional
	public String approveChangeAddress(List<Long> sendcarHeaderIdArray, List<Long> changeAddressIdArray, Long userId,
			String sessionId) {

		try {
			for(int i = 0; i<changeAddressIdArray.size();i++) {
				FmsChangeAddressRecordRequest fmsChangeAddressRecordRequest = new FmsChangeAddressRecordRequest();
				fmsChangeAddressRecordRequest = fmsChangeAddressRecordMapper.selectById(changeAddressIdArray.get(i));
				if(!fmsChangeAddressRecordRequest.getSendcarHeaderId().equals(sendcarHeaderIdArray.get(i))) {
					System.out.println(fmsChangeAddressRecordRequest.getSendcarHeaderId() + "与" + sendcarHeaderIdArray.get(i) + "不同");
					return "数据错误，请联系管理员！";
				}
				Date date = new Date();
				fmsChangeAddressRecordRequest.setLastUpdateDate(date);
				fmsChangeAddressRecordRequest.setLastUpdatedBy(userId);
				fmsChangeAddressRecordRequest.setLastUpdateLogin(sessionId);
				fmsChangeAddressRecordRequest.setApproveBy(userId);
				fmsChangeAddressRecordRequest.setApproveDate(date);
				fmsChangeAddressRecordRequest.setApproveStatus(Long.valueOf(2));
				int flag = fmsChangeAddressRecordMapper.approve(fmsChangeAddressRecordRequest);
				if(flag == 0) {
					return "审核失败";
				}
				flag = fmsChangeAddressRecordMapper.approveSendcar(fmsChangeAddressRecordRequest);
				if(flag == 0) {
					return "审核失败";
				}
			}
		}catch(Exception e) {
			return "审核失败";
		}
		
		return "审核成功";
	}

	@Override
	public BasePageResponse pageFmsChangeAttachmentsExport(Integer pageIndex, Integer pageSize,
			FmsExportChangeAddressAttachments whereClause) {
		PageHelper.startPage(pageIndex, pageSize, whereClause.getOrderBy());

		List<FmsExportChangeAddressAttachments>  fmsExportChangeAddressAttachments= fmsChangeAddressRecordMapper.selectExportById(whereClause);
		Page<FmsExportChangeAddressAttachments> page = (Page<FmsExportChangeAddressAttachments>) fmsExportChangeAddressAttachments;
		BasePageResponse basePageResponse = new BasePageResponse(page.getTotal(), page.getResult());
		return basePageResponse;
	}
}
