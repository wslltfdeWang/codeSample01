package com.foreveross.vds.service.fms.service;

import java.util.List;

import com.foreveross.vds.dto.Pagination;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.vo.fms.FmsCarCategory;
import org.apache.poi.ss.usermodel.Workbook;

public interface FmsCarCategoryService {

    /**
     * 分页查询
     * @param whereCause
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Pagination page(FmsCarCategory whereCause, int pageIndex, int pageSize);

    /**
     *
     * @param whereCaus
     * @return
     */
    List<FmsCarCategory> query(FmsCarCategory whereCaus);

    /**
     * 查询所有
     * @param whereCause
     * @return
     */
    List<FmsCarCategory> listByWhereCause(FmsCarCategory whereCause);

    /**
     * 根据主键查询
     * @param carCategoryId
     * @return
     */
    FmsCarCategory get(Long carCategoryId);

    /**
     * 更新
     * @param fmsCarCategory
     * @return
     */
    int insert(FmsCarCategory fmsCarCategory);

    /**
     * 更新
     * @param fmsCarCategory
     * @return
     */
    int update(FmsCarCategory fmsCarCategory);

    /**
     * 根据主键删除
     * @param carCategoryId
     * @return
     */
    int delete(Long carCategoryId);


    void checkParams(FmsCarCategory fmsCarCategory) throws BusinessServiceException;

    void importFmsCarCategory(Long userId, String sessionId, Workbook workbook) throws BusinessServiceException;

    void translate(List<FmsCarCategory> list);
}
