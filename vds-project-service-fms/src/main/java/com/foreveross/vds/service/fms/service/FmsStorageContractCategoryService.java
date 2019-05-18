package com.foreveross.vds.service.fms.service;

import java.util.List;

import com.foreveross.vds.dto.Pagination;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.common.service.BaseService;
import com.foreveross.vds.vo.fms.FmsStorageContractCategory;

public interface FmsStorageContractCategoryService extends BaseService<FmsStorageContractCategory, Long> {

    Pagination page(FmsStorageContractCategory whereCause, Integer pageIndex, Integer pageSize);

    List<FmsStorageContractCategory> listByWhereCause(FmsStorageContractCategory whereCause);

    void checkParams(FmsStorageContractCategory fmsStorageContractCategory) throws BusinessServiceException;

}
