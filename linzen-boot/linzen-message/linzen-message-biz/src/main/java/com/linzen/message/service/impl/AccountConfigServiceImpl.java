package com.linzen.message.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.ServiceResult;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.constant.MsgCode;
import com.linzen.database.model.superQuery.ConditionJsonModel;
import com.linzen.database.model.superQuery.SuperQueryConditionModel;
import com.linzen.exception.DataBaseException;
import com.linzen.message.entity.AccountConfigEntity;
import com.linzen.message.mapper.AccountConfigMapper;
import com.linzen.message.model.accountconfig.AccountConfigForm;
import com.linzen.message.model.accountconfig.AccountConfigPagination;
import com.linzen.message.service.AccountConfigService;
import com.linzen.permission.service.AuthorizeService;
import com.linzen.util.DateUtil;
import com.linzen.util.JsonUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import com.linzen.util.visiual.ProjectKeyConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 账号配置功能
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Service
public class AccountConfigServiceImpl extends SuperServiceImpl<AccountConfigMapper, AccountConfigEntity> implements AccountConfigService {


    @Autowired
    private UserProvider userProvider;

    @Autowired
    private AuthorizeService authorizeService;


    @Override
    public List<AccountConfigEntity> getList(AccountConfigPagination accountConfigPagination) {
        return getTypeList(accountConfigPagination, accountConfigPagination.getDataType());
    }

