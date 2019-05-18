package com.foreveross.vds.service.fms.service.transportContractImport;

import com.foreveross.vds.dto.fms.TransportContract;
import com.foreveross.vds.dto.fms.transportContractImport.CostType;
import com.foreveross.vds.dto.fms.transportContractImport.Cursor;
import com.foreveross.vds.dto.fms.transportContractImport.TransportMethod;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.vo.fms.FmsCarCategory;
import com.foreveross.vds.vo.fms.FmsDistanceHeader;
import com.foreveross.vds.vo.fms.FmsDistanceLine;
import com.foreveross.vds.vo.tms.TmsDeparture;
import com.foreveross.vds.vo.tms.TmsLogisticsProviders;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 运输合同导入验证
 */
public interface TransportContractImportVerifierService {

    /**
     *验证发运方式
     * @param stringCellValue
     * @return
     */
    TransportMethod verifyTransMethodNoException(String stringCellValue);

    /**
     * 验证车型大类
     * @param stringCellValue
     * @param cursor
     * @return
     */
    FmsCarCategory verifyCarCategory(String stringCellValue, Cursor cursor) throws BusinessServiceException;

    /**
     * 验证费用类型
     * @param stringCellValue
     * @return
     */
    CostType verifyCostType(String stringCellValue);

    /**
     * 验证发运方式
     * @param stringCellValue
     * @param cursor
     * @return
     */
    TransportMethod verifyTransMethod(String stringCellValue, Cursor cursor);

    Long verifyContractType(String stringCellValue, Cursor cursor) throws BusinessServiceException;

    /**
     * 验证合同号
     * @param stringCellValue
     * @param cursor
     */
    void verifyContractNumber(String stringCellValue, Cursor cursor) throws BusinessServiceException;

    /**
     * 验证合同名称
     * @param stringCellValue
     * @param cursor
     */
    void verifyContractName(String stringCellValue, Cursor cursor) throws BusinessServiceException;

    /**
     * 验证物流商
     * @param stringCellValue
     * @param cursor
     */
    TmsLogisticsProviders verifyLogistics(String stringCellValue, Cursor cursor) throws BusinessServiceException;

    /**
     * 验证基地
     * @param stringCellValue
     * @param cursor
     * @return
     */
    TmsDeparture verifyTmsStartPoint(String stringCellValue, Cursor cursor) throws BusinessServiceException;

    /**
     * 验证合同开始时间
     * @param stringCellValue
     * @param cursor
     * @return
     */
    Date verifyStartDate(String stringCellValue, Cursor cursor) throws BusinessServiceException;

    /**
     * 验证合同结束时间
     * @param stringCellValue
     * @param cursor
     * @param startDate
     * @return
     */
    Date verifyEndDate(String stringCellValue, Cursor cursor, Date startDate) throws BusinessServiceException;

    /**
     * 验证起点省
     * @param stringCellValue
     * @param cursor
     * @return
     */
    Long verifyStartProvince(String stringCellValue, Cursor cursor) throws BusinessServiceException;

    /**
     * 验证起点市
     * @param stringCellValue
     * @param cursor
     * @param startProvinceId
     * @return
     */
    Long verifyStartCity(String stringCellValue, Cursor cursor, Long startProvinceId) throws BusinessServiceException;

    /**
     * 验证起点区
     * @param stringCellValue
     * @param cursor
     * @param startCityId
     * @return
     */
    Long verifyStartCounty(String stringCellValue, Cursor cursor, Long startCityId) throws BusinessServiceException;

    /**
     * 验证终点省
     * @param stringCellValue
     * @param cursor
     * @return
     * @throws BusinessServiceException
     */
    Long verifyEndProvince(String stringCellValue, Cursor cursor) throws BusinessServiceException;

    /**
     * 验证终点市
     * @param stringCellValue
     * @param cursor
     * @param endProvinceId
     * @return
     * @throws BusinessServiceException
     */
    Long verifyEndCity(String stringCellValue, Cursor cursor, Long endProvinceId) throws BusinessServiceException;

    /**
     * 验证终点区
     * @param stringCellValue
     * @param cursor
     * @param endCityId
     * @return
     * @throws BusinessServiceException
     */
    Long verifyEndCounty(String stringCellValue, Cursor cursor, Long endCityId) throws BusinessServiceException;

    /**
     * 验证运距名称
     * @param stringCellValue
     * @param cursor
     * @return
     */
    FmsDistanceHeader verifyDistanceName(String stringCellValue, Cursor cursor) throws BusinessServiceException;

    /**
     * 验证运距
     * @param stringCellValue
     * @param cursor
     * @return
     */
    Integer verifyDistance(String stringCellValue, Cursor cursor) throws BusinessServiceException;

    /**
     * 验证金额
     * @param stringCellValue
     * @param cursor
     * @return
     */
    BigDecimal verifyUnitPrice(String stringCellValue, Cursor cursor) throws BusinessServiceException;

    /**
     * 验证运距行
     * @param contract
     * @param cursor
     * @return
     */
    FmsDistanceLine verifyDistanceLine(TransportContract contract, Cursor cursor) throws BusinessServiceException;

    /**
     * 验证其他费用
     * @param stringCellValue
     * @param cursor
     * @return
     */
    BigDecimal verifyOtherPrice(String stringCellValue, Cursor cursor) throws BusinessServiceException;

    /**
     * 收款单位
     * @param stringCellValue
     * @param cursor
     */
    void verifyReceivingUnit(String stringCellValue, Cursor cursor) throws BusinessServiceException;

    /**
     * 开户行
     * @param stringCellValue
     * @param cursor
     */
    void verifyAccountBank(String stringCellValue, Cursor cursor) throws BusinessServiceException;

    /**
     * 收款帐户
     * @param stringCellValue
     * @param cursor
     */
    void verifyReceivingAcount(String stringCellValue, Cursor cursor) throws BusinessServiceException;
}
