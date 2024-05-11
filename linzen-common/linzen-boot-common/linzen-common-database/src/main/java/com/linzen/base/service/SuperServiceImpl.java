package com.linzen.base.service;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.linzen.base.mapper.SuperMapper;
import com.linzen.util.DateUtil;
import com.linzen.util.UserProvider;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Collection;
import java.util.Date;

public abstract class SuperServiceImpl<M extends SuperMapper<T>, T> extends MPJBaseServiceImpl<M, T> implements SuperService<T> {

    private final ConversionService conversionService = DefaultConversionService.getSharedInstance();

    /**
     * 删除人的字段名
     */
    public static final String DELETE_USER_ID = "deleteUserId";

    /**
     * 删除时间的字段名
     */
    public static final String DELETE_TIME = "deleteTime";

    /**
     * 填充删除用户与删除时间
     *
     * @param entity 实体
     * @return
     */
    @Override
    public boolean removeById(T entity) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(getEntityClass());
        if (tableInfo.isWithLogicDelete() && tableInfo.isWithUpdateFill()) {
            if (tableInfo.getPropertyValue(entity, DELETE_USER_ID) == null) {
                String userId = UserProvider.getLoginUserId();
                if (userId != null) {
                    Date deleteTime = DateUtil.getNowDate();
                    tableInfo.setPropertyValue(entity, DELETE_USER_ID, userId);
                    tableInfo.setPropertyValue(entity, DELETE_TIME, deleteTime);
                }
            }
        }
        return super.removeById(entity);
    }

    /**
     * 填充删除用户与删除时间
     *
     * @param list      主键ID或实体列表
     * @param batchSize 批次大小
     * @param useFill   是否启用填充(为true的情况,会将入参转换实体进行delete删除)
     * @return
     */
    @Override
    public boolean removeBatchByIds(Collection<?> list, int batchSize, boolean useFill) {
        String sqlStatement = getSqlStatement(SqlMethod.DELETE_BY_ID);
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);

        return executeBatch(list, batchSize, (sqlSession, e) -> {
            if (useFill && tableInfo.isWithLogicDelete()) {
                if (entityClass.isAssignableFrom(e.getClass())) {
                    sqlSession.update(sqlStatement, e);
                } else {
                    T instance = tableInfo.newInstance();
                    Object value = tableInfo.getKeyType() != e.getClass() ? conversionService.convert(e, tableInfo.getKeyType()) : e;
                    tableInfo.setPropertyValue(instance, tableInfo.getKeyProperty(), value);
                    if (tableInfo.getPropertyValue(instance, DELETE_USER_ID) == null) {
                        String userId = UserProvider.getLoginUserId();
                        if (userId != null) {
                            Date deleteTime = DateUtil.getNowDate();
                            tableInfo.setPropertyValue(instance, DELETE_USER_ID, userId);
                            tableInfo.setPropertyValue(instance, DELETE_TIME, deleteTime);
                        }
                    }
                    sqlSession.update(sqlStatement, instance);
                }
            } else {
                sqlSession.update(sqlStatement, e);
            }
        });
    }


}
