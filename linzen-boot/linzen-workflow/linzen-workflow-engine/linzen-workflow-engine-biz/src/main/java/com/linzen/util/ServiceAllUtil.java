package com.linzen.util;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.linzen.base.ServiceResult;
import com.linzen.base.Pagination;
import com.linzen.base.entity.DataInterfaceEntity;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.service.BillRuleService;
import com.linzen.base.service.DataInterfaceService;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.util.SentMessageUtil;
import com.linzen.base.vo.DownloadVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.engine.model.flowtemplate.FlowExportModel;
import com.linzen.entity.FlowFormEntity;
import com.linzen.exception.WorkFlowException;
import com.linzen.message.model.SentMessageForm;
import com.linzen.message.service.SendMessageConfigService;
import com.linzen.model.flow.FlowFormDataModel;
import com.linzen.permission.entity.*;
import com.linzen.permission.model.organizeadministrator.OrganizeAdministratorModel;
import com.linzen.permission.service.*;
import com.linzen.service.FlowFormRelationService;
import com.linzen.service.FlowFormService;
import com.linzen.service.FormDataService;
import com.linzen.emnus.DictionaryDataEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.linzen.util.Constants.ADMIN_KEY;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Component
@DS("")
public class ServiceAllUtil {


    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private OrganizeAdministratorService organizeAdministratorService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private BillRuleService billRuleService;
    @Autowired
    private DataInterfaceService dataInterfaceService;
    @Autowired
    private SentMessageUtil sentMessageUtil;
    @Autowired
    private SendMessageConfigService sendMessageConfigService;
    @Autowired
    private FormDataService formDataService;
    @Autowired
    private FlowFormService flowFormService;
    @Autowired
    private FlowFormRelationService flowFormRelationService;
    @Autowired
    private DataFileExport fileExport;
    @Autowired
    private ConfigValueUtil configValueUtil;

    //--------------------------------数据字典------------------------------
    public List<DictionaryDataEntity> getDiList() {
        List<DictionaryDataEntity> dictionList = dictionaryDataService.getListByTypeDataCode(DictionaryDataEnum.FLOWWOEK_ENGINE.getDictionaryTypeId());
        return dictionList;
    }

    public List<DictionaryDataEntity> getDictionName(List<String> id) {
        List<DictionaryDataEntity> dictionList = dictionaryDataService.getDictionName(id);
        return dictionList;
    }

    //--------------------------------用户关系表------------------------------
    public List<SysUserRelationEntity> getListByUserIdAll(List<String> id) {
        List<SysUserRelationEntity> list = userRelationService.getListByUserIdAll(id).stream().filter(t -> StringUtil.isNotEmpty(t.getObjectId())).collect(Collectors.toList());
        return list;
    }

    public List<SysUserRelationEntity> getListByObjectIdAll(List<String> id) {
        List<SysUserRelationEntity> list = userRelationService.getListByObjectIdAll(id);
        return list;
    }

    public String getAdmin() {
        SysUserEntity admin = userService.getUserByAccount(ADMIN_KEY);
        return admin.getId();
    }

    //--------------------------------用户------------------------------
    public List<SysUserEntity> getUserName(List<String> id) {
        List<SysUserEntity> list = getUserName(id, false);
        return list;
    }

    public List<SysUserEntity> getListByManagerId(String managerId) {
        List<SysUserEntity> list = StringUtil.isNotEmpty(managerId) ? userService.getListByManagerId(managerId, null) : new ArrayList<>();
        return list;
    }

    public List<SysUserEntity> getUserName(List<String> id, boolean enableMark) {
        List<SysUserEntity> list = userService.getUserName(id);
        if (enableMark) list = list.stream().filter(t -> t.getEnabledMark() != 0).collect(Collectors.toList());
        return list;
    }

    public List<SysUserEntity> getUserName(List<String> id, Pagination pagination) {
        List<SysUserEntity> list = userService.getUserName(id, pagination);
        return list;
    }

    public SysUserEntity getUserInfo(String id) {
        SysUserEntity entity = null;
        if (StringUtil.isNotEmpty(id)) {
            entity = id.equalsIgnoreCase(ADMIN_KEY) ? userService.getUserByAccount(id) : userService.getInfo(id);
        }
        return entity;
    }

    public List<String> getUserListAll(List<String> idList) {
        List<String> userIdList = userService.getUserIdList(idList, null);
        return userIdList;
    }

    public List<String> getOrganizeUserList(String type) {
        OrganizeAdministratorModel model = organizeAdministratorService.getOrganizeAdministratorList();
        Map<String, List<String>> map = new HashMap<>();
        map.put("select", model.getSelectList());
        map.put("add", model.getAddList());
        map.put("delete", model.getDeleteList());
        map.put("edit", model.getEditList());
        List<String> list = map.get(type) != null ? map.get(type) : new ArrayList<>();
        List<String> userList = userRelationService.getListByObjectIdAll(list).stream().map(SysUserRelationEntity::getUserId).collect(Collectors.toList());
        return userList;
    }

