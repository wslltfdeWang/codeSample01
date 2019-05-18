package com.foreveross.vds.service.fms.service.transportContractImport;

import com.foreveross.vds.service.common.exception.BusinessServiceException;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 运输合同导入服务
 */
public interface TransportContractImportService {

    /**
     * 导入运输合同
     * @param userId
     * @param sessionId
     * @param workbook
     */
    void importTransportContract(Workbook workbook, Long userId, String sessionId) throws BusinessServiceException;
}
