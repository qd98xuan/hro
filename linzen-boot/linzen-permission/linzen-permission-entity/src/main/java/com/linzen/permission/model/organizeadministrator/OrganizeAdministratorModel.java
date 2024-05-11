package com.linzen.permission.model.organizeadministrator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织管理模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizeAdministratorModel {

    private List<String> addList = new ArrayList<>();
    private List<String> editList = new ArrayList<>();
    private List<String> deleteList = new ArrayList<>();
    private List<String> selectList = new ArrayList<>();
}
