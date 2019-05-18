package com.foreveross.vds.service.fms.service.impl;

import java.math.BigDecimal;
import java.util.*;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.common.Cursor;
import com.foreveross.vds.service.common.service.ImportVerifierService;
import com.foreveross.vds.service.common.service.impl.BaseServiceImpl;
import com.foreveross.vds.service.fms.mapper.FmsStorageContractMapper;
import com.foreveross.vds.service.fms.service.FmsStorageContractService;
import com.foreveross.vds.util.exception.ServiceException;
import com.foreveross.vds.util.tools.AnalysisExcelUtils;
import com.foreveross.vds.util.tools.AnalyzerTool;
import com.foreveross.vds.util.tools.DateUtil;
import com.foreveross.vds.vo.fms.FmsProvider;
import com.foreveross.vds.vo.fms.FmsStorageContract;
import com.foreveross.vds.vo.fms.FmsStorageContractCategory;
import com.foreveross.vds.vo.fms.FmsStorageContractV;
import com.foreveross.vds.vo.inv.FndLookupValues;
import com.foreveross.vds.vo.inv.InvStorageRoom;
import com.foreveross.vds.vo.tms.TmsDeparture;
import com.foreveross.vds.vo.tms.TmsLogisticsProviders;
import com.foreveross.vds.vo.tms.TmsStartPoint;

/**
 * @author lc
 */
@Service
public class FmsStorageContractServiceImpl extends BaseServiceImpl<FmsStorageContract, Long> implements FmsStorageContractService {


    @Autowired
    private ImportVerifierService importVerifierService;

    @Autowired
    private FmsStorageContractMapper fmsStorageContractMapper;

    public FmsStorageContractServiceImpl() {
        super("storageContractId");
    }

    @Override
    public DetailResponse<Date> customSave(FmsStorageContract record) {
        boolean flag = true;
        String message = "";
        String y = "Y";

        // 确认复核
        if (y.equals(record.getIsConfirmFlag())) {
            super.updateByPrimaryKeySelective(record);
            return new DetailResponse<>();
        }


        // 查询已存在的合同参数
        FmsStorageContract params = null;
        if (flag) {
            params = new FmsStorageContract();
            if (record.getStorageContractId() == null && record.getContractType() == null) {
                params.setContractName(record.getContractName());
                List<FmsStorageContract> queryList = super.queryList(params);
                if (AnalyzerTool.isNotEmpty(queryList)) {
                    for (FmsStorageContract fmsStorageContract : queryList) {
                        if (fmsStorageContract.getContractName().equals(record.getContractName())) {
                            flag = false;
                            message = "合同名称不能重复";
                        }
                    }
                }
                params.setContractName(null);

                if (flag) {
                    params.setContractNumber(record.getContractNumber());
                    List<FmsStorageContract> queryList1 = super.queryList(params);
                    if (AnalyzerTool.isNotEmpty(queryList1)) {
                        for (FmsStorageContract fmsStorageContract : queryList) {
                            if (fmsStorageContract.getContractNumber().equals(record.getContractNumber())) {
                                flag = false;
                                message = "合同号不能重复";
                            }
                        }
                    }
                    params.setContractNumber(null);
                }
            }
        }

        if (flag) {
            params.setOrgId(record.getOrgId());
            params.setLogisticsId(record.getLogisticsId());
            params.setStartPointId(record.getStartPointId());
            params.setStorageRoomId(record.getStorageRoomId());
            params.setContractCategoryId(record.getContractCategoryId());
            params.setStartDate(record.getStartDate());
            params.setEndDate(record.getStartDate());
        }

        // 判断合同开始时间是否与正式合同冲突
        Date lastDate = null;
        if (flag) {
//			params.setContractType(formalContractId);
            List<FmsStorageContract> queryList = super.queryList(params);
            if (AnalyzerTool.isNotEmpty(queryList)) {
                lastDate = queryList.get(0).getEndDate();
                for (FmsStorageContract fmsStorageContract : queryList) {
                    if (lastDate.compareTo(fmsStorageContract.getEndDate()) < 0) {
                        lastDate = fmsStorageContract.getEndDate();
                    }
                }
                flag = false;
                lastDate = DateUtil.add(lastDate, Calendar.DAY_OF_MONTH, 1);
                message = String.format("此合同开始时间与其他正式合同时间冲突，请从：%s 开始", DateUtil.format(lastDate));
                // 修改操作可能查询到自己
                if (record.getStorageContractId() != null) {
                    if (queryList.size() == 1) {
                        if (Objects.equals(queryList.get(0).getStorageContractId(), record.getStorageContractId())) {
                            flag = true;
                        }
                    }
                }
            }

            if (flag) {
                params.setStartDate(record.getEndDate());
                params.setEndDate(record.getEndDate());
                queryList = super.queryList(params);
                if (AnalyzerTool.isNotEmpty(queryList)) {
                    lastDate = queryList.get(0).getStartDate();
                    for (FmsStorageContract fmsStorageContract : queryList) {
                        if (lastDate.compareTo(fmsStorageContract.getStartDate()) > 0) {
                            lastDate = fmsStorageContract.getStartDate();
                        }
                    }
                    flag = false;
                    lastDate = DateUtil.add(lastDate, Calendar.DAY_OF_MONTH, 1);
                    message = String.format("此合同结束时间与其他正式合同时间冲突，请从：%s 开始", DateUtil.format(lastDate));
                    // 修改操作可能查询到自己
                    if (record.getStorageContractId() != null) {
                        if (queryList.size() == 1) {
                            if (queryList.get(0).getStorageContractId().equals(record.getStorageContractId())) {
                                flag = true;
                            }
                        }
                    }
                }
            }
        }

        if (flag) {
            if (record.getStorageContractId() == null) {
                record.setIsConfirmFlag("N");
            }
        }

        if (flag) {
            // 月度总价=单价*数量
            BigDecimal quantity = record.getQuantity();
            BigDecimal unitPrice = record.getUnitPrice();
            if (AnalyzerTool.isAllNotEmpty(quantity, unitPrice)) {
                record.setMonthPrice(quantity.multiply(unitPrice));
            }
        }

        if (flag) {
            super.quickSave(record);
        }

        if (!flag) {
            DetailResponse<Date> detailResponse = new DetailResponse<>(0, message);
            detailResponse.setDetail(lastDate);
            return detailResponse;
        }

        return new DetailResponse<>();
    }

