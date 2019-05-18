package com.foreveross.vds.service.fms.service;

import java.util.List;

import com.foreveross.vds.dto.Pagination;
import com.foreveross.vds.dto.fms.FmsCarCategoryRealationImportDto;
import com.foreveross.vds.dto.fms.FmsCarCategoryReleationRequest;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.vo.fms.FmsCarCategoryReleation;
import com.foreveross.vds.vo.inv.InvCarSeries;
import org.apache.poi.ss.usermodel.Workbook;

public interface FmsCarCategoryReleationService {

    Integer insert(FmsCarCategoryReleation fmsCarCategoryReleation);

    Integer insertAll(List<FmsCarCategoryReleation> fmsCarCategoryReleations);

    Integer delete(Long carCategoryReleationId);

    Integer deleteAll(Long[] realationIds);

    Pagination pageRealation(Integer page, Integer rows, FmsCarCategoryReleationRequest fmsCarCategoryReleationRequest);

    Pagination leftQuery(Integer page, Integer rows, String carSeriesCode, Long carCategoryId);

    Pagination rightQuery(Integer page, Integer rows, Long startPointId);

    void addRealation(Long userId, Long carCategoryId, List<InvCarSeries> list, String sessionId);

    List<FmsCarCategoryReleation> selectRelationByWhereClause(FmsCarCategoryReleationRequest fmsCarCategoryReleationRequest);

    void importFmsCarCategoryReleation(Long userId, String sessionId, Workbook workbook) throws BusinessServiceException;

    void translation(List<FmsCarCategoryReleation> fmsCarCategoryOrgRealations);

	void addRealationNew(Long userId, FmsCarCategoryRealationImportDto fmsCarCategoryRealationImportDto,
			String sessionId)throws BusinessServiceException;
}
