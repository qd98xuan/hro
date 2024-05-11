package com.linzen.permission.model.user.vo;

import com.linzen.permission.model.user.mod.UserAuthorizeModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthorizeVO {
    private List<UserAuthorizeModel> button;
    private List<UserAuthorizeModel> column;
    private List<UserAuthorizeModel> module;
    private List<UserAuthorizeModel> resource;
    private List<UserAuthorizeModel> form;
    private List<UserAuthorizeModel> portal;
}
