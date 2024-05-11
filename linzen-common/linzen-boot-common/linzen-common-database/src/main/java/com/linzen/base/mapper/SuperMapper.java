package com.linzen.base.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.linzen.database.util.IgnoreLogicDeleteHolder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.ResultHandler;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SuperMapper<T> extends MPJBaseMapper<T> {

    /**
     * 调用此方法后 后续SQL操作忽略逻辑删除筛选
     * 调用完成后需要调用clearIgnoreLogicDelete 清除标记
     * @return
     */
    default SuperMapper<T> setIgnoreLogicDelete(){
        IgnoreLogicDeleteHolder.setIgnoreLogicDelete();
        return this;
    }

    /**
     * 调用此方法后 后续SQL恢复逻辑删除筛选
     * @return
     */
    default SuperMapper<T> clearIgnoreLogicDelete(){
        IgnoreLogicDeleteHolder.clear();
        return this;
    }


    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    int deleteByIdIlg(Serializable id);

    /**
     * 根据实体(ID)删除
     *
     * @param entity 实体对象
     * @since 3.4.4
     */
    int deleteByIdIlg(T entity);


    /**
     * 根据 entity 条件，删除记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     */
    int deleteIlg(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 删除（根据ID或实体 批量删除）
     *
     * @param idList 主键ID列表或实体列表(不能为 null 以及 empty)
     */
    int deleteBatchIdsIlg(@Param(Constants.COLL) Collection<?> idList);

    /**
     * 根据 ID 修改
     *
     * @param entity 实体对象
     */
    int updateByIdIlg(@Param(Constants.ENTITY) T entity);

    /**
     * 根据 whereEntity 条件，更新记录
     *
     * @param entity        实体对象 (set 条件值,可以为 null)
     * @param updateWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     */
    int updateIlg(@Param(Constants.ENTITY) T entity, @Param(Constants.WRAPPER) Wrapper<T> updateWrapper);

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     */
    T selectByIdIlg(Serializable id);

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList 主键ID列表(不能为 null 以及 empty)
     */
    List<T> selectBatchIdsIlg(@Param(Constants.COLL) Collection<? extends Serializable> idList);

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList        idList 主键ID列表(不能为 null 以及 empty)
     * @param resultHandler resultHandler 结果处理器 {@link ResultHandler}
     * @since 3.5.4
     */
    void selectBatchIdsIlg(@Param(Constants.COLL) Collection<? extends Serializable> idList, ResultHandler<T> resultHandler);

    /**
     * 根据 Wrapper 条件，查询总记录数
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    Long selectCountIlg(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 entity 条件，查询全部记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    List<T> selectListIlg(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 entity 条件，查询全部记录
     *
     * @param queryWrapper  实体对象封装操作类（可以为 null）
     * @param resultHandler 结果处理器 {@link ResultHandler}
     * @since 3.5.4
     */
    void selectListIlg(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper, ResultHandler<T> resultHandler);

    /**
     * 根据 entity 条件，查询全部记录（并翻页）
     *
     * @param page         分页查询条件
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     * @since 3.5.3.2
     */
    List<T> selectListIlg(IPage<T> page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 entity 条件，查询全部记录（并翻页）
     * @param page          分页查询条件
     * @param queryWrapper  实体对象封装操作类（可以为 null）
     * @param resultHandler 结果处理器 {@link ResultHandler}
     * @since 3.5.4
     */
    void selectListIlg(IPage<T> page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper, ResultHandler<T> resultHandler);


    /**
     * 根据 Wrapper 条件，查询全部记录
     *
     * @param queryWrapper 实体对象封装操作类
     */
    List<Map<String, Object>> selectMapsIlg(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 Wrapper 条件，查询全部记录
     *
     * @param queryWrapper  实体对象封装操作类
     * @param resultHandler 结果处理器 {@link ResultHandler}
     * @since 3.5.4
     */
    void selectMapsIlg(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper, ResultHandler<Map<String, Object>> resultHandler);

    /**
     * 根据 Wrapper 条件，查询全部记录（并翻页）
     *
     * @param page         分页查询条件
     * @param queryWrapper 实体对象封装操作类
     * @since 3.5.3.2
     */
    List<Map<String, Object>> selectMapsIlg(IPage<? extends Map<String, Object>> page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 Wrapper 条件，查询全部记录（并翻页）
     *
     * @param page          分页查询条件
     * @param queryWrapper  实体对象封装操作类
     * @param resultHandler 结果处理器 {@link ResultHandler}
     * @since 3.5.4
     */
    void selectMapsIlg(IPage<? extends Map<String, Object>> page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper, ResultHandler<Map<String, Object>> resultHandler);

    /**
     * 根据 Wrapper 条件，查询全部记录
     * <p>注意： 只返回第一个字段的值</p>
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    <E> List<E> selectObjsIlg(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 Wrapper 条件，查询全部记录
     * <p>注意： 只返回第一个字段的值</p>
     *
     * @param queryWrapper  实体对象封装操作类（可以为 null）
     * @param resultHandler 结果处理器 {@link ResultHandler}
     * @since 3.5.4
     */
    <E> void selectObjsIlg(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper, ResultHandler<E> resultHandler);


}
