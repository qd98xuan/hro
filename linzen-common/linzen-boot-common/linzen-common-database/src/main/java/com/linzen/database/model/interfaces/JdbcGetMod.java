package com.linzen.database.model.interfaces;

import com.linzen.database.model.dto.ModelDTO;

import java.sql.SQLException;

/**
 * 数据模板接口
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface JdbcGetMod {

     /**
      * 设置自定义模板接口
      * @param modelDTO 模板相关参数
      * @throws SQLException ignore
      */
     void setMod(ModelDTO modelDTO) throws SQLException;

}