    //--------------------------------单据规则------------------------------

    public void useBillNumber(String enCode) {
        billRuleService.useBillNumber(enCode);
    }

    //--------------------------------角色------------------------------
    public List<SysRoleEntity> getListByIds(List<String> id) {
        List<SysRoleEntity> list = roleService.getListByIds(id, null, false);
        return list;
    }

    //--------------------------------组织------------------------------
    public List<SysOrganizeEntity> getOrganizeName(List<String> id) {
        List<SysOrganizeEntity> list = organizeService.getOrganizeName(id);
        return list;
    }

    public SysOrganizeEntity getOrganizeInfo(String id) {
        SysOrganizeEntity entity = StringUtil.isNotEmpty(id) ? organizeService.getInfo(id) : null;
        return entity;
    }

    public List<SysOrganizeEntity> getOrganizeId(String organizeId) {
        List<SysOrganizeEntity> organizeList = new ArrayList<>();
        organizeService.getOrganizeId(organizeId, organizeList);
        Collections.reverse(organizeList);
        return organizeList;
    }

    public List<SysOrganizeEntity> getDepartmentAll(String organizeId) {
        List<SysOrganizeEntity> departmentAll = organizeService.getDepartmentAll(organizeId);
        return departmentAll;
    }

    //--------------------------------岗位------------------------------
    public List<SysPositionEntity> getPositionName(List<String> id) {
        List<SysPositionEntity> list = positionService.getPositionName(id, false);
        return list;
    }

    //--------------------------------远端------------------------------
    public ServiceResult infoToId(String interId, Map<String, String> parameterMap) {
        return dataInterfaceService.infoToId(interId, null, parameterMap);
    }

    public List<DataInterfaceEntity> getInterfaceList(List<String> id) {
        return dataInterfaceService.getList(id);
    }

    //--------------------------------发送消息------------------------------
    public void sendMessage(List<SentMessageForm> messageListAll) {
        for (SentMessageForm messageForm : messageListAll)
            if (messageForm.isSysMessage()) sentMessageUtil.sendMessage(messageForm);
    }

    public void updateSendConfigUsed(String id, List<String> idList) {
        sendMessageConfigService.updateUsed(id, idList);
    }

    public void sendDelegateMsg(List<SentMessageForm> messageListAll) {
        for (SentMessageForm messageForm : messageListAll) {
            sentMessageUtil.sendDelegateMsg(messageForm);
        }
    }

    //------------------------------导出-------------------------------

    /**
     * 导出流程模板
     *
     * @param model FlowExportModel
     * @return DownloadVO
     */
    public DownloadVO exportData(FlowExportModel model) {
        return fileExport.exportFile(model, configValueUtil.getTemporaryFilePath(), model.getFlowTemplate().getFullName(), ModuleTypeEnum.FLOW_FLOWENGINE.getTableName());
    }

    //------------------------------表单数据-------------------------------
    public void createOrUpdate(FlowFormDataModel flowFormDataModel) throws WorkFlowException {
        formDataService.saveOrUpdate(flowFormDataModel);
    }

    public Map<String, Object> infoData(String formId, String id) throws WorkFlowException {
        Map<String, Object> dataAll = new HashMap<>();
        if (StringUtil.isNotEmpty(formId) && StringUtil.isNotEmpty(id)) {
            Map<String, Object> info = new HashMap<>();
            ServiceResult result = formDataService.info(formId, id);
            if (result.getCode() != 200) {
                throw new WorkFlowException(result.getMsg());
            }
            if (result.getData() instanceof Map) {
                info.putAll((Map) result.getData());
            }
            dataAll.putAll(info);
        }
        return dataAll;
    }

    //------------------------------表单对象-------------------------------
    public FlowFormEntity getForm(String id) {
        FlowFormEntity form = StringUtil.isNotEmpty(id) ? flowFormService.getById(id) : null;
        return form;
    }

    public List<FlowFormEntity> getFlowIdList(String id) {
        List<FlowFormEntity> list = StringUtil.isNotEmpty(id) ? flowFormService.getFlowIdList(id) : new ArrayList<>();
        return list;
    }

    public void updateForm(FlowFormEntity entity) {
        flowFormService.updateForm(entity);
    }

    public void formIdList(List<String> formId, String tempJsonId) {
        flowFormRelationService.saveFlowIdByFormIds(tempJsonId, formId);
    }

    public void deleteFormId(String tempJsonId) {
        flowFormRelationService.saveFlowIdByFormIds(tempJsonId, new ArrayList<>());
    }

}
