package com.foreveross.vds.service.fms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.foreveross.vds.dto.fms.FmsProviderRequest;
import com.foreveross.vds.vo.fms.FmsProvider;
import com.github.pagehelper.Page;

@Mapper
public interface FmsProviderMapper {
	
	int deleteByPrimaryKey(Long carCategoryReleationId);
	
    int insert(FmsProvider fmsProvider);

    Page<FmsProvider> selectRelationByWhereClauseNotSeries(FmsProviderRequest fmsProviderRequest);

	Page<FmsProvider> selectRelationByWhereClause(FmsProviderRequest fmsProviderRequest);

	void updateByPrimaryKey(FmsProvider fmsProvider);

	List<FmsProvider> getAllProviders(Long providerId);

	List<FmsProvider> selectRelationByWhereClauseNotLike(FmsProviderRequest fmsProviderRequest);
}
