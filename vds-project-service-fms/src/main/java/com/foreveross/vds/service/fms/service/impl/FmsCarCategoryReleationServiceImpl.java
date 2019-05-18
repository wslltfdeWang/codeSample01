package com.foreveross.vds.service.fms.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.foreveross.vds.annotation.CustTx;
import com.foreveross.vds.dto.fms.FmsCarCategoryRealationImportDto;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.common.service.ExportTranslatorService;
import com.foreveross.vds.service.fms.mapper.FmsCarCategoryMapper;
import com.foreveross.vds.util.tools.ExcelImportUtil;
import com.foreveross.vds.util.tools.StringHelper;
import com.foreveross.vds.vo.fms.FmsCarCategory;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.foreveross.vds.dto.Pagination;
import com.foreveross.vds.dto.fms.FmsCarCategoryReleationRequest;
import com.foreveross.vds.service.fms.mapper.FmsCarCategoryReleationMapper;
import com.foreveross.vds.service.fms.service.FmsCarCategoryReleationService;
import com.foreveross.vds.vo.fms.FmsCarCategoryReleation;
import com.foreveross.vds.vo.inv.InvCarSeries;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service("fmsCarCategoryOrgReleationService")
public class FmsCarCategoryReleationServiceImpl implements FmsCarCategoryReleationService {

    @Autowired
    private FmsCarCategoryMapper fmsCarCategoryMapper;

    @Autowired
    private FmsCarCategoryReleationMapper fmsCarCategoryReleationMapper;

    @Autowired
    private ExportTranslatorService exportTranslatorService;

    @Override
    public Integer insert(FmsCarCategoryReleation fmsCarCategoryReleation){
        return fmsCarCategoryReleationMapper.insert(fmsCarCategoryReleation);
    }

    @Override
    @CustTx
    public Integer insertAll(List<FmsCarCategoryReleation> fmsCarCategoryReleations){
        int i = 0;
        for(FmsCarCategoryReleation t : fmsCarCategoryReleations){
            i += this.insert(t);
        }
        return i;
    }

    @Override
    public Integer delete(Long carCategoryReleationId){
        return fmsCarCategoryReleationMapper.deleteByPrimaryKey(carCategoryReleationId);
    }

    @Override
    @CustTx
    public Integer deleteAll(Long[] carCategoryReleationIds) {
        int i = 0;
        for(Long carCategoryReleationId : carCategoryReleationIds){
            this.delete(carCategoryReleationId);
        }
        return i;
    }

