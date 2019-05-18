package com.foreveross.vds.service.fms.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreveross.vds.dto.common.Cursor;
import com.foreveross.vds.service.common.service.ImportVerifierService;
import com.foreveross.vds.service.common.service.impl.BaseServiceImpl;
import com.foreveross.vds.service.fms.service.DistanceLineService;
import com.foreveross.vds.service.fms.service.FmsDistanceHeaderService;
import com.foreveross.vds.service.fms.service.FmsDistanceVService;
import com.foreveross.vds.util.exception.ServiceException;
import com.foreveross.vds.util.tools.AnalysisExcelUtils;
import com.foreveross.vds.util.tools.AnalyzerTool;
import com.foreveross.vds.vo.fms.FmsDistanceHeader;
import com.foreveross.vds.vo.fms.FmsDistanceLine;
import com.foreveross.vds.vo.fms.FmsDistanceV;
import com.foreveross.vds.vo.inv.FndLookupValues;
import com.foreveross.vds.vo.tms.TmsRegionInfo;

@Service
public class FmsDistanceVServiceImpl extends BaseServiceImpl<FmsDistanceV, Long> implements FmsDistanceVService {

	@Autowired
	private ImportVerifierService importVerifierService;
	@Autowired
	private FmsDistanceHeaderService fmsDistanceHeaderService;
	@Autowired
	private DistanceLineService distanceLineService;
	
