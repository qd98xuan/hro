package com.linzen.database.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.database.model.dto.PrepSqlDTO;
import com.linzen.database.util.DataSourceUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据连接
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("base_db_link")
@NoArgsConstructor
public class DbLinkEntity extends DataSourceUtil {
    /**
     * 连接主键
     */
    @TableId("f_id")
    private String id;
    /**
     * 连接图片
     */
    @TableField("f_image")
    private String image;

    /**
     * 连接名称
     */
    @TableField("f_full_name")
    private String fullName;

    /**
     * Oracle扩展开关
     */
    @TableField("f_oracle_extend")
    private Integer oracleExtend;

    public static DbLinkEntity newInstance(String dbLinkId){
        return PrepSqlDTO.DB_LINK_FUN.apply(dbLinkId);
    }

    public DbLinkEntity(String dbType){
        super.setDbType(dbType);
    }

}
