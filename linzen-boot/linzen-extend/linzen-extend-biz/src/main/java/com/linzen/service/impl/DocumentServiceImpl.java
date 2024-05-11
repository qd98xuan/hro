package com.linzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.config.ConfigValueUtil;
import com.linzen.entity.DocumentEntity;
import com.linzen.entity.DocumentShareEntity;
import com.linzen.mapper.DocumentMapper;
import com.linzen.service.DocumentService;
import com.linzen.service.DocumentShareService;
import com.linzen.util.FileUtil;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识文档
 *
 * @author FHNP
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class DocumentServiceImpl extends SuperServiceImpl<DocumentMapper, DocumentEntity> implements DocumentService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private DocumentShareService documentShareService;
    @Autowired
    private ConfigValueUtil configValueUtil;

    @Override
    public List<DocumentEntity> getFolderList() {
        QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DocumentEntity::getCreatorUserId, userProvider.get().getUserId())
                .eq(DocumentEntity::getType, 0)
                .eq(DocumentEntity::getEnabledMark, 1)
                .orderByAsc(DocumentEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<DocumentEntity> getAllList(String parentId) {
        QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DocumentEntity::getCreatorUserId, userProvider.get().getUserId())
                .eq(DocumentEntity::getEnabledMark, 1)
                .eq(DocumentEntity::getParentId, parentId)
                .orderByDesc(DocumentEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<DocumentEntity> getTrashList() {
        QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DocumentEntity::getCreatorUserId, userProvider.get().getUserId())
                .eq(DocumentEntity::getEnabledMark, 0)
                .orderByDesc(DocumentEntity::getDeleteTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<DocumentEntity> getShareOutList() {
        QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DocumentEntity::getCreatorUserId, userProvider.get().getUserId())
                .eq(DocumentEntity::getEnabledMark, 1)
                .gt(DocumentEntity::getIsShare, 0)
                .orderByAsc(DocumentEntity::getType)
                .orderByDesc(DocumentEntity::getDeleteTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<DocumentEntity> getShareTomeList() {
        List<DocumentEntity> listAll = new ArrayList<>();
        String userId = userProvider.get().getUserId();
        QueryWrapper<DocumentShareEntity> shareWrapper = new QueryWrapper<>();
        shareWrapper.lambda().eq(DocumentShareEntity::getShareUserId,userId);
        shareWrapper.lambda().select(DocumentShareEntity::getDocumentId);
        List<DocumentShareEntity> list = documentShareService.list(shareWrapper);
        List<String> id = list.stream().map(DocumentShareEntity::getDocumentId).collect(Collectors.toList());
        if(id.size()>0){
            QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(DocumentEntity::getId,id);
            queryWrapper.lambda().eq(DocumentEntity::getEnabledMark,1);
            queryWrapper.lambda().isNull(DocumentEntity::getEnabledMark);
            listAll.addAll(this.list(queryWrapper));
        }
        return listAll;
    }

    @Override
    public List<DocumentShareEntity> getShareUserList(String documentId) {
        QueryWrapper<DocumentShareEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DocumentShareEntity::getDocumentId, documentId);
        return documentShareService.list(queryWrapper);
    }

    @Override
    public DocumentEntity getInfo(String id) {
        QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DocumentEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void delete(DocumentEntity entity) {
        entity.setDeleteTime(new Date());
        entity.setDeleteUserId(userProvider.get().getUserId());
        entity.setEnabledMark(0);
        this.updateById(entity);

    }

    @Override
    public void create(DocumentEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorUserId(userProvider.get().getUserId());
        entity.setEnabledMark(1);
        this.save(entity);
    }

    @Override
    public boolean update(String id, DocumentEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(new Date());
        entity.setUpdateUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    @Transactional
    public boolean sharecreate(String documentId, String[] shareUserId) {
        List<DocumentShareEntity> entitys = new ArrayList<>();
        for (String item : shareUserId) {
            DocumentShareEntity entity = new DocumentShareEntity();
            entity.setId(RandomUtil.uuId());
            entity.setDocumentId(documentId);
            entity.setShareUserId(item);
            entity.setShareTime(new Date());
            entitys.add(entity);
        }
        QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
        DocumentEntity entity = this.getOne(queryWrapper.lambda().eq(DocumentEntity::getId, documentId));
        if (entity != null) {
            entity.setIsShare(entitys.size());
            entity.setShareTime(new Date());
            this.updateById(entity);
            QueryWrapper<DocumentShareEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(DocumentShareEntity::getDocumentId, documentId);
            documentShareService.remove(wrapper);
            for (DocumentShareEntity shareEntity : entitys) {
                documentShareService.save(shareEntity);
            }
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean shareCancel(String documentId) {
        QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DocumentEntity::getId, documentId);
        DocumentEntity entity = this.getOne(queryWrapper);
        if (entity != null) {
            entity.setIsShare(0);
            entity.setShareTime(new Date());
            this.updateById(entity);
            QueryWrapper<DocumentShareEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(DocumentShareEntity::getDocumentId, documentId);
            documentShareService.remove(wrapper);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void trashdelete(String folderId) {
        DocumentEntity entity = this.getInfo(folderId);
        if(entity!=null){
            this.removeById(folderId);
            FileUtil.deleteFile(configValueUtil.getDocumentFilePath() + entity.getFilePath());
        }
//        List<DocumentEntity> list = this.baseMapper.GetChildList(folderId);
//        List<String> deleteId = new ArrayList<>();
//        for (DocumentEntity entity : list) {
//            if(!StringUtil.isEmpty(entity.getFilePath())){
//                FileUtil.deleteFile(configValueUtil.getDocumentFilePath() + entity.getFilePath());
//            }
//            deleteId.add(entity.getId());
//        }
//        this.removeByIds(deleteId);
    }

    @Override
    public boolean trashRecovery(String id) {
        UpdateWrapper<DocumentEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(DocumentEntity::getEnabledMark,1);
        updateWrapper.lambda().set(DocumentEntity::getDeleteTime,null);
        updateWrapper.lambda().set(DocumentEntity::getDeleteUserId,null);
        updateWrapper.lambda().eq(DocumentEntity::getId,id);
       return this.update(updateWrapper);
    }

    @Override
    public boolean moveTo(String id, String toId) {
        DocumentEntity entity = this.getInfo(id);
        if(entity!=null){
            entity.setParentId(toId);
            this.updateById(entity);
           return true;
        }
       return false;
    }

    @Override
    public boolean isExistByFullName(String fullName, String id, String parentId) {
        String userId=userProvider.get().getUserId();
        QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DocumentEntity::getFullName,fullName).eq(DocumentEntity::getEnabledMark, 1).eq(DocumentEntity::getCreatorUserId,userId);
        queryWrapper.lambda().eq(DocumentEntity::getParentId, parentId);
        if(!StringUtil.isEmpty(id)){
            queryWrapper.lambda().ne(DocumentEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }
}