	@Override
	@Transactional
	public int importDistance(Workbook workbook, Long userId, String sessionId) {
		int result = 0;
		for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++){
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            Cursor cursor = new Cursor(sheet, sheetIndex);
            result += sheetWork(cursor, userId, sessionId);
        }
		return result;
	}
	
	private int sheetWork(Cursor cursor, Long userId, String sessionId) {
		
		int result = 0;
		boolean flag = true;
		String message = "";
		
		Sheet sheet = null;
		if (flag) {
			sheet = cursor.getSheet();
		}
		
		List<String> list = null;
		if (flag) {
			list = new ArrayList<String>();
			list.add("distanceCode");
			list.add("distanceName");
			list.add("startProvinceName");
			list.add("startCityName");
			list.add("startCountyName");
			list.add("endProvinceName");
			list.add("endCityName");
			list.add("endCountyName");
			list.add("transportMethodName");
			list.add("transportDistance");
			list.add("enabledFlag");
			list.add("startDate");
			list.add("endDate");
		}
		
		// 解析数据
		List<FmsDistanceV> fmsDistanceVList = null;
		if (flag) {
			fmsDistanceVList = new ArrayList<>();
			for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
				Row row = sheet.getRow(rowNum);
				if (row != null) {
					FmsDistanceV fmsDistanceV = null;
					try {
						fmsDistanceV = AnalysisExcelUtils.getValue1(row, list.size(), FmsDistanceV.class, list);
					} catch (Exception e) {
						flag = false;
						message = String.format("Sheet:%s 行:%s 解析数据失败", cursor.getSheetIndex(), rowNum);
						e.printStackTrace();
						break;
					}
					fmsDistanceVList.add(fmsDistanceV);
				}
			}
			
			if (flag) {
				if (fmsDistanceVList.size() == 0) {
					flag = false;
					message = "未解析到数据，请检查导入文件";
				}
			}
		}
		
		// 验证数据
		List<TmsRegionInfo> startRegionInfoList = null;
		List<TmsRegionInfo> endRegionInfoList = null;
		FndLookupValues transportMethod = null;
		
		//封装数据
		Map<String, FmsDistanceHeader> fmsDistanceHeaderMap = null;
		Map<String, List<FmsDistanceLine>> fmsDistanceLineListMap = null;
		if (flag) {
			fmsDistanceHeaderMap = new HashMap<>();
			fmsDistanceLineListMap = new HashMap<>();
			int i = 2;
			Date date = new Date();
			for (FmsDistanceV fmsDistanceV : fmsDistanceVList) {
				cursor.setRow(sheet.getRow(i), i+1);
				
				cursor.setCellIndex(3);
				startRegionInfoList = importVerifierService.verifyRegionInfo(
						fmsDistanceV.getStartProvinceName(), 
						fmsDistanceV.getStartCityName(), 
						fmsDistanceV.getStartCountyName(), 
						cursor);
				
				cursor.setCellIndex(6);
				endRegionInfoList = importVerifierService.verifyRegionInfo(
						fmsDistanceV.getEndProvinceName(), 
						fmsDistanceV.getEndCityName(), 
						fmsDistanceV.getEndCountyName(), 
						cursor);
				
				cursor.setCellIndex(12);
				importVerifierService.verifyEnabledFlag(fmsDistanceV.getEnabledFlag(), cursor);
				cursor.setCellIndex(13);
				importVerifierService.verifyEffectiveDate(fmsDistanceV.getStartDate(), fmsDistanceV.getEndDate(), cursor);
				
				cursor.setCellIndex(8);
				transportMethod = importVerifierService.verifyLookupName(fmsDistanceV.getTransportMethodName(), "TRANSPORT_METHOD", cursor);
				
				importVerifierService.verifyEffectiveRepeat(fmsDistanceV,fmsDistanceVList,i-2,cursor);
				
				i++;
				
				//封装数据
				fmsDistanceV.setCreatedBy(userId);
				fmsDistanceV.setCreationDate(date);
				fmsDistanceV.setLastUpdateDate(date);
				fmsDistanceV.setLastUpdatedBy(userId);
				fmsDistanceV.setLastUpdateLogin(sessionId);
				
				// 运距头表
				if (fmsDistanceHeaderMap.get(fmsDistanceV.getDistanceCode()) == null) {
					fmsDistanceHeaderMap.put(fmsDistanceV.getDistanceCode(), AnalyzerTool.toObject(fmsDistanceV, FmsDistanceHeader.class));
				}
				// 运距行表
				FmsDistanceLine fmsDistanceLine = AnalyzerTool.toObject(fmsDistanceV, FmsDistanceLine.class);
				fmsDistanceLine.setStartProvinceId(startRegionInfoList.get(0).getRegionId());
				fmsDistanceLine.setStartCityId(startRegionInfoList.get(1).getRegionId());
				fmsDistanceLine.setStartCountyId(startRegionInfoList.get(2).getRegionId());
				fmsDistanceLine.setEndProvinceId(endRegionInfoList.get(0).getRegionId());
				fmsDistanceLine.setEndCityId(endRegionInfoList.get(1).getRegionId());
				fmsDistanceLine.setEndCountyId(endRegionInfoList.get(2).getRegionId());
				fmsDistanceLine.setTransportMethod(transportMethod.getLookupId());
				List<FmsDistanceLine> lineList = fmsDistanceLineListMap.get(fmsDistanceV.getDistanceCode());
				if (lineList == null) {
					lineList = new ArrayList<>();
				}
				lineList.add(fmsDistanceLine);
				fmsDistanceLineListMap.put(fmsDistanceV.getDistanceCode(), lineList);
			}
		}
		
		// 保存
		if (flag) {
			// 先保存头表，再根据头表ID设值给行表的头表ID
			for (Entry<String, FmsDistanceHeader> entry : fmsDistanceHeaderMap.entrySet()) {
				fmsDistanceHeaderService.quickSave(entry.getValue());
				fmsDistanceLineListMap.get(entry.getKey()).forEach(line -> line.setDistanceHeaderId(entry.getValue().getDistanceHeaderId()));
			}
			// 保存行表
			fmsDistanceLineListMap.values().forEach(lineList -> lineList.forEach(line -> distanceLineService.quickSave(line)));
		}
		
		if (!flag) {
			throw new ServiceException("0", message);
		}
		
		return result;
    }

}
