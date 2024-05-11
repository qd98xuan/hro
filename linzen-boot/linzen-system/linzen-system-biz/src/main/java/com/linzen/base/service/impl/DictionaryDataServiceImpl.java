package com.linzen.base.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.entity.DictionaryTypeEntity;
import com.linzen.base.mapper.DictionaryDataMapper;
import com.linzen.base.model.dictionarydata.DictionaryDataExportModel;
import com.linzen.base.model.dictionarytype.DictionaryExportModel;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.service.DictionaryTypeService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.base.vo.DownloadVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.exception.DataBaseException;
import com.linzen.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 字典数据
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class DictionaryDataServiceImpl extends SuperServiceImpl<DictionaryDataMapper, DictionaryDataEntity> implements DictionaryDataService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;
    @Autowired
    private FileExport fileExport;
    @Autowired
    private ConfigValueUtil configValueUtil;

    @Override
    public List<DictionaryDataEntity> getList(String dictionaryTypeId, Boolean enable) {
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DictionaryDataEntity::getDictionaryTypeId, dictionaryTypeId);
        if (enable) {
            queryWrapper.lambda().eq(DictionaryDataEntity::getEnabledMark, 1);
        }
        queryWrapper.lambda().orderByAsc(DictionaryDataEntity::getSortCode).orderByDesc(DictionaryDataEntity::getCreatorTime).orderByDesc(DictionaryDataEntity::getUpdateTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<DictionaryDataEntity> getList(String dictionaryTypeId) {
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DictionaryDataEntity::getDictionaryTypeId, dictionaryTypeId);
        queryWrapper.lambda().orderByAsc(DictionaryDataEntity::getSortCode)
                .orderByDesc(DictionaryDataEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<DictionaryDataEntity> getDicList(String dictionaryTypeId) {
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().and(
                t -> t.eq(DictionaryDataEntity::getDictionaryTypeId, dictionaryTypeId)
                        .or().eq(DictionaryDataEntity::getEnCode, dictionaryTypeId)
        );
        queryWrapper.lambda().select(DictionaryDataEntity::getId, DictionaryDataEntity::getFullName, DictionaryDataEntity::getEnCode);
        return this.list(queryWrapper);
    }

    @Override
    public List<DictionaryDataEntity> geDicList(String dictionaryTypeId) {
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().and(
                t -> t.eq(DictionaryDataEntity::getDictionaryTypeId, dictionaryTypeId)
                        .or().eq(DictionaryDataEntity::getEnCode, dictionaryTypeId)
        );
        queryWrapper.lambda().select(DictionaryDataEntity::getId, DictionaryDataEntity::getFullName);
        return this.list(queryWrapper);
    }

    @Override
    public Boolean isExistSubset(String parentId) {
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DictionaryDataEntity::getParentId, parentId);
        return this.list(queryWrapper).size() > 0;
    }

    @Override
    public DictionaryDataEntity getInfo(String id) {
        if (id == null) {
            return null;
        }
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DictionaryDataEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public DictionaryDataEntity getSwapInfo(String value, String dictionaryTypeId) {
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DictionaryDataEntity::getDictionaryTypeId, dictionaryTypeId).and(
                t -> t.eq(DictionaryDataEntity::getId, value)
                        .or().eq(DictionaryDataEntity::getEnCode, value)
        );
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String dictionaryTypeId, String fullName, String id) {
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DictionaryDataEntity::getFullName, fullName).eq(DictionaryDataEntity::getDictionaryTypeId, dictionaryTypeId);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(DictionaryDataEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String dictionaryTypeId, String enCode, String id) {
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DictionaryDataEntity::getEnCode, enCode).eq(DictionaryDataEntity::getDictionaryTypeId, dictionaryTypeId);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(DictionaryDataEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public void delete(DictionaryDataEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public void create(DictionaryDataEntity entity) {
        //判断id是否为空,为空则为新建
        if (StringUtil.isEmpty(entity.getId())) {
            entity.setId(RandomUtil.uuId());
            entity.setSimpleSpelling(PinYinUtil.getFirstSpell(entity.getFullName()).toUpperCase());
            entity.setCreatorUserId(userProvider.get().getUserId());
        }
        this.save(entity);
    }

    @Override
    public boolean update(String id, DictionaryDataEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(DateUtil.getNowDate());
        entity.setUpdateUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        DictionaryDataEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DictionaryDataEntity::getDictionaryTypeId, upEntity.getDictionaryTypeId())
                .eq(DictionaryDataEntity::getParentId, upEntity.getParentId())
                .lt(DictionaryDataEntity::getSortCode, upSortCode)
                .orderByDesc(DictionaryDataEntity::getSortCode);
        List<DictionaryDataEntity> downEntity = this.list(queryWrapper);
        if (downEntity.size() > 0) {
            //交换两条记录的sort值
            Long temp = upEntity.getSortCode();
            upEntity.setSortCode(downEntity.get(0).getSortCode());
            downEntity.get(0).setSortCode(temp);
            updateById(downEntity.get(0));
            updateById(upEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    public boolean next(String id) {
        boolean isOk = false;
        //获取要下移的那条数据的信息
        DictionaryDataEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DictionaryDataEntity::getDictionaryTypeId, downEntity.getDictionaryTypeId())
                .eq(DictionaryDataEntity::getParentId, downEntity.getParentId())
                .gt(DictionaryDataEntity::getSortCode, upSortCode)
                .orderByAsc(DictionaryDataEntity::getSortCode);
        List<DictionaryDataEntity> upEntity = this.list(queryWrapper);
        if (!upEntity.isEmpty()) {
            //交换两条记录的sort值
            Long temp = downEntity.getSortCode();
            downEntity.setSortCode(upEntity.get(0).getSortCode());
            upEntity.get(0).setSortCode(temp);
            updateById(upEntity.get(0));
            updateById(downEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    public List<DictionaryDataEntity> getDictionName(List<String> id) {
        List<DictionaryDataEntity> dictionList = new ArrayList<>();
        if (id != null && id.size() > 0) {
            QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().and(
                    t -> t.in(DictionaryDataEntity::getEnCode, id)
                            .or().in(DictionaryDataEntity::getId, id)
            );
            queryWrapper.lambda().orderByAsc(DictionaryDataEntity::getParentId);
            dictionList = this.list(queryWrapper);
        }
        return dictionList;
    }

    @Override
    public DownloadVO exportData(String id) {
        //获取数据分类字段详情
        DictionaryTypeEntity typeEntity = dictionaryTypeService.getInfo(id);
        if (typeEntity == null) {
            throw new DataBaseException(MsgCode.FA001.get());
        }
        DictionaryExportModel exportModel = new DictionaryExportModel();
        //递归子分类
        List<DictionaryTypeEntity> typeList = new ArrayList<>();
        List<DictionaryTypeEntity> typeEntityList = dictionaryTypeService.getList();
        typeList.add(typeEntity);
        getDictionaryTypeEntitySet(typeEntity, typeList, typeEntityList);
        List<DictionaryTypeEntity> collect = typeList.stream().distinct().collect(Collectors.toList());
        //判断是否有子分类
        if (!collect.isEmpty()) {
            exportModel.setList(collect);
        }
        //获取该类型下的数据
        List<DictionaryDataExportModel> modelList = new ArrayList<>();
        for (DictionaryTypeEntity dictionaryTypeEntity : exportModel.getList()) {
            List<DictionaryDataEntity> entityList = getList(dictionaryTypeEntity.getId());
            for (DictionaryDataEntity dictionaryDataEntity : entityList) {
                DictionaryDataExportModel dataExportModel = BeanUtil.toBean(dictionaryDataEntity, DictionaryDataExportModel.class);
                modelList.add(dataExportModel);
            }
        }
        exportModel.setModelList(modelList);
        //导出文件
        DownloadVO downloadVO = fileExport.exportFile(exportModel, configValueUtil.getTemporaryFilePath(), typeEntity.getFullName(), ModuleTypeEnum.SYSTEM_DICTIONARYDATA.getTableName());
        return downloadVO;
    }

    /**
     * 递归字典分类
     *
     * @param dictionaryTypeEntity 数据字典类型实体
     */
    private void getDictionaryTypeEntitySet(DictionaryTypeEntity dictionaryTypeEntity, List<DictionaryTypeEntity> set, List<DictionaryTypeEntity> typeEntityList) {
        //是否含有子分类
        List<DictionaryTypeEntity> collect = typeEntityList.stream().filter(t -> dictionaryTypeEntity.getId().equals(t.getParentId())).collect(Collectors.toList());
        if (collect.size() > 0) {
            for (DictionaryTypeEntity typeEntity : collect) {
                set.add(typeEntity);
                getDictionaryTypeEntitySet(typeEntity, set, typeEntityList);
            }
        }
    }

    @Override
    @DSTransactional
    public ServiceResult importData(DictionaryExportModel exportModel, Integer type) throws DataBaseException {
        try {
            StringBuilder message = new StringBuilder();
            StringJoiner exceptionMessage = new StringJoiner("、");
            List<DictionaryTypeEntity> list = JsonUtil.createJsonToList(exportModel.getList(), DictionaryTypeEntity.class);
            List<DictionaryDataEntity> entityList = JsonUtil.createJsonToList(exportModel.getModelList(), DictionaryDataEntity.class);
            //遍历插入分类
            StringJoiner IDMessage = new StringJoiner("、");
            StringJoiner fullNameMessage = new StringJoiner("、");
            StringJoiner enCodeMessage = new StringJoiner("、");
            Map<String, String> idFor = new HashMap<>();
            for (DictionaryTypeEntity entity : list) {
                String copyNum = UUID.randomUUID().toString().substring(0, 5);
                if (dictionaryTypeService.getInfo(entity.getId()) != null) {
                    IDMessage.add(entity.getId());
                }
                if (dictionaryTypeService.isExistByFullName(entity.getFullName(), null)) {
                    fullNameMessage.add(entity.getFullName());
                }
                if (dictionaryTypeService.isExistByEnCode(entity.getEnCode(), null)) {
                    enCodeMessage.add(entity.getEnCode());
                }
                if ((IDMessage.length() > 0 || fullNameMessage.length() > 0 || enCodeMessage.length() > 0)) {
                    if (ObjectUtil.equal(type, 1)) {
                        entity.setFullName(entity.getFullName() + ".副本" + copyNum);
                        entity.setEnCode(entity.getEnCode() + copyNum);
                        String oldId = entity.getId();
                        entity.setId(RandomUtil.uuId());
                        dictionaryTypeService.setIgnoreLogicDelete().removeById(entity);
                        if (Optional.ofNullable(idFor.get(entity.getParentId())).isPresent()) {
                            entity.setParentId(idFor.get(entity.getParentId()));
                        }
                        dictionaryTypeService.setIgnoreLogicDelete().saveOrUpdate(entity);
                        idFor.put(oldId, entity.getId());
                    }
                } else {
                    dictionaryTypeService.setIgnoreLogicDelete().removeById(entity);
                    dictionaryTypeService.setIgnoreLogicDelete().saveOrUpdate(entity);
                }
            }
            if (IDMessage.length() > 0) {
                exceptionMessage.add("ID（" + IDMessage.toString() + "）重复");
            }
            if (enCodeMessage.length() > 0) {
                exceptionMessage.add("编码（" + IDMessage.toString() + "）重复");
            }
            if (fullNameMessage.length() > 0) {
                exceptionMessage.add("名称（" + IDMessage.toString() + "）重复");
            }
            if (exceptionMessage.length() > 0) {
                message.append(exceptionMessage.toString()).append("；");
                exceptionMessage = new StringJoiner("、");
                IDMessage = new StringJoiner("、");
                fullNameMessage = new StringJoiner("、");
                enCodeMessage = new StringJoiner("、");
            }
            for (DictionaryDataEntity entity1 : entityList) {
                String copyNum = UUID.randomUUID().toString().substring(0, 5);
                if (this.getInfo(entity1.getId()) != null) {
                    IDMessage.add(entity1.getId());
                }
                if (this.isExistByFullName(entity1.getDictionaryTypeId(), entity1.getFullName(), null)) {
                    fullNameMessage.add(entity1.getFullName());
                }
                if (this.isExistByEnCode(entity1.getDictionaryTypeId(), entity1.getEnCode(), null)) {
                    enCodeMessage.add(entity1.getEnCode());
                }
                if (ObjectUtil.equal(type, 1)) {
                    entity1.setId(RandomUtil.uuId());
                    if (Optional.ofNullable(idFor.get(entity1.getDictionaryTypeId())).isPresent()) {
                        entity1.setDictionaryTypeId(idFor.get(entity1.getDictionaryTypeId()));
                    }
                    if (this.isExistByFullName(entity1.getDictionaryTypeId(), entity1.getFullName(), null)
                            || this.isExistByEnCode(entity1.getDictionaryTypeId(), entity1.getEnCode(), null)) {
                        entity1.setFullName(entity1.getFullName() + ".副本" + copyNum);
                        entity1.setEnCode(entity1.getEnCode() + copyNum);
                    }
                    this.setIgnoreLogicDelete().saveOrUpdate(entity1);
                } else if (IDMessage.length() == 0 && fullNameMessage.length() == 0 && enCodeMessage.length() == 0) {
                    this.setIgnoreLogicDelete().removeById(entity1);
                    this.setIgnoreLogicDelete().saveOrUpdate(entity1);
                }
            }
            if (IDMessage.length() > 0) {
                exceptionMessage.add("ID（" + IDMessage.toString() + "）重复");
            }
            if (enCodeMessage.length() > 0) {
                exceptionMessage.add("编码（" + enCodeMessage.toString() + "）重复");
            }
            if (fullNameMessage.length() > 0) {
                exceptionMessage.add("名称（" + fullNameMessage.toString() + "）重复");
            }
            if (exceptionMessage.length() > 0) {
                message.append("modelList：" + exceptionMessage.toString() + "；");
            }
            if (ObjectUtil.equal(type, 0) && message.length() > 0) {
                return ServiceResult.error(message.toString().substring(0, message.lastIndexOf("；")));
            }
            return ServiceResult.success(MsgCode.IMP001.get());
        } catch (Exception e) {
            //手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new DataBaseException(e.getMessage());
        } finally {
            this.clearIgnoreLogicDelete();
            dictionaryTypeService.clearIgnoreLogicDelete();
            this.clearIgnoreLogicDelete();
        }
    }

    @Override
    public List<DictionaryDataEntity> getListByTypeDataCode(String typeCode) {
        DictionaryTypeEntity dictionaryTypeEntity = dictionaryTypeService.getInfoByEnCode(typeCode);
        List<DictionaryDataEntity> list = new ArrayList<>();
        if (dictionaryTypeEntity != null) {
            list = this.getList(dictionaryTypeEntity.getId());
        }
        return list;
    }

}
