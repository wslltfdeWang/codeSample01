package com.foreveross.vds.service.fms.service.impl.transportContractImport;

import com.foreveross.vds.annotation.CustTx;
import com.foreveross.vds.dto.fms.TransportContract;
import com.foreveross.vds.dto.fms.transportContractImport.*;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.fms.service.*;
import com.foreveross.vds.service.fms.service.transportContractImport.TransportContractImportService;
import com.foreveross.vds.service.fms.service.transportContractImport.TransportContractImportVerifierService;
import com.foreveross.vds.util.tools.StringHelper;
import com.foreveross.vds.vo.fms.*;
import com.foreveross.vds.vo.tms.TmsDeparture;
import com.foreveross.vds.vo.tms.TmsLogisticsProviders;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class TransportContractImportServiceImpl implements TransportContractImportService {

    @Autowired
    private FmsTransportContractHeaderService fmsTransportContractHeaderService;

    @Autowired
    private TransportContractImportVerifierService transportContractImportVerifierService;

    private ThreadLocal<TransportContract> transportContractThreadLocal = new ThreadLocal();//用来记录每次循环时的合同头

    @Override
    @CustTx
    public void importTransportContract(Workbook workbook, Long userId, String sessionId) throws BusinessServiceException {
        for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            Cursor cursor = new Cursor(sheet, sheetIndex);
            sheetWork(cursor, userId, sessionId);
        }
    }

    private void sheetWork(Cursor cursor, Long userId, String sessionId) throws BusinessServiceException {
        //解析表头第2部分
        parseHeadPart2(cursor);

        //解析表头第3部分
        parseHeadPart3(cursor);

        //解析数据
        parseData(cursor, userId, sessionId);
    }

    private void parseHeadPart2(Cursor cursor) throws BusinessServiceException {
        parseTransportMethod(cursor);
        parseCarCategory(cursor);
    }

    private void parseTransportMethod(Cursor cursor) {
        Sheet sheet = cursor.getSheet();
        List<HeadPart2> headPart2List = new ArrayList<>();

        Row row = sheet.getRow(DataIndex.PART2_TRANSPOR_TMETHOD_ROW_INDEX.val);

        String preStringCellValue = "";//上一个cell的值
        HeadPart2 headPart2 = null;
        int flag = 0;
        for (int cellIndex = 0; cellIndex < row.getPhysicalNumberOfCells(); cellIndex++) {
            Cell cell = row.getCell(cellIndex);
            cell.setCellType(CellType.STRING);
            String curStringCellValue = cell.getStringCellValue();

            if (StringHelper.isEmpty(curStringCellValue)) {
                continue;
            }

            TransportMethod transportMethod = transportContractImportVerifierService.verifyTransMethodNoException(curStringCellValue);
            if (transportMethod == null) {
                continue;
            }

            //设置运距名称
            if (!preStringCellValue.equals(curStringCellValue) && flag == 0) {
                headPart2 = new HeadPart2();
                headPart2.setTransMethodName(transportMethod.getTransMethodName());
                headPart2.setTransMethodId(transportMethod.getTransMethodId());
                headPart2.setDistanceNameCellIndex(cellIndex);
                headPart2List.add(headPart2);

                flag++;
                preStringCellValue = curStringCellValue;
                continue;
            }

            //设置运距
            if (flag == 1) {
                headPart2.setDistanceCellIndex(cellIndex);

                flag++;
                preStringCellValue = curStringCellValue;
                continue;
            }


            HeadPart2.Article article = new HeadPart2.Article();
            article.setCellIndex(cellIndex);
            headPart2.addArticle(article);

            flag = 0;
            preStringCellValue = curStringCellValue;
        }

        cursor.setHeadPart2List(headPart2List);
    }

    private void parseCarCategory(Cursor cursor) throws BusinessServiceException {
        Sheet sheet = cursor.getSheet();
        List<HeadPart2> headPart2List = cursor.getHeadPart2List();


        Row row = sheet.getRow(DataIndex.PART2_CAR_CATEGORY_ROW_INDEX.val);
        cursor.setRow(row, DataIndex.PART2_CAR_CATEGORY_ROW_INDEX.val);


        for (HeadPart2 headPart2 : headPart2List) {
            for (HeadPart2.Article article : headPart2.getArticleList()) {
                Integer cellIndex = article.getCellIndex();
                Cell cell = row.getCell(cellIndex);
                cursor.setCell(cell, cellIndex);

                cell.setCellType(CellType.STRING);
                String stringCellValue = cell.getStringCellValue();

                FmsCarCategory fmsCarCategory = transportContractImportVerifierService.verifyCarCategory(stringCellValue, cursor);

                article.setCarCategoryId(fmsCarCategory.getCarCategoryId());
                article.setCarCategoryName(fmsCarCategory.getCarCategoryName());
            }
        }
    }

    private void parseHeadPart3(Cursor cursor) {
        parseCostType(cursor);
        parseTransMethod(cursor);
    }

    private void parseCostType(Cursor cursor) {
        Sheet sheet = cursor.getSheet();
        List<HeadPart3> headPart3List = new ArrayList<>();

        int seq = 1;
        Row row = sheet.getRow(DataIndex.PART3_COST_TYPE_ROW_INDEX.val);
        for (int cellIndex = 0; cellIndex < row.getPhysicalNumberOfCells(); cellIndex++) {
            Cell cell = row.getCell(cellIndex);
            cell.setCellType(CellType.STRING);
            String stringCellValue = cell.getStringCellValue();

            CostType costType = transportContractImportVerifierService.verifyCostType(stringCellValue);
            if (costType == null) {
                continue;
            }

            HeadPart3 headPart3 = new HeadPart3();
            headPart3.setCellIndex(cellIndex);
            headPart3.setSeq(seq);
            headPart3.setCostType(costType.name());
            headPart3List.add(headPart3);
            seq++;
        }

        cursor.setHeadPart3List(headPart3List);
    }

    private void parseTransMethod(Cursor cursor) {
        Sheet sheet = cursor.getSheet();
        List<HeadPart3> headPart3List = cursor.getHeadPart3List();

        Row row = sheet.getRow(DataIndex.PART3_TRANSPOR_TMETHOD_ROW_INDEX.val);
        cursor.setRow(row, DataIndex.PART3_TRANSPOR_TMETHOD_ROW_INDEX.val);

        int seq = 1;
        for (int cellIndex = 0; cellIndex < row.getPhysicalNumberOfCells(); cellIndex++) {
            Cell cell = row.getCell(cellIndex);
            cursor.setCell(cell, cellIndex);

            cell.setCellType(CellType.STRING);
            String stringCellValue = cell.getStringCellValue();

            TransportMethod transportMethod = transportContractImportVerifierService.verifyTransMethod(stringCellValue, cursor);
            if (transportMethod == null) {
                continue;
            }


            for (HeadPart3 headPart3 : headPart3List) {
                if (headPart3.getSeq() == seq) {
                    headPart3.setTransMethodId(transportMethod.getTransMethodId());
                    headPart3.setTransMethodName(transportMethod.getTransMethodName());
                    seq++;
                    break;
                }
            }
        }
    }

    private void parseData(Cursor cursor, Long userId, String sessionId) throws BusinessServiceException {
        Sheet sheet = cursor.getSheet();
        for (int rowIndex = 0; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
            if (rowIndex < DataIndex.DATA_START_ROW_INDEX.val) {//从第4行开始解析数据
                continue;
            }

            Row row = sheet.getRow(rowIndex);
            cursor.setRow(row, rowIndex);
            rowWork(cursor, userId, sessionId);
        }
    }

    private void rowWork(Cursor cursor, Long userId, String sessionId) throws BusinessServiceException {
        TransportContract nowEntity = transportContractThreadLocal.get();
        if(nowEntity == null){
            nowEntity = new TransportContract();
            transportContractThreadLocal.set(nowEntity);
        }

        //解析一行
        List<TransportContract> transportContracts = parseRow(cursor);
        if (nowEntity.getFmsTransportContractHeader().getContractNumber() == null) { //第一次循环初始化
            nowEntity.setFmsTransportContractHeader(transportContracts.get(0).getFmsTransportContractHeader());
        }
        Integer headerId = fmsTransportContractHeaderService.isContractHeaderExists(nowEntity);//存放查询到的合同头ID

        if (headerId == 0) {//如果合同头不存在，则新增一条合同头数据
            fmsTransportContractHeaderService.saveTransportContract(transportContracts.get(0), userId, sessionId);
            headerId = fmsTransportContractHeaderService.isContractHeaderExists(nowEntity);
            nowEntity.getFmsTransportContractHeader().setTransportContractHeaderId(headerId.longValue());
        } else {
            nowEntity.getFmsTransportContractHeader().setTransportContractHeaderId(headerId.longValue());
        }
        for (TransportContract transportContract : transportContracts) {
            if (!Objects.equals(transportContract.getFmsTransportContractHeader().getContractNumber(), nowEntity.getFmsTransportContractHeader().getContractNumber())) {//如果合同头变化
                nowEntity.setFmsTransportContractHeader(transportContract.getFmsTransportContractHeader());
                headerId = fmsTransportContractHeaderService.isContractHeaderExists(nowEntity);//存放查询到的合同头ID
                if (headerId == 0) {//如果合同头不存在，则新增一条合同头数据
                    fmsTransportContractHeaderService.saveTransportContract(nowEntity, userId, sessionId);
                    nowEntity.getFmsTransportContractHeader().setTransportContractHeaderId(headerId.longValue());
                    transportContract.getFmsTransportContractHeader().setTransportContractHeaderId(headerId.longValue());
                } else {
                    nowEntity.getFmsTransportContractHeader().setTransportContractHeaderId(headerId.longValue());
                    transportContract.getFmsTransportContractHeader().setTransportContractHeaderId(headerId.longValue());
                }
            } else {
                transportContract.getFmsTransportContractHeader().setTransportContractHeaderId(headerId.longValue());
            }

            fmsTransportContractHeaderService.saveTransportContractLine(transportContract, userId, sessionId);

        }
    }

    private List<TransportContract> parseRow(Cursor cursor) throws BusinessServiceException {
        //解析数据第1部分
        TransportContract transportContract = parseDataPart1(cursor);
        //解析数据第2部分
        List<TransportContract> transportContractList = parseDataPart2(transportContract, cursor);
        //解析数据第3部分
        parseDatePart3(transportContractList, cursor);

        return transportContractList;
    }

    private TransportContract parseDataPart1(Cursor cursor) throws BusinessServiceException {
        Row row = cursor.getRow();

        TransportContract transportContract = new TransportContract();
        FmsTransportContractHeader fmsTransportContractHeader = transportContract.getFmsTransportContractHeader();
        FmsTransportContractLine fmsTransportContractLine = transportContract.getFmsTransportContractLine();


        for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
            Cell cell = row.getCell(cellIndex);
            if(cell == null){
                continue;
            }
            cursor.setCell(cell, cellIndex);

            cell.setCellType(CellType.STRING);
            String stringCellValue = cell.getStringCellValue();

            switch (cellIndex) {
                case 0://合同类型
                    transportContractImportVerifierService.verifyContractType(stringCellValue, cursor);
                    fmsTransportContractHeader.setContractType(transportContractImportVerifierService.verifyContractType(stringCellValue, cursor));
                    break;

                case 1://合同号
                    transportContractImportVerifierService.verifyContractNumber(stringCellValue, cursor);
                    fmsTransportContractHeader.setContractNumber(stringCellValue);
                    break;

                case 2://被覆盖合同号
                    break;
                case 3://收款单位
                    //transportContractImportVerifierService.verifyReceivingUnit(stringCellValue, cursor);
                    fmsTransportContractHeader.setReceivingUnit(stringCellValue);
                    break;

                case 4://开户行
                    //transportContractImportVerifierService.verifyAccountBank(stringCellValue, cursor);
                    fmsTransportContractHeader.setAccountBank(stringCellValue);
                    break;

                case 5://收款帐户
                    //transportContractImportVerifierService.verifyReceivingAcount(stringCellValue, cursor);
                    fmsTransportContractHeader.setReceivingAcount(stringCellValue);
                    break;
                case 6://合同名称
                    transportContractImportVerifierService.verifyContractName(stringCellValue, cursor);
                    fmsTransportContractHeader.setContractName(stringCellValue);
                    break;
                case 7://物流商
                    TmsLogisticsProviders tmsLogisticsProviders = transportContractImportVerifierService.verifyLogistics(stringCellValue, cursor);
                    fmsTransportContractHeader.setLogisticsId(tmsLogisticsProviders.getLogisticsId());
                    break;
                case 8://所属基地
                    TmsDeparture tmsDeparture = transportContractImportVerifierService.verifyTmsStartPoint(stringCellValue, cursor);
                    fmsTransportContractLine.setStartPointId(tmsDeparture.getDepartureId());
                    break;
                case 9://合同开始时间
                    Date startDate = transportContractImportVerifierService.verifyStartDate(stringCellValue, cursor);
                    fmsTransportContractHeader.setStartDate(startDate);
                    break;
                case 10://合同结束时间
                    Date endDate = transportContractImportVerifierService.verifyEndDate(stringCellValue, cursor, fmsTransportContractHeader.getStartDate());
                    fmsTransportContractHeader.setEndDate(endDate);
                    break;
                case 11://起点省
                    Long startProvinceId = transportContractImportVerifierService.verifyStartProvince(stringCellValue, cursor);
                    fmsTransportContractLine.setStartProvinceId(startProvinceId);
                    break;
                case 12://起点市
                    Long startCityId = transportContractImportVerifierService.verifyStartCity(stringCellValue, cursor, fmsTransportContractLine.getStartProvinceId());
                    fmsTransportContractLine.setStartCityId(startCityId);
                    break;
                case 13://起点区
                    Long startCountyId = transportContractImportVerifierService.verifyStartCounty(stringCellValue, cursor, fmsTransportContractLine.getStartCityId());
                    fmsTransportContractLine.setStartCountyId(startCountyId);
                    break;
                case 14://终点省
                    Long endProvinceId = transportContractImportVerifierService.verifyEndProvince(stringCellValue, cursor);
                    fmsTransportContractLine.setEndProvinceId(endProvinceId);
                    break;
                case 15://终点市
                    Long endCityId = transportContractImportVerifierService.verifyEndCity(stringCellValue, cursor, fmsTransportContractLine.getEndProvinceId());
                    fmsTransportContractLine.setEndCityId(endCityId);
                    break;
                case 16://终点区
                    Long endCountyId = transportContractImportVerifierService.verifyEndCounty(stringCellValue, cursor, fmsTransportContractLine.getEndCityId());
                    fmsTransportContractLine.setEndCountyId(endCountyId);
                    break;
            }

        }
        return transportContract;
    }

    private List<TransportContract> parseDataPart2(TransportContract transportContract, Cursor cursor) throws BusinessServiceException {
        List<HeadPart2> headPart2List = cursor.getHeadPart2List();
        Row row = cursor.getRow();

        List<TransportContract> transportContractList = new ArrayList<>(headPart2List.size());
        FmsTransportContractHeader fmsTransportContractHeader1 = transportContract.getFmsTransportContractHeader();
        FmsTransportContractLine fmsTransportContractLine1 = transportContract.getFmsTransportContractLine();


        for (HeadPart2 headPart2 : headPart2List) {

            //解析运距名称（查找运距头）
            Integer distanceNameCellIndex = headPart2.getDistanceNameCellIndex();
            Cell cell = row.getCell(distanceNameCellIndex);
            if(cell == null){
                continue;
            }
            cursor.setCell(cell, distanceNameCellIndex);
            cell.setCellType(CellType.STRING);
            String stringCellValue = cell.getStringCellValue();
            if(StringHelper.isEmpty(stringCellValue)){
                continue;
            }
            FmsDistanceHeader fmsDistanceHeader = transportContractImportVerifierService.verifyDistanceName(stringCellValue, cursor);
            fmsTransportContractHeader1.setDistanceHeaderId(fmsDistanceHeader.getDistanceHeaderId());
            fmsTransportContractLine1.setTransportMethod(headPart2.getTransMethodId());

            //查找运距
            FmsDistanceLine fmsDistanceLine = transportContractImportVerifierService.verifyDistanceLine(transportContract, cursor);
            fmsTransportContractLine1.setDistanceLineId(fmsDistanceLine.getDistanceLineId());

            //解析条目
            for (HeadPart2.Article article : headPart2.getArticleList()) {
                TransportContract transportContract1 = new TransportContract();
                BeanUtils.copyProperties(transportContract.getFmsTransportContractLine(), transportContract1.getFmsTransportContractLine());//需要深copy
                BeanUtils.copyProperties(transportContract.getFmsTransportContractHeader(), transportContract1.getFmsTransportContractHeader());//需要深copy

                FmsTransportContractLine fmsTransportContractLine2 = transportContract1.getFmsTransportContractLine();

                Integer cellIndex = article.getCellIndex();
                cell = row.getCell(cellIndex);
                if(cell == null){
                    continue;
                }
                cursor.setCell(cell, cellIndex);
                cell.setCellType(CellType.STRING);
                stringCellValue = cell.getStringCellValue();
                BigDecimal unitPrice = transportContractImportVerifierService.verifyUnitPrice(stringCellValue, cursor);
                fmsTransportContractLine2.setUnitPrice(unitPrice);
                fmsTransportContractLine2.setCarCategoryId(article.getCarCategoryId());

                transportContractList.add(transportContract1);
            }
        }

        return transportContractList;
    }

    private void parseDatePart3(List<TransportContract> transportContractList, Cursor cursor) throws BusinessServiceException {
        List<HeadPart3> headPart3List = cursor.getHeadPart3List();
        Row row = cursor.getRow();

        for (HeadPart3 headPart3 : headPart3List) {
            Integer cellIndex = headPart3.getCellIndex();
            Cell cell = row.getCell(cellIndex);
            if(cell == null){
            	headPart3.setPrice(null);
                continue;
            }
            cell.setCellType(CellType.STRING);
            String stringCellValue = cell.getStringCellValue();

            BigDecimal price = transportContractImportVerifierService.verifyOtherPrice(stringCellValue, cursor);
            headPart3.setPrice(price);
        }


        for (TransportContract transportContract : transportContractList) {
            FmsTransportContractLine fmsTransportContractLine = transportContract.getFmsTransportContractLine();
            Long transportMethod = fmsTransportContractLine.getTransportMethod();
            for (HeadPart3 headPart3 : headPart3List) {
                if(headPart3.getPrice() == null){
                    continue;
                }

                if (headPart3.getTransMethodId() == transportMethod && headPart3.getCostType().equals(CostType.over_sea_fee.name())) {
                    fmsTransportContractLine.setOverSeaFee(headPart3.getPrice());
                }

                if (headPart3.getTransMethodId() == transportMethod && headPart3.getCostType().equals(CostType.railway_Prepare_Fee.name())) {
                    fmsTransportContractLine.setRailwayPrepareFee(headPart3.getPrice());
                }
            }
        }


    }
}
