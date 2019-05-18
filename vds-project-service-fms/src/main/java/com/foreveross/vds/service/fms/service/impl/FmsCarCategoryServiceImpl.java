package com.foreveross.vds.service.fms.service.impl;

import java.util.Date;
import java.util.List;

import com.foreveross.vds.annotation.CustTx;
import com.foreveross.vds.dto.fms.FmsCarCategoryImportDto;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.common.service.ExportTranslatorService;
import com.foreveross.vds.service.common.service.LookupService;
import com.foreveross.vds.util.tools.ExcelImportUtil;
import com.foreveross.vds.util.tools.StringHelper;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.dto.Pagination;
import com.foreveross.vds.service.fms.mapper.FmsCarCategoryMapper;
import com.foreveross.vds.service.fms.service.FmsCarCategoryService;
import com.foreveross.vds.vo.fms.FmsCarCategory;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class FmsCarCategoryServiceImpl implements FmsCarCategoryService {

    @Autowired
    private FmsCarCategoryMapper fmsCarCategoryMapper;

    @Autowired
    private ExportTranslatorService exportTranslatorService;

    @Autowired
    private LookupService lookupService;

    @Override
    public Pagination page(FmsCarCategory whereCause, int pageIndex, int pageSize) {
        PageHelper.startPage(pageIndex, pageSize,whereCause.getOrderBy());
        Page<FmsCarCategory> page = (Page<FmsCarCategory>) this.listByWhereCause(whereCause);

        Pagination pagination = new Pagination();
        pagination.setData(page.getResult());
        pagination.setRows(page.getResult());
        pagination.setTotal(page.getTotal());
        return pagination;
    }

    @Override
    public List<FmsCarCategory> query(FmsCarCategory whereCaus) {
        return this.listByWhereCause(whereCaus);
    }

    @Override
    public List<FmsCarCategory> listByWhereCause(FmsCarCategory whereCause) {
        return fmsCarCategoryMapper.selectByWhere(whereCause);
    }

    @Override
    public FmsCarCategory get(Long carCategoryId) {
        return fmsCarCategoryMapper.selectByPrimaryKey(carCategoryId);
    }

    @Override
    public int insert(FmsCarCategory fmsCarCategory) {
        return fmsCarCategoryMapper.insert(fmsCarCategory);
    }

    @Override
    public int update(FmsCarCategory fmsCarCategory) {
        return fmsCarCategoryMapper.updateByPrimaryKeySelective(fmsCarCategory);
    }

    @Override
    public int delete(Long carCategoryId) {
        return fmsCarCategoryMapper.deleteByPrimaryKey(carCategoryId);
    }

    @Override
    public void checkParams(FmsCarCategory fmsCarCategory) throws BusinessServiceException {
        int i = fmsCarCategoryMapper.countExistsCode(fmsCarCategory.getCarCategoryCode(), fmsCarCategory.getCarCategoryId());
        if(i != 0){
            throw new BusinessServiceException("车型代码已存在！");
        }
    }

    @Override
    @CustTx
    public void importFmsCarCategory(Long userId, String sessionId, Workbook workbook) throws BusinessServiceException {
        List<FmsCarCategoryImportDto> fmsCarCategoryImportDtos = ExcelImportUtil.importExcel(FmsCarCategoryImportDto.class, workbook);
        Date now = new Date();
        for(FmsCarCategoryImportDto fmsCarCategoryImportDto : fmsCarCategoryImportDtos){
            FmsCarCategory fmsCarCategory = this.valid(fmsCarCategoryImportDto);
            fmsCarCategory.setCreatedBy(userId);
            fmsCarCategory.setCreationDate(now);
            fmsCarCategory.setLastUpdateLogin(sessionId);
            fmsCarCategory.setLastUpdateDate(now);
            fmsCarCategory.setLastUpdatedBy(userId);
            fmsCarCategoryMapper.insert(fmsCarCategory);
        }
    }

    @Override
    public void translate(List<FmsCarCategory> list) {
        for(FmsCarCategory fmsCarCategory : list){
            //业务实体
            fmsCarCategory.setOrgIdDesc(exportTranslatorService.transEnum("BUSSINESS_UNIT", String.valueOf(fmsCarCategory.getOrgId())));
            //开始时间
            fmsCarCategory.setStartDateDesc(exportTranslatorService.transDate(fmsCarCategory.getStartDate()));
            //结束时间
            fmsCarCategory.setEndDateDesc(exportTranslatorService.transDate(fmsCarCategory.getEndDate()));
            //有效标识
            fmsCarCategory.setEnabledFlagMeaning(exportTranslatorService.transEnableFlag(fmsCarCategory.getEnabledFlag()));
        }
    }

    private FmsCarCategory valid(FmsCarCategoryImportDto fmsCarCategoryImportDto) throws BusinessServiceException {
        FmsCarCategory fmsCarCategory = new FmsCarCategory();
        //车型大类代码
        String carCategoryCode = fmsCarCategoryImportDto.getCarCategoryCode();
        if(StringHelper.isEmpty(carCategoryCode)){
            throw new BusinessServiceException("车型大类代码不能为空！");
        }
        if(fmsCarCategoryMapper.countExistsCode(carCategoryCode, null) != 0){
            throw new BusinessServiceException("存在重复的车型大类代码["+ fmsCarCategoryImportDto.getCarCategoryCode()+"]!");
        }else{
            fmsCarCategory.setCarCategoryCode(carCategoryCode);
        }


        //车型大类名称
        String carCategoryName = fmsCarCategoryImportDto.getCarCategoryName();
        if(StringHelper.isEmpty(carCategoryName)){
            throw new BusinessServiceException("车型大类名称不能为空！");
        }else{
            fmsCarCategory.setCarCategoryName(carCategoryName);
        }

        //业务实体
        String bussinessUnit = fmsCarCategoryImportDto.getOrgName();
        if(StringHelper.isEmpty(bussinessUnit)){
            throw new BusinessServiceException("业务实体不能为空！");
        }
        String orgLookUpCode = validBussinessUnit(bussinessUnit);
        if(orgLookUpCode == null){
            throw new BusinessServiceException("业务实体["+ bussinessUnit+"]不正确！");
        }else{
            fmsCarCategory.setOrgId(Long.valueOf(orgLookUpCode));
        }

        //开始时间
        Date startDate = fmsCarCategoryImportDto.getStartDate();
        if(startDate == null){
            throw new BusinessServiceException("开始时间不能为空！");
        }else{
            fmsCarCategory.setStartDate(startDate);
        }

        //结束时间
        Date endDate = fmsCarCategoryImportDto.getEndDate();
        if(endDate != null && endDate.compareTo(startDate) < 0){
            throw new BusinessServiceException("存在结束时间早于开始时间！");
        }else{
            fmsCarCategory.setEndDate(endDate);
        }

        //有效标识
        String enabledFlag = fmsCarCategoryImportDto.getEnabledFlag();
        if(StringHelper.isEmpty(enabledFlag)){
            throw new BusinessServiceException("有效标识不能为空！");
        }
        if(!enabledFlag.equals("Y") && !enabledFlag.equals("N")){
            throw new BusinessServiceException("有效标识只能是Y或N！");
        }else{
            fmsCarCategory.setEnabledFlag(enabledFlag);
        }
        return fmsCarCategory;
    }

    private String validBussinessUnit(String bussinessUnit) {
        return lookupService.getLookUpCode("BUSSINESS_UNIT", bussinessUnit);
    }
}
