package com.foreveross.vds.service.fms.service.impl;

import java.util.List;

import com.foreveross.vds.service.common.exception.BusinessServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.dto.Pagination;
import com.foreveross.vds.service.common.service.impl.BaseServiceImpl;
import com.foreveross.vds.service.fms.mapper.FmsStorageContractCategoryMapper;
import com.foreveross.vds.service.fms.service.FmsStorageContractCategoryService;
import com.foreveross.vds.vo.fms.FmsStorageContractCategory;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class FmsStorageContractCategoryServiceImpl extends BaseServiceImpl<FmsStorageContractCategory, Long> implements FmsStorageContractCategoryService {

    @Autowired
    private FmsStorageContractCategoryMapper fmsStorageContractCategoryMapper;

    @Override
    public Pagination page(FmsStorageContractCategory whereCause, Integer pageIndex, Integer pageSize) {
        PageHelper.startPage(pageIndex, pageSize,whereCause.getOrderBy());
        Page<FmsStorageContractCategory> page = (Page<FmsStorageContractCategory>) this.listByWhereCause(whereCause);

        Pagination pagination = new Pagination();
        pagination.setData(page.getResult());
        pagination.setRows(page.getResult());
        pagination.setTotal(page.getTotal());
        return pagination;
    }

    @Override
    public List<FmsStorageContractCategory> listByWhereCause(FmsStorageContractCategory whereCause) {
        return fmsStorageContractCategoryMapper.queryList(whereCause);
    }

    @Override
    public void checkParams(FmsStorageContractCategory fmsStorageContractCategory) throws BusinessServiceException {
        int i = fmsStorageContractCategoryMapper.selectExists(fmsStorageContractCategory);
        if(i != 0){
            throw new BusinessServiceException("类别名称已存在！");
        }


    }

}
