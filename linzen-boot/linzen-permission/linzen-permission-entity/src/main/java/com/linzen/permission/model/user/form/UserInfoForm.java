package com.linzen.permission.model.user.form;

import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserInfoForm {
    private String signature;
    private String gender;
    private String nation;
    private String nativePlace;
    private String entryDate;
    private String certificatesType;
    private String certificatesNumber;
    private String education;
    private Long birthday;
    private String telePhone;
    private String landline;
    private String mobilePhone;
    private String email;
    private String urgentContacts;
    private String urgentTelePhone;
    private String postalAddress;
    private String realName;
}
