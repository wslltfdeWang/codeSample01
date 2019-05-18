package com.foreveross.vds.service.fms.mapper;

import com.foreveross.vds.dto.fms.transportContractImport.TransportMethod;
import com.foreveross.vds.vo.tms.TmsDeparture;
import com.foreveross.vds.vo.tms.TmsLogisticsProviders;
import com.foreveross.vds.vo.tms.TmsRegionInfo;
import com.foreveross.vds.vo.tms.TmsStartPoint;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 运输合同导入验证
 */
@Repository
public interface TransportContractImportVerifierMapper {

    /**
     * 判断合同号是否重复
     * @param contractNumber
     * @return
     */
    int contractNumberRepeat(String contractNumber);

    /**
     * 判断合同名称是否重复
     * @param contractName
     * @return
     */
    int contractNameRepeat(String contractName);

    /**
     * 根据发运方式名称查找发运方式
     * @param transMethodName
     * @return
     */
    TransportMethod getTransportMethod(String transMethodName);

    /**
     * 根据物流商名称查询物流商
     * @param logisticsName
     * @return
     */
    List<TmsLogisticsProviders> getLogistics(String logisticsName);

    /**
     * 根据基地名称获取所属基地
     * @param startPointName
     * @return
     */
    List<TmsDeparture> getTmsStartPoint(String startPointName);

    /**
     * 查询地区
     * @param param
     * @return
     */
    List<TmsRegionInfo> getReginInfo(TmsRegionInfo param);
}