    @Override
    public Pagination pageRealation(Integer page, Integer rows, FmsCarCategoryReleationRequest fmsCarCategoryReleationRequest) {
        String s = fmsCarCategoryReleationRequest.getOrderBy();
        PageHelper.startPage(page, rows,fmsCarCategoryReleationRequest.getOrderBy());
        Page<FmsCarCategoryReleation> pageList = (Page<FmsCarCategoryReleation>)
        		fmsCarCategoryReleationMapper.selectRelationByWhereClauseNotSeries(fmsCarCategoryReleationRequest);
        
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
    public Pagination leftQuery(Integer page, Integer rows, String carSeriesCode,Long carCategoryId) {
        PageHelper.startPage(page, rows);
        Page<InvCarSeries> pageList = (Page<InvCarSeries>)
                fmsCarCategoryReleationMapper.leftQuery(carSeriesCode, carCategoryId);
        Pagination pagination = new Pagination();
        pagination.setData(pageList.getResult());
        pagination.setRows(pageList.getResult());
        pagination.setTotal(pageList.getTotal());
        return pagination;
    }

    @Override
    public Pagination rightQuery(Integer page, Integer rows, Long carCategoryId) {
        PageHelper.startPage(page, rows);
        Page<InvCarSeries> pageList = (Page<InvCarSeries>)
                fmsCarCategoryReleationMapper.rightQuery(carCategoryId);
        Pagination pagination = new Pagination();
        pagination.setData(pageList.getResult());
        pagination.setRows(pageList.getResult());
        pagination.setTotal(pageList.getTotal());
        return pagination;
    }

    @Override
    @CustTx
    public void addRealation(Long userId, Long carCategoryId, List<InvCarSeries> list, String sessionId) {
        if(StringUtils.isEmpty(carCategoryId)) {
            return;
        }

        Date now = new Date();
        for(int i=0;i<list.size();i++) {
            InvCarSeries invCarSeries = list.get(i);
            if(invCarSeries != null && invCarSeries.getCarCategorReleationId() != null) {
                fmsCarCategoryReleationMapper.deleteByPrimaryKey(invCarSeries.getCarCategorReleationId());
            }else if(invCarSeries != null) {
                FmsCarCategoryReleation fmsCarCategoryReleation = new FmsCarCategoryReleation();
                fmsCarCategoryReleation.setCarCategoryId(carCategoryId);
                fmsCarCategoryReleation.setCarSeriesId(invCarSeries.getCarSeriesId());
                fmsCarCategoryReleation.setCreatedBy(userId);
                fmsCarCategoryReleation.setCreationDate(now);
                fmsCarCategoryReleation.setLastUpdatedBy(userId);
                fmsCarCategoryReleation.setLastUpdateDate(now);
                fmsCarCategoryReleation.setLastUpdateLogin(sessionId);
                fmsCarCategoryReleation.setStartDate(now);
                fmsCarCategoryReleation.setEnabledFlag("Y");
                fmsCarCategoryReleationMapper.insert(fmsCarCategoryReleation);
            }
        }

    }

    @Override
    @CustTx
    public void addRealationNew(Long userId, FmsCarCategoryRealationImportDto fmsCarCategoryRealationImportDto, String sessionId)throws BusinessServiceException {
    	Date now = new Date();
    	FmsCarCategoryReleation fmsCarCategoryReleation = this.valid(fmsCarCategoryRealationImportDto);
        fmsCarCategoryReleation.setCreationDate(now);
        fmsCarCategoryReleation.setCreatedBy(userId);
        fmsCarCategoryReleation.setLastUpdateDate(now);
        fmsCarCategoryReleation.setLastUpdatedBy(userId);
        fmsCarCategoryReleation.setLastUpdateDate(now);
        fmsCarCategoryReleation.setLastUpdateLogin(sessionId);
        fmsCarCategoryReleationMapper.insert(fmsCarCategoryReleation);
    }
    
    @Override
    public List<FmsCarCategoryReleation> selectRelationByWhereClause(FmsCarCategoryReleationRequest fmsCarCategoryReleationRequest){
    	List<FmsCarCategoryReleation> fmsCarCategoryReleation = 
    			fmsCarCategoryReleationMapper.selectRelationByWhereClauseNotSeries(fmsCarCategoryReleationRequest);
    	return fmsCarCategoryReleation;
    }

    @Override
    @CustTx
    public void importFmsCarCategoryReleation(Long userId, String sessionId, Workbook workbook) throws BusinessServiceException {
        List<FmsCarCategoryRealationImportDto> fmsCarCategoryReleationImportDtoList = ExcelImportUtil.importExcel(FmsCarCategoryRealationImportDto.class, workbook);
        Date now = new Date();
        for(FmsCarCategoryRealationImportDto fmsCarCategoryReleationImportDto : fmsCarCategoryReleationImportDtoList){
            FmsCarCategoryReleation fmsCarCategoryReleation = this.valid(fmsCarCategoryReleationImportDto);
            fmsCarCategoryReleation.setCreationDate(now);
            fmsCarCategoryReleation.setCreatedBy(userId);
            fmsCarCategoryReleation.setLastUpdateDate(now);
            fmsCarCategoryReleation.setLastUpdatedBy(userId);
            fmsCarCategoryReleation.setLastUpdateDate(now);
            fmsCarCategoryReleation.setLastUpdateLogin(sessionId);
            fmsCarCategoryReleationMapper.insert(fmsCarCategoryReleation);
        }
    }

    @Override
    public void translation(List<FmsCarCategoryReleation> fmsCarCategoryOrgRealations) {
        for(FmsCarCategoryReleation fmsCarCategoryReleation : fmsCarCategoryOrgRealations){
            //业务实体
            fmsCarCategoryReleation.setOrgIdDesc(exportTranslatorService.transEnum(fmsCarCategoryReleation.getOrgId()));
        }
    }

    private FmsCarCategoryReleation valid(FmsCarCategoryRealationImportDto fmsCarCategoryRealationImportDto) throws BusinessServiceException {
        FmsCarCategoryReleation fmsCarCategoryReleation = new FmsCarCategoryReleation();

        //车型大类代码
        String carCategoryName = fmsCarCategoryRealationImportDto.getCarCategoryName();
        if(StringHelper.isEmpty(carCategoryName)){
            throw new BusinessServiceException("车型大类名称不能为空！");
        }
        FmsCarCategory fmsCarCategoryParameter = new FmsCarCategory();
        fmsCarCategoryParameter.setCarCategoryName(carCategoryName);
        List<FmsCarCategory> list = fmsCarCategoryMapper.selectByWhereNotLike(fmsCarCategoryParameter);
        if(list.size() == 0){
            throw new BusinessServiceException("车型大类名称["+carCategoryName+"]不存在！");
        }else{
            fmsCarCategoryReleation.setCarCategoryId(list.get(0).getCarCategoryId());
        }

        //车系代码
        String vehicleCode = fmsCarCategoryRealationImportDto.getVehicleCode();
        if(StringHelper.isEmpty(vehicleCode)){
            throw new BusinessServiceException("车型代码不能为空！");
        }
        fmsCarCategoryReleation.setVehicleCode(vehicleCode);

        InvCarSeries invCarSeries = new InvCarSeries();
        invCarSeries.setOrgId(list.get(0).getOrgId());
        invCarSeries.setVehicleCode(vehicleCode);
        List<InvCarSeries> invCarSeriesList = fmsCarCategoryReleationMapper.selectCarSeriesByVehicleCode(invCarSeries);
        if(invCarSeriesList.size() == 0){
        	fmsCarCategoryReleation.setCarSeriesId(null);
        }else if(invCarSeriesList.size() != 0){
            fmsCarCategoryReleation.setCarSeriesId(invCarSeriesList.get(0).getCarSeriesId());
        }else {
            throw new BusinessServiceException("车型名称["+vehicleCode+"]对应有多个车系名称！");
        }
        

        //是否存在该关系
        if(isThisReleationExsits(fmsCarCategoryRealationImportDto)){
            throw new BusinessServiceException("["+carCategoryName+"]-["+vehicleCode+"]该关系已存在，不能重复导入！");
        }

        //有效开始时间
        Date startDate = fmsCarCategoryRealationImportDto.getStartDate();
        if(null == startDate){
            throw new BusinessServiceException("开始日期不能为空！");
        }else{
            fmsCarCategoryReleation.setStartDate(startDate);
        }

        //有效结束时间
        Date endDate = fmsCarCategoryRealationImportDto.getEndDate();
        if(endDate != null && endDate.compareTo(startDate) < 0){
            throw new BusinessServiceException("存在结束时间早于开始时间！");
        }else{
            fmsCarCategoryReleation.setEndDate(endDate);
        }

        //有效标识
        String enabledFlag = fmsCarCategoryRealationImportDto.getEnabledFlag();
        if(StringHelper.isEmpty(enabledFlag)){
            throw new BusinessServiceException("有效标识不能为空！");
        }
        if(!enabledFlag.equals("Y") && !enabledFlag.equals("N")){
            throw new BusinessServiceException("有效标识只能是Y或N！");
        }else{
            fmsCarCategoryReleation.setEnabledFlag(enabledFlag);
        }

        return fmsCarCategoryReleation;
    }

    private boolean isThisReleationExsits(FmsCarCategoryRealationImportDto fmsCarCategoryRealationImportDto) {
        FmsCarCategoryReleationRequest param = new FmsCarCategoryReleationRequest();
        param.setCarCategoryName(fmsCarCategoryRealationImportDto.getCarCategoryName());
        param.setVehicleCode(fmsCarCategoryRealationImportDto.getVehicleCode());
        List<FmsCarCategoryReleation> fmsCarCategoryReleationList = fmsCarCategoryReleationMapper.selectRelationByWhereClauseNotLike(param);
        return fmsCarCategoryReleationList.size() == 0 ? false : true;
    }

}
