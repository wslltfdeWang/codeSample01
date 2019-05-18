package com.foreveross.vds.service.fms.service;

import java.util.List;

import com.foreveross.vds.dto.Pagination;
import com.foreveross.vds.dto.fms.FmsProviderRequest;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.vo.fms.FmsProvider;

public interface FmsProviderService {
	
	Pagination pageRealation(Integer page, Integer rows, FmsProviderRequest fmsProviderRequest);

	void addRealationNew(Long userId, FmsProvider fmsProvider,
			String sessionId)throws BusinessServiceException;

	Integer delete(Long providerId);

	void updateRealationNew(Long userId, FmsProvider fmsProvider, String sessionId) throws BusinessServiceException;

	List<FmsProvider> getAllProviders(Long providerId);

}
