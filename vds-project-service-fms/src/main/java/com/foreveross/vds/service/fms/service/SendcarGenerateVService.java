package com.foreveross.vds.service.fms.service;

import com.foreveross.vds.dto.fms.SendcarExportDto;
import com.foreveross.vds.dto.fms.SendcarGenerateRequest;
import com.foreveross.vds.dto.fms.SendcarGenerateSubmit;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.common.service.BaseService;
import com.foreveross.vds.vo.fms.SendcarGenerateV;
import com.foreveross.vds.vo.inv.InvDriverInfo;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * 生成送车交接单
 */
public interface SendcarGenerateVService extends BaseService<SendcarGenerateV, Long> {

    /**
     * 获取给定商品车某一段次同一装车单下的所有商品车数据
     * @param vinId
     * @param organizationIds
     * @return
     */
    List<SendcarGenerateV> getSameLoadListDetail(Long vinId, List<Long> organizationIds) throws BusinessServiceException;

    /**
     * 生成送车交接单
     * @param sendcarGenerateSubmit
     * @param userId
     * @param sessionId
     * @return
     * @throws BusinessServiceException
     */
    List<Long> doGenerateSendcar(SendcarGenerateSubmit sendcarGenerateSubmit, Long userId, String sessionId) throws BusinessServiceException;

    /**
     * 调用接口
     * @param sendcarHeaderIdList
     */
    void sendInter(List<Long> sendcarHeaderIdList);

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
     * 翻译
     * @param list
     */
    void translate(List<SendcarExportDto> list);

    /**
     *
     * @param workbook
     * @throws BusinessServiceException
     */
    void checkWorkBook(Workbook workbook) throws BusinessServiceException;

    /**
     *
     * @param userId
     * @param sessionId
     * @param organizationIdList
     * @param workbook
     * @return
     * @throws BusinessServiceException
     */
    List<Long> importPreparecarPlan(Long userId, String sessionId, List<Long> organizationIdList, Workbook workbook) throws BusinessServiceException;

    /**
     *
     * @param sendcarGenerateSubmit
     * @throws BusinessServiceException
     */
    void checkParam(SendcarGenerateSubmit sendcarGenerateSubmit) throws BusinessServiceException;

    void relateData(List<Long> sendcarHeaderIdList, Long userId, String sessionId);
}
