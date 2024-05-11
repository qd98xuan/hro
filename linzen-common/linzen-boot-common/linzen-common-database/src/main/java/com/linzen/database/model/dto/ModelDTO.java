package com.linzen.database.model.dto;

import com.linzen.database.source.DbBase;
import lombok.Data;

import java.sql.ResultSet;

/**
 * 自定义模板参数对象
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ModelDTO {

    public ModelDTO(ResultSet resultSet, String dbEncode){
        this.resultSet = resultSet;
        this.dbEncode = dbEncode;
    }

    public ModelDTO(ResultSet resultSet, DbBase dbBase){
        this.resultSet = resultSet;
    }

    /**
     * 结果集
     */
    private ResultSet resultSet;

    /**
     * 数据基类
     */
    private String dbEncode;

}
