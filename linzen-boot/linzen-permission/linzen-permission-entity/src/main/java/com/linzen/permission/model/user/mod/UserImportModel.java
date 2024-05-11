package com.linzen.permission.model.user.mod;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 导入模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserImportModel implements Serializable {

    private String account;

    private String realName;

    private String organizeId;

    private String managerId;

    private String positionId;

    private String roleId;

    private String description;

    private String gender;

    private String nation;

    private String nativePlace;

    private String certificatesType;

    private String certificatesNumber;

    private String education;

    private Date birthday;

    private String telePhone;

    private String landline;

    private String mobilePhone;

    private String email;

    private String urgentContacts;

    private String urgentTelePhone;

    private String postalAddress;

    private Long sortCode;

    private Date entryDate;

    private Integer enabledMark;

    private String ranks;
}
