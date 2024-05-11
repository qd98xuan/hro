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
import com.linzen.message.entity.MessageTemplateConfigEntity;
import com.linzen.message.entity.SmsFieldEntity;
import com.linzen.message.entity.TemplateParamEntity;
import com.linzen.message.mapper.MessageTemplateConfigMapper;
import com.linzen.message.model.messagetemplateconfig.MessageTemplateConfigForm;
import com.linzen.message.model.messagetemplateconfig.MessageTemplateConfigPagination;
import com.linzen.message.model.messagetemplateconfig.TemplateParamModel;
import com.linzen.message.service.MessageTemplateConfigService;
import com.linzen.message.service.SmsFieldService;
import com.linzen.message.service.TemplateParamService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 消息模板（新）
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Service
public class MessageTemplateConfigServiceImpl extends SuperServiceImpl<MessageTemplateConfigMapper, MessageTemplateConfigEntity> implements MessageTemplateConfigService {



    @Autowired
    private UserProvider userProvider;

    @Autowired
    private AuthorizeService authorizeService;


    @Autowired
    private TemplateParamService templateParamService;

    @Autowired
    private SmsFieldService smsFieldService;


    @Override
    public List<MessageTemplateConfigEntity> getList(MessageTemplateConfigPagination MessageTemplateConfigPagination) {
        return getTypeList(MessageTemplateConfigPagination, MessageTemplateConfigPagination.getDataType());
    }

