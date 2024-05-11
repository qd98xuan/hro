package com.linzen.util;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.linzen.base.Page;
import com.linzen.base.Pagination;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.service.*;
import com.linzen.database.model.dbfield.DbFieldModel;
import com.linzen.database.model.dbfield.base.DbFieldModelBase;
import com.linzen.database.model.dbtable.DbTableFieldModel;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.permission.entity.*;
import com.linzen.permission.model.user.vo.UserByRoleVO;
import com.linzen.permission.service.*;
import com.linzen.emnus.DictionaryDataEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
public class ServiceBaseUtil {

    @Autowired
    private DbLinkService dblinkService;
    @Autowired
    private DbTableService dbTableService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;
    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private BillRuleService billRuleService;
    @Autowired
    private DataInterfaceService dataInterfaceService;

    //--------------------------------数据连接------------------------------
    public DbLinkEntity getDbLink(String dbLink) {
        DbLinkEntity link = StringUtil.isNotEmpty(dbLink) ? dblinkService.getInfo(dbLink) : null;
        return link;
    }

    public void createTable(List<DbTableFieldModel> dbTable) throws Exception {
        for (DbTableFieldModel dbTableFieldModel : dbTable) {
            dbTableService.createTable(dbTableFieldModel);
        }
    }

    public void addField(DbTableFieldModel dbTable) throws Exception {
        dbTableService.addField(dbTable);
    }

    public List<DbFieldModelBase> getDbTableModel(String linkId, String table) throws Exception {
        List<DbFieldModel> dbFieldModelList = dbTableService.getDbTableModel(linkId, table).getDbFieldModelList();
        List<DbFieldModelBase> list = JsonUtil.createJsonToList(dbFieldModelList,DbFieldModelBase.class);
        return list;
    }

    /**
     * 获取所有字段
     * @param linkId 链接名
     * @param table 表名
     * @return
     * @throws Exception
     */
    public List<DbFieldModel> getFieldList(String linkId, String table) throws Exception {
        return dbTableService.getFieldList(linkId, table);
    }


    //--------------------------------数据字典------------------------------
    public List<DictionaryDataEntity> getDiList() {
        List<DictionaryDataEntity> dictionList = dictionaryDataService.getList(dictionaryTypeService.getInfoByEnCode(DictionaryDataEnum.FLOWWOEK_ENGINE.getDictionaryTypeId()).getId());
        return dictionList;
    }

    public List<DictionaryDataEntity> getDictionName(List<String> id) {
        List<DictionaryDataEntity> dictionList = dictionaryDataService.getDictionName(id);
        return dictionList;
    }

    //--------------------------------用户关系表------------------------------
    public List<SysUserRelationEntity> getListByUserIdAll(List<String> id) {
        List<SysUserRelationEntity> list = userRelationService.getListByUserIdAll(id);
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
        if (enableMark) {
            list = list.stream().filter(t -> t.getEnabledMark() != 0).collect(Collectors.toList());
        }
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

    public SysUserEntity getByRealName(String realName) {
        SysUserEntity entity = StringUtil.isNotEmpty(realName) ? userService.getByRealName(realName) : null;
        return entity;
    }

    public List<UserByRoleVO> getListByAuthorize(String organizeId) {
        List<UserByRoleVO> list = userService.getListByAuthorize(organizeId, new Page());
        return list;
    }

    //--------------------------------单据规则------------------------------
    public String getBillNumber(String enCode) {
        String billNo = "";
        try {
            billNo = billRuleService.getBillNumber(enCode, false);
        } catch (Exception e) {

        }
        return billNo;
    }

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

    public SysOrganizeEntity getOrganizeFullName(String fullName) {
        SysOrganizeEntity entity = organizeService.getByFullName(fullName);
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

    /**
     * 获取当前组织名称（all-显示组织名,else 显示部门名）
     *
     * @param obj
     * @param showLevel
     * @return
     */
    public String getCurrentOrganizeName(Object obj, String showLevel) {
        if(obj==null){
            return null;
        }
        String value=String.valueOf(obj);
        String orgName = "";
        if (value != null) {
            String orgId = "";
            try {
                List<String> jsonToList = JsonUtil.createJsonToList(value, String.class);
                orgId = jsonToList.get(jsonToList.size() - 1);
            } catch (Exception e) {
                orgId = value;
            }
            SysOrganizeEntity organizeEntity = this.getOrganizeInfo(orgId);
            if ("all".equals(showLevel)) {
                if (organizeEntity != null) {
                    List<SysOrganizeEntity> organizeList = this.getOrganizeId(organizeEntity.getId());
                    orgName = organizeList.stream().map(SysOrganizeEntity::getFullName).collect(Collectors.joining("/"));
                }
            } else {
                if (organizeEntity != null) {
                    orgName = organizeEntity.getFullName();
                } else {
                    orgName = " ";
                }
            }
        }
        return orgName;
    }

    //--------------------------------岗位------------------------------
    public List<SysPositionEntity> getPositionName(List<String> id) {
        List<SysPositionEntity> list = positionService.getPositionName(id, false);
        return list;
    }

    public SysPositionEntity getPositionFullName(String fullName) {
        SysPositionEntity entity = positionService.getByFullName(fullName);
        return entity;
    }

    public SysPositionEntity getPositionInfo(String id) {
        SysPositionEntity entity = StringUtil.isNotEmpty(id) ? positionService.getInfo(id) : null;
        return entity;
    }

    //--------------------------------远端------------------------------
    public void infoToId(String interId, Map<String, String> parameterMap) {
        dataInterfaceService.infoToId(interId, null, parameterMap);
    }

}
