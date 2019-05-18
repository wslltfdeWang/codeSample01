package com.foreveross.vds.service.fms.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.dto.Pagination;
import com.foreveross.vds.dto.fms.FmsProviderRequest;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.fms.mapper.FmsProviderMapper;
import com.foreveross.vds.service.fms.service.FmsProviderService;
import com.foreveross.vds.vo.fms.FmsProvider;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class FmsProviderServiceImpl implements FmsProviderService {

	@Autowired
    private FmsProviderMapper fmsProviderMapper;
	
	@Override
	public Pagination pageRealation(Integer page, Integer rows, FmsProviderRequest fmsProviderRequest) {
		String s = fmsProviderRequest.getOrderBy();
        PageHelper.startPage(page, rows,fmsProviderRequest.getOrderBy());
        Page<FmsProvider> pageList = (Page<FmsProvider>)
        		fmsProviderMapper.selectRelationByWhereClause(fmsProviderRequest);
        
        Integer totalCount = Integer.valueOf(String.valueOf(pageList.getTotal()));
        com.foreveross.vds.dto.Page p = new com.foreveross.vds.dto.Page();
        p.setTotalRecords(totalCount);
        p.setPageNo(page);
        p.setPageSize(rows);
        p.setTotalPage(totalCount % pageList.getPageSize() ==0? totalCount/pageList.getPageSize():totalCount/pageList.getPageSize() +1);

        Pagination pagination = new Pagination();
        pagination.setData(pageList.getResult());
        pagination.setRows(pageList.getResult());
        pagination.setTotal(pageList.getTotal());
        pagination.setPage(p);
        return pagination;
	}

	@Override
	public void addRealationNew(Long userId, FmsProvider fmsProvider, String sessionId)
			throws BusinessServiceException {
		FmsProviderRequest fmsProviderRequest = new FmsProviderRequest(); 
		fmsProviderRequest.setProviderName(fmsProvider.getProviderName());
		List<FmsProvider> fmsProviderList = fmsProviderMapper.selectRelationByWhereClauseNotLike(fmsProviderRequest);
		Date now = new Date();
		if(fmsProviderList.size() != 0) {
			throw new BusinessServiceException("供应商已存在");
		}
		fmsProvider.setCreationDate(now);
		fmsProvider.setCreatedBy(userId);
		fmsProvider.setLastUpdateDate(now);
		fmsProvider.setLastUpdatedBy(userId);
		fmsProvider.setLastUpdateDate(now);
		fmsProvider.setLastUpdateLogin(sessionId);
        fmsProviderMapper.insert(fmsProvider);
		
	}
	
	@Override
	public void updateRealationNew(Long userId, FmsProvider fmsProvider, String sessionId)
			throws BusinessServiceException {
		Date now = new Date();
		fmsProvider.setLastUpdateDate(now);
		fmsProvider.setLastUpdatedBy(userId);
		fmsProvider.setLastUpdateDate(now);
		fmsProvider.setLastUpdateLogin(sessionId);
        fmsProviderMapper.updateByPrimaryKey(fmsProvider);
	}

	@Override
	public Integer delete(Long providerId) {
		return fmsProviderMapper.deleteByPrimaryKey(providerId);
	}

	
    @Override
    public List<FmsProvider> getAllProviders(Long providerId) {
        return fmsProviderMapper.getAllProviders(providerId);
    }
}
