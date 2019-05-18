package com.foreveross.vds.service.fms.mapper;

import com.foreveross.vds.dto.fms.SendcarExportDto;
import com.foreveross.vds.dto.fms.SendcarGenerateRequest;
import com.foreveross.vds.service.common.mapper.BaseMapper;
import com.foreveross.vds.vo.fms.SendcarGenerateV;
import com.foreveross.vds.vo.inv.InvDriverInfo;
import com.foreveross.vds.vo.invload.InvPreparecarPlan;
import com.foreveross.vds.vo.tms.TmsTransTool;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SendcarGenerateVMapper extends BaseMapper<SendcarGenerateV, Long>{

    /**
     * 查询给定车辆vin相同装车单下的所有商品车
     * @param vinId 车辆vinId
     * @param organizationIds 当前库存组织
     * @return 返回的结果将作为queryList的参数查询需要生成的送车交接单数据
     */
    List<SendcarGenerateRequest> selectSameLoadListDetail(@Param("vinId") Long vinId, @Param("organizationIds") List<Long> organizationIds);

    /**
     * 查询当前商品车集合在给定段次是否属于同一个送车交接单
     *
     * @param sectionOrder
     * @param vinIdList
     * @return 返回==1：属于同一个送车交接单，返回！=1，不在同一个送车交接单
     */
    int countSendcarHeaderId(@Param("sectionOrder")Long sectionOrder, @Param("vinIdList")List<Long> vinIdList);

    /**
     *
     * @param sectionOrder
     * @param vinCode
     * @return
     */
    Long getSendcarHeaderIdBy(@Param("sectionOrder")Long sectionOrder, @Param("vinCode")String vinCode);

    /**
     * 查询运力工具
     * @param transToolId
     * @return
     */
    TmsTransTool getTransTool(Long transToolId);

    /**
     * 根据运输工具ID查询createData最晚的一条送车交接单头数据，如果存在，取那条数据的司机信息
     * @param transToolId
     * @return
     */
    InvDriverInfo getDriverInfo(Long transToolId);

    /**
     *
     * @param sendcarGenerateRequest
     * @return
     */
    List<SendcarExportDto> queryExport(SendcarGenerateRequest sendcarGenerateRequest);

    /**
     *
     * @param vinCode
     * @param organizationIdList
     * @return
     */
    int countLoadScan(@Param("vinCode") String vinCode, @Param("organizationIdList")List<Long> organizationIdList);

    InvPreparecarPlan queryPreparecarPlanBy(@Param("vinCode")String vinCode, @Param("organizationIdList")List<Long> organizationIdList);

    Long getStorageLocationType(Long storageLocationId);

}