    @Override
    public List<MessageTemplateConfigEntity> getTypeList(MessageTemplateConfigPagination MessageTemplateConfigPagination, String dataType) {
        String userId = userProvider.get().getUserId();
        int total = 0;
        int messageTemplateNewNum = 0;
        QueryWrapper<MessageTemplateConfigEntity> messageTemplateNewQueryWrapper = new QueryWrapper<>();
        int templateParamNum = 0;
        QueryWrapper<TemplateParamEntity> templateParamQueryWrapper = new QueryWrapper<>();
        int smsFieldNum = 0;
        QueryWrapper<SmsFieldEntity> smsFieldQueryWrapper = new QueryWrapper<>();
        //关键字
        if (ObjectUtil.isNotEmpty(MessageTemplateConfigPagination.getKeyword())) {
            messageTemplateNewNum++;
            messageTemplateNewQueryWrapper.lambda().and(t -> t.like(MessageTemplateConfigEntity::getEnCode, MessageTemplateConfigPagination.getKeyword()).
                    or().like(MessageTemplateConfigEntity::getFullName, MessageTemplateConfigPagination.getKeyword()));
        }
        //模板类型
        if (ObjectUtil.isNotEmpty(MessageTemplateConfigPagination.getTemplateType())) {
            messageTemplateNewNum++;
            messageTemplateNewQueryWrapper.lambda().eq(MessageTemplateConfigEntity::getTemplateType, MessageTemplateConfigPagination.getTemplateType());
        }
        //消息类型
        if (ObjectUtil.isNotEmpty(MessageTemplateConfigPagination.getMessageType())) {
            messageTemplateNewNum++;
            messageTemplateNewQueryWrapper.lambda().eq(MessageTemplateConfigEntity::getMessageType, MessageTemplateConfigPagination.getMessageType());
        }
        //消息来源
        if (ObjectUtil.isNotEmpty(MessageTemplateConfigPagination.getMessageSource())) {
            messageTemplateNewNum++;
            messageTemplateNewQueryWrapper.lambda().eq(MessageTemplateConfigEntity::getMessageSource, MessageTemplateConfigPagination.getMessageSource());
        }
        //状态
        if (ObjectUtil.isNotEmpty(MessageTemplateConfigPagination.getEnabledMark())) {
            messageTemplateNewNum++;
            int enabledMark = Integer.parseInt(MessageTemplateConfigPagination.getEnabledMark());
            messageTemplateNewQueryWrapper.lambda().eq(MessageTemplateConfigEntity::getEnabledMark, enabledMark);
        }

        //排序
        if (StringUtil.isEmpty(MessageTemplateConfigPagination.getSidx())) {
            messageTemplateNewQueryWrapper.lambda().orderByAsc(MessageTemplateConfigEntity::getSortCode).orderByDesc(MessageTemplateConfigEntity::getCreatorTime).orderByDesc(MessageTemplateConfigEntity::getUpdateTime);
        } else {
            try {
                String sidx = MessageTemplateConfigPagination.getSidx();
                MessageTemplateConfigEntity MessageTemplateConfigEntity = new MessageTemplateConfigEntity();
                Field declaredField = MessageTemplateConfigEntity.getClass().getDeclaredField(sidx);
                declaredField.setAccessible(true);
                String value = declaredField.getAnnotation(TableField.class).value();
                messageTemplateNewQueryWrapper = "asc".equals(MessageTemplateConfigPagination.getSort().toLowerCase()) ? messageTemplateNewQueryWrapper.orderByAsc(value) : messageTemplateNewQueryWrapper.orderByDesc(value);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        if (!"1".equals(dataType)) {
            if (total > 0|| total == 0) {
                Page<MessageTemplateConfigEntity> page = new Page<>(MessageTemplateConfigPagination.getCurrentPage(), MessageTemplateConfigPagination.getPageSize());
                IPage<MessageTemplateConfigEntity> userIPage = this.page(page, messageTemplateNewQueryWrapper);
                return MessageTemplateConfigPagination.setData(userIPage.getRecords(), userIPage.getTotal());
            } else {
                List<MessageTemplateConfigEntity> list = new ArrayList();
                return MessageTemplateConfigPagination.setData(list, list.size());
            }
        } else {
            return this.list(messageTemplateNewQueryWrapper);
        }
    }


    @Override
    public MessageTemplateConfigEntity getInfo(String id) {
        QueryWrapper<MessageTemplateConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageTemplateConfigEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public MessageTemplateConfigEntity getInfoByEnCode(String enCode,String messageType) {
        QueryWrapper<MessageTemplateConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageTemplateConfigEntity::getEnCode, enCode);
        queryWrapper.lambda().eq(MessageTemplateConfigEntity::getMessageType, messageType);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(MessageTemplateConfigEntity entity) {
        this.save(entity);
    }

    @Override
    public boolean update(String id, MessageTemplateConfigEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(MessageTemplateConfigEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

    //子表方法
    @Override
    public List<TemplateParamEntity> getTemplateParamList(String id, MessageTemplateConfigPagination MessageTemplateConfigPagination) {
        QueryWrapper<TemplateParamEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TemplateParamEntity::getTemplateId, id);
        return templateParamService.list(templateParamService.getChild(MessageTemplateConfigPagination, queryWrapper));
    }

    @Override
    public List<TemplateParamEntity> getTemplateParamList(String id) {
        QueryWrapper<TemplateParamEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TemplateParamEntity::getTemplateId, id);
        return templateParamService.list(queryWrapper);
    }

    @Override
    public List<SmsFieldEntity> getSmsFieldList(String id, MessageTemplateConfigPagination MessageTemplateConfigPagination) {
        QueryWrapper<SmsFieldEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SmsFieldEntity::getTemplateId, id);
        return smsFieldService.list(smsFieldService.getChild(MessageTemplateConfigPagination, queryWrapper));
    }

    @Override
    public List<SmsFieldEntity> getSmsFieldList(String id) {
        QueryWrapper<SmsFieldEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SmsFieldEntity::getTemplateId, id);
        return smsFieldService.list(queryWrapper);
    }


    //验证表单唯一字段
    @Override
    public boolean checkForm(MessageTemplateConfigForm form, int i,String id) {
        int total = 0;
        if (ObjectUtil.isNotEmpty(form.getEnCode())) {
            QueryWrapper<MessageTemplateConfigEntity> codeWrapper = new QueryWrapper<>();
            codeWrapper.lambda().eq(MessageTemplateConfigEntity::getEnCode, form.getEnCode());
            if(StringUtil.isNotBlank(id) && !"null".equals(id)) {
                codeWrapper.lambda().ne(MessageTemplateConfigEntity::getId, id);
            }
            if ((int) this.count(codeWrapper) > i) {
                total++;
            }
        }
        if (form.getTemplateParamList() != null) {
        }
        if (form.getSmsFieldList() != null) {
        }
        if (total > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<MessageTemplateConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageTemplateConfigEntity::getFullName, fullName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(MessageTemplateConfigEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<MessageTemplateConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageTemplateConfigEntity::getEnCode, enCode);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(MessageTemplateConfigEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public ServiceResult ImportData(MessageTemplateConfigEntity entity) throws DataBaseException {
        if (entity != null) {
//            if (isExistByFullName(entity.getFullName(), null)) {
//                return ServiceResult.error(MsgCode.EXIST001.get());
//            }
            if (isExistByEnCode(entity.getEnCode(), entity.getId())) {
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


    @Override
    public List<TemplateParamModel> getParamJson(String id) {
        MessageTemplateConfigEntity entity = getInfo(id);
        List<TemplateParamModel> paramModelList = new ArrayList<>();
        List<String> smsField = new ArrayList<>();
        List<String> paramList = new ArrayList<>();
        if (entity != null) {
            if ("3".equals(entity.getMessageType()) || "7".equals(entity.getMessageType())) {
                List<SmsFieldEntity> smsFieldList = smsFieldService.getDetailListByParentId(id);
                for (SmsFieldEntity entity1 : smsFieldList) {
                    if(!"@flowLink".equals(entity1.getField())) {
                        smsField.add(entity1.getField());
                    }
                }
                List<TemplateParamEntity> paramFieldList = templateParamService.getDetailListByParentId(id);
                for (TemplateParamEntity entity1 : paramFieldList) {
                    if (smsField.contains(entity1.getField())) {
                        TemplateParamModel paramModel = new TemplateParamModel();
                        paramModel.setTemplateId(entity.getId());
                        paramModel.setTemplateCode(entity.getEnCode());
                        paramModel.setTemplateType(entity.getTemplateType());
                        paramModel.setField(entity1.getField());
                        paramModel.setFieldName(entity1.getFieldName());
                        paramModel.setId(entity1.getId());
                        paramModel.setTemplateName(entity.getFullName());
                        paramModelList.add(paramModel);
                    }
                }
            } else {
                String content = StringUtil.isNotEmpty(entity.getContent()) ? entity.getContent() : "";
                String title = StringUtil.isNotEmpty(entity.getTitle()) ? entity.getTitle() : "";
                Set<String> list = new HashSet<>();
                list.addAll(regexContent(content));
                list.addAll(regexContent(title));
                List<TemplateParamEntity> paramFieldList = templateParamService.getDetailListByParentId(id);
                for (TemplateParamEntity entity1 : paramFieldList) {
                    TemplateParamModel paramModel = new TemplateParamModel();
                    paramModel.setTemplateId(entity.getId());
                    paramModel.setTemplateCode(entity.getEnCode());
                    paramModel.setTemplateType(entity.getTemplateType());
                    paramModel.setField(entity1.getField());
                    paramModel.setFieldName(entity1.getFieldName());
                    paramModel.setId(entity1.getId());
                    paramModel.setTemplateName(entity.getFullName());
                    if(list.contains(entity1.getField())){
                        if (!"@FlowLink".equals(entity1.getField())) {
                            paramModelList.add(paramModel);
                        }
                    }
                }
            }
        }
        //将参数模板转为json格式数据
//        String data = JsonUtil.createObjectToString(paramModelList);
        return paramModelList;
    }

    //获取消息内容参数
    public List<String> regexContent(String content) {
        List<String> list = new ArrayList<>();
        String pattern = "[{]([^}]+)[}]";
        Pattern patternList = Pattern.compile(pattern);
        Matcher m = patternList.matcher(content);
        while (m.find()) {
            String group = m.group().replaceAll("\\{", "").replaceAll("}", "");
            list.add(group);
        }
        return list;
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
