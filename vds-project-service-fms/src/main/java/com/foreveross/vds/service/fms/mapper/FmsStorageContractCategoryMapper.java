package com.foreveross.vds.service.fms.mapper;


import org.apache.ibatis.annotations.Mapper;

import com.foreveross.vds.service.common.mapper.BaseMapper;
import com.foreveross.vds.vo.fms.FmsStorageContractCategory;

@Mapper
public interface FmsStorageContractCategoryMapper extends BaseMapper<FmsStorageContractCategory, Long>{

    int selectExists(FmsStorageContractCategory fmsStorageContractCategory);
}