    @Override
    public <P> List<FmsStorageContractV> export(P params) {
        return fmsStorageContractMapper.export(params);
    }

    @Override
    @Transactional
    public int importStorageContract(Workbook workbook, Long userId, String sessionId) {
        int result = 0;
        for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
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
            list.add("contractName");
            list.add("contractNumber");
            list.add("categoryName");
            list.add("startDate");
            list.add("endDate");
            list.add("logisticsShortName");
            list.add("orgName");
            list.add("startPointName");
            list.add("storageRoomName");
            list.add("unitPrice");
            list.add("quantity");
            list.add("enabledFlag");
        }

        // 解析数据
        List<FmsStorageContractV> fmsStorageContractVList = null;
        if (flag) {
            fmsStorageContractVList = new ArrayList<>();
            for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row != null) {
                    FmsStorageContractV fmsStorageContractV = null;
                    try {
                        fmsStorageContractV = AnalysisExcelUtils.getValue1(row, list.size(), FmsStorageContractV.class, list);
                    } catch (Exception e) {
                        flag = false;
                        message = String.format("Sheet:%s 行:%s 解析数据失败", cursor.getSheetIndex(), rowNum);
                        e.printStackTrace();
                        break;
                    }
                    fmsStorageContractVList.add(fmsStorageContractV);
                }
            }

            if (fmsStorageContractVList.size() == 0) {
                flag = false;
                message = "未解析到数据，请检查导入文件";
            }
        }

        // 验证、设值数据
        if (flag) {
            int i = 2;
            Date date = new Date();
            for (FmsStorageContractV item : fmsStorageContractVList) {
                cursor.setRow(sheet.getRow(i), i);

                cursor.setCellIndex(3);
                FmsStorageContractCategory fmsStorageContractCategory = importVerifierService.verifyCategoryName(item.getCategoryName(), cursor);
                item.setContractCategoryId(fmsStorageContractCategory.getContractCategoryId());

                cursor.setCellIndex(6);
                FmsProvider fmsProvider = importVerifierService.verifyProvider(item.getLogisticsShortName(), cursor);
                item.setLogisticsId(fmsProvider.getProviderId());

                cursor.setCellIndex(7);
                FndLookupValues fndLookupValues = importVerifierService.verifyLookupName(item.getOrgName(), "BUSSINESS_UNIT", cursor);
                item.setOrgId(fndLookupValues.getLookupId());

                cursor.setCellIndex(8);
                TmsDeparture tmsDeparture = importVerifierService.verifyStartPointName(item.getStartPointName(), cursor);
                item.setStartPointId(tmsDeparture.getDepartureId());

                cursor.setCellIndex(9);
                InvStorageRoom invStorageRoom = importVerifierService.verifyStorageRoom(item.getStorageRoomName(), cursor);
                item.setStorageRoomId(invStorageRoom.getStorageRoomId());

                cursor.setCellIndex(12);
                importVerifierService.verifyEnabledFlag(item.getEnabledFlag(), cursor);

                item.setCreatedBy(userId);
                item.setCreationDate(date);
                item.setLastUpdateLogin(sessionId);
                item.setLastUpdatedBy(userId);
                item.setLastUpdateDate(date);

                i++;
            }
        }

        // 保存
        if (flag) {
            for (FmsStorageContractV item : fmsStorageContractVList) {
                DetailResponse<Date> response = this.customSave(item);
                if (response.getStatus() != 200) {
                    throw new ServiceException("0", response.getMessage());
                }
            }
        }

        if (!flag) {
            throw new ServiceException("0", message);
        }

        return result;
    }

}