    @Override
    public List<AccountConfigEntity> getTypeList(AccountConfigPagination accountConfigPagination, String dataType) {
        String userId = userProvider.get().getUserId();
        int total = 0;
        int accountConfigNum = 0;
        QueryWrapper<AccountConfigEntity> accountConfigQueryWrapper = new QueryWrapper<>();

        //关键字
        if (StringUtil.isNotBlank(accountConfigPagination.getKeyword()) && !"null".equals(accountConfigPagination.getKeyword())) {
            accountConfigNum++;
            accountConfigQueryWrapper.lambda().and(t -> t.like(AccountConfigEntity::getEnCode, accountConfigPagination.getKeyword())
                    .or().like(AccountConfigEntity::getFullName, accountConfigPagination.getKeyword()).or().like(AccountConfigEntity::getAddressorName,accountConfigPagination.getKeyword())
                    .or().like(AccountConfigEntity::getSmtpUser,accountConfigPagination.getKeyword()).or().like(AccountConfigEntity::getSmsSignature,accountConfigPagination.getKeyword()));
        }
        //webhook类型
        if (ObjectUtil.isNotEmpty(accountConfigPagination.getWebhookType())) {
            accountConfigNum++;
            accountConfigQueryWrapper.lambda().eq(AccountConfigEntity::getWebhookType, accountConfigPagination.getWebhookType());
        }
        //渠道
        if (ObjectUtil.isNotEmpty(accountConfigPagination.getChannel())) {
            accountConfigNum++;
            accountConfigQueryWrapper.lambda().eq(AccountConfigEntity::getChannel, accountConfigPagination.getChannel());
        }
        //状态
        if(ObjectUtil.isNotEmpty(accountConfigPagination.getEnabledMark())){
            accountConfigNum++;
            int enabledMark = Integer.parseInt(accountConfigPagination.getEnabledMark());
            accountConfigQueryWrapper.lambda().eq(AccountConfigEntity::getEnabledMark, enabledMark);
        }
        //配置类型
        if (ObjectUtil.isNotEmpty(accountConfigPagination.getType())) {
            accountConfigNum++;
            accountConfigQueryWrapper.lambda().eq(AccountConfigEntity::getType, accountConfigPagination.getType());
        }

        //排序
        if (StringUtil.isEmpty(accountConfigPagination.getSidx())) {
            accountConfigQueryWrapper.lambda().orderByAsc(AccountConfigEntity::getSortCode).orderByDesc(AccountConfigEntity::getCreatorTime).orderByDesc(AccountConfigEntity::getUpdateTime);
        } else {
            try {
                String sidx = accountConfigPagination.getSidx();
                AccountConfigEntity accountConfigEntity = new AccountConfigEntity();
                Field declaredField = accountConfigEntity.getClass().getDeclaredField(sidx);
                declaredField.setAccessible(true);
                String value = declaredField.getAnnotation(TableField.class).value();
                accountConfigQueryWrapper = "asc".equals(accountConfigPagination.getSort().toLowerCase()) ? accountConfigQueryWrapper.orderByAsc(value) : accountConfigQueryWrapper.orderByDesc(value);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        if (!"1".equals(dataType)) {
            if (total > 0 || total == 0) {
                Page<AccountConfigEntity> page = new Page<>(accountConfigPagination.getCurrentPage(), accountConfigPagination.getPageSize());
                IPage<AccountConfigEntity> userIPage = this.page(page, accountConfigQueryWrapper);
                return accountConfigPagination.setData(userIPage.getRecords(), userIPage.getTotal());
            } else {
                List<AccountConfigEntity> list = new ArrayList();
                return accountConfigPagination.setData(list, list.size());
            }
        } else {
            return this.list(accountConfigQueryWrapper);
        }
    }


    @Override
    public AccountConfigEntity getInfo(String id) {
        QueryWrapper<AccountConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AccountConfigEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(AccountConfigEntity entity) {
        this.save(entity);
    }

    @Override
    public boolean update(String id, AccountConfigEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(AccountConfigEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }
    //子表方法

    //列表子表数据方法


    //验证表单唯一字段
    @Override
    public boolean checkForm(AccountConfigForm form, int i,String type,String id) {
        int total = 0;
        if (ObjectUtil.isNotEmpty(form.getEnCode())) {
            QueryWrapper<AccountConfigEntity> codeWrapper = new QueryWrapper<>();
            codeWrapper.lambda().eq(AccountConfigEntity::getEnCode, form.getEnCode());
            codeWrapper.lambda().eq(AccountConfigEntity::getType,type);
            if(StringUtil.isNotBlank(id) && !"null".equals(id)) {
                codeWrapper.lambda().ne(AccountConfigEntity::getId, id);
            }
            total += (int) this.count(codeWrapper);
        }
        int c = 0;
        if (total > i + c) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkGzhId(String gzhId, int i,String type,String id) {
        int total = 0;
        if (StringUtil.isNotEmpty(gzhId) && !"null".equals(gzhId)) {
            QueryWrapper<AccountConfigEntity> codeWrapper = new QueryWrapper<>();
            codeWrapper.lambda().eq(AccountConfigEntity::getAppKey, gzhId);
            codeWrapper.lambda().eq(AccountConfigEntity::getType,type);
            if(StringUtil.isNotBlank(id) && !"null".equals(id)) {
                codeWrapper.lambda().ne(AccountConfigEntity::getId, id);
            }
            total += (int) this.count(codeWrapper);
        }
        int c = 0;
        if (total > i + c) {
            return true;
        }
        return false;
    }

    @Override
    public AccountConfigEntity getInfoByType(String appKey, String type) {
        QueryWrapper<AccountConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AccountConfigEntity::getType, type);
        queryWrapper.lambda().eq(AccountConfigEntity::getAppKey,appKey);
        return this.getOne(queryWrapper);
    }

    @Override
    public AccountConfigEntity getInfoByEnCode(String enCode, String type){
        QueryWrapper<AccountConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AccountConfigEntity::getType, type);
        queryWrapper.lambda().eq(AccountConfigEntity::getEnCode,enCode);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<AccountConfigEntity> getListByType(String type){
        QueryWrapper<AccountConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AccountConfigEntity::getType,type);
        queryWrapper.lambda().eq(AccountConfigEntity::getEnabledMark,1);
        return this.list(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<AccountConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AccountConfigEntity::getFullName, fullName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(AccountConfigEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id,String type) {
        QueryWrapper<AccountConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AccountConfigEntity::getEnCode, enCode);
        queryWrapper.lambda().eq(AccountConfigEntity::getType,type);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(AccountConfigEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public ServiceResult ImportData(AccountConfigEntity entity) throws DataBaseException {
        if (entity != null) {
//            if (isExistByFullName(entity.getFullName(), entity.getId())) {
//                return ServiceResult.error(MsgCode.EXIST001.get());
//            }
            if (isExistByEnCode(entity.getEnCode(), entity.getId(),entity.getType())) {
                return ServiceResult.error(MsgCode.EXIST002.get());
            }
            try {
                this.save(entity);
            } catch (Exception e) {
                throw new DataBaseException(MsgCode.IMP003.get());
            }
            return ServiceResult.success(MsgCode.IMP001.get());
        }
        return ServiceResult.error("导入数据格式不正确");
    }

    /**
     * 高级查询
     *
     * @param conditionModel
     * @param entity
     * @param num
     * @return
     */
    public Integer getCondition(SuperQueryConditionModel conditionModel, Object entity, int num) {
        QueryWrapper<?> queryWrapper = conditionModel.getObj();
        List<ConditionJsonModel> queryConditionModels = conditionModel.getConditionList();
        String op = conditionModel.getMatchLogic();
        String tableName = conditionModel.getTableName();
        List<ConditionJsonModel> useCondition = new ArrayList<>();
        for (ConditionJsonModel queryConditionModel : queryConditionModels) {
            if (queryConditionModel.getTableName().equalsIgnoreCase(tableName)) {
                if (queryConditionModel.getField().contains("linzen")) {
                    String child = queryConditionModel.getField();
                    String s1 = child.substring(child.lastIndexOf("linzen_")).replace("linzen_", "");
                    queryConditionModel.setField(s1);
                }
                if (queryConditionModel.getField().toLowerCase().startsWith(ProjectKeyConsts.CHILD_TABLE_PREFIX)) {
                    String child = queryConditionModel.getField();
                    String s1 = child.substring(child.indexOf("-") + 1);
                    queryConditionModel.setField(s1);
                }
                useCondition.add(queryConditionModel);
            }
        }

        if (queryConditionModels.size() < 1 || useCondition.size() < 1) {
            return num;
        }
        if (useCondition.size() > 0) {
            num += 1;
        }
        //处理控件 转换为有效值
        for (ConditionJsonModel queryConditionModel : useCondition) {
            String projectKey = queryConditionModel.getProjectKey();
            String fieldValue = queryConditionModel.getFieldValue();
            if (projectKey.equals(ProjectKeyConsts.DATE)) {
                Long o1 = Long.valueOf(fieldValue);
                String startTime = DateUtil.daFormat(o1);
                queryConditionModel.setFieldValue(startTime);
            } else if (projectKey.equals(ProjectKeyConsts.CREATETIME) || projectKey.equals(ProjectKeyConsts.MODIFYTIME)) {
                Long o1 = Long.valueOf(fieldValue);
                String startTime = DateUtil.daFormatHHMMSS(o1);
                queryConditionModel.setFieldValue(startTime);
            } else if (projectKey.equals(ProjectKeyConsts.CURRORGANIZE)) {
                List<String> orgList = JsonUtil.createJsonToList(fieldValue, String.class);
                queryConditionModel.setFieldValue(orgList.get(orgList.size() - 1));
            }
        }
        //反射获取数据库实际字段
        Class<?> aClass = entity.getClass();

        queryWrapper.and(tw -> {
            for (ConditionJsonModel conditionJsonModel : useCondition) {
                String conditionField = conditionJsonModel.getField();
                Field declaredField = null;
                try {
                    declaredField = aClass.getDeclaredField(conditionField);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                declaredField.setAccessible(true);
                String field = declaredField.getAnnotation(TableField.class).value();
                String fieldValue = conditionJsonModel.getFieldValue();
                String symbol = conditionJsonModel.getSymbol();
                if ("AND".equalsIgnoreCase(op)) {
                    if (symbol.equals("==")) {
                        tw.eq(field, fieldValue);
                    } else if (symbol.equals(">=")) {
                        tw.ge(field, fieldValue);
                    } else if (symbol.equals("<=")) {
                        tw.le(field, fieldValue);
                        tw.and(
                                qw -> qw.ne(field, "")
                        );
                    } else if (symbol.equals(">")) {
                        tw.gt(field, fieldValue);
                    } else if (symbol.equals("<")) {
                        tw.lt(field, fieldValue);
                        tw.and(
                                qw -> qw.ne(field, "")
                        );
                    } else if (symbol.equals("<>")) {
                        tw.ne(field, fieldValue);
                        if (StringUtil.isNotEmpty(fieldValue)) {
                            tw.or(
                                    qw -> qw.isNull(field)
                            );
                        }
                    } else if (symbol.equals("like")) {
                        if (StringUtil.isNotEmpty(fieldValue)) {
                            tw.like(field, fieldValue);
                        } else {
                            tw.isNull(field);
                        }
                    } else if (symbol.equals("notLike")) {
                        if (StringUtil.isNotEmpty(fieldValue)) {
                            tw.notLike(field, fieldValue);
                            tw.or(
                                    qw -> qw.isNull(field)
                            );
                        } else {
                            tw.isNotNull(field);
                        }
                    }
                } else {
                    if (symbol.equals("==")) {
                        tw.or(
                                qw -> qw.eq(field, fieldValue)
                        );
                    } else if (symbol.equals(">=")) {
                        tw.or(
                                qw -> qw.ge(field, fieldValue)
                        );
                    } else if (symbol.equals("<=")) {
                        tw.or(
                                qw -> qw.le(field, fieldValue)
                        );
                    } else if (symbol.equals(">")) {
                        tw.or(
                                qw -> qw.gt(field, fieldValue)
                        );
                    } else if (symbol.equals("<")) {
                        tw.or(
                                qw -> qw.lt(field, fieldValue)
                        );
                    } else if (symbol.equals("<>")) {
                        tw.or(
                                qw -> qw.ne(field, fieldValue)
                        );
                        if (StringUtil.isNotEmpty(fieldValue)) {
                            tw.or(
                                    qw -> qw.isNull(field)
                            );
                        }
                    } else if (symbol.equals("like")) {
                        if (StringUtil.isNotEmpty(fieldValue)) {
                            tw.or(
                                    qw -> qw.like(field, fieldValue)
                            );
                        } else {
                            tw.or(
                                    qw -> qw.isNull(field)
                            );
                        }
                    } else if (symbol.equals("notLike")) {
                        if (StringUtil.isNotEmpty(fieldValue)) {
                            tw.or(
                                    qw -> qw.notLike(field, fieldValue)
                            );
                            tw.or(
                                    qw -> qw.isNull(field)
                            );
                        } else {
                            tw.or(
                                    qw -> qw.isNotNull(field)
                            );
                        }
                    }
                }
            }
        });
        return num;
    }
}
