package com.linzen.base.model.dblink;

import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.source.impl.DbOracle;
import com.linzen.exception.DataBaseException;
import com.linzen.util.JsonUtil;
import com.linzen.util.JsonUtilEx;
import com.linzen.util.StringUtil;
import com.linzen.util.XSSEscape;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 页面显示对象
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DbLinkInfoVO extends DbLinkBaseForm {

    /**
     * 获取连接页面显示对象
     * @param entity 连接实体对象
     * @return 返回显示对象
     * @throws DataBaseException ignore
     */
    public DbLinkInfoVO getDbLinkInfoVO(DbLinkEntity entity) throws DataBaseException {
        DbLinkInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, DbLinkInfoVO.class);
        vo.setServiceName(XSSEscape.escape(entity.getDbName()));
        vo.setTableSpace(XSSEscape.escape(entity.getDbTableSpace()));
        vo.setOracleExtend(entity.getOracleExtend() != null && entity.getOracleExtend() == 1);
        if(StringUtil.isNotEmpty(entity.getOracleParam())){
            Map<String, Object> oracleParam = JsonUtil.stringToMap(entity.getOracleParam());
            if(oracleParam.size() > 0){
                vo.setOracleLinkType(oracleParam.get(DbOracle.ORACLE_LINK_TYPE).toString());
                vo.setOracleRole(oracleParam.get(DbOracle.ORACLE_ROLE).toString());
                vo.setOracleService(oracleParam.get(DbOracle.ORACLE_SERVICE).toString());
            }
        }
        return vo;
    }

    @Schema(description = "主键")
    private String id;

}
