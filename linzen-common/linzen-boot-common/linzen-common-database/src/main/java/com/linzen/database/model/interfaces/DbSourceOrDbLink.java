package com.linzen.database.model.interfaces;


import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.sql.model.DbStruct;

/**
 * 数据源接口
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface DbSourceOrDbLink {

    DbStruct getDbStruct();

    DbLinkEntity init();

    DbLinkEntity init(String dbName);

}
