package com.linzen.base.service;

import com.linzen.base.ServiceResult;
import com.linzen.base.Pagination;
import com.linzen.base.entity.BillRuleEntity;
import com.linzen.base.model.billrule.BillRulePagination;
import com.linzen.exception.DataBaseException;

import java.util.List;

/**
 * 单据规则
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface BillRuleService extends SuperService<BillRuleEntity> {

    /**
     * 列表
     *
     * @param pagination 条件
     * @return 单据规则列表
     */
    List<BillRuleEntity> getList(BillRulePagination pagination);

    /**
     * 列表
     *
     * @return 单据规则集合
     */
    List<BillRuleEntity> getList();

    /**
     * 信息
     *
     * @param id 主键值
     * @return 单据规则
     */
    BillRuleEntity getInfo(String id);

    /**
     * 验证名称
     *
     * @param fullName 名称
     * @param id       主键值
     * @return ignore
     */
    boolean isExistByFullName(String fullName, String id);

    /**
     * 验证编码
     *
     * @param enCode 编码
     * @param id     主键值
     * @return ignore
     */
    boolean isExistByEnCode(String enCode, String id);

    /**
     * 获取流水号
     *
     * @param enCode 流水编码
     * @return ignore
     * @throws DataBaseException ignore
     */
    String getNumber(String enCode) throws DataBaseException;

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(BillRuleEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return ignore
     */
    boolean update(String id, BillRuleEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(BillRuleEntity entity);

    /**
     * 上移
     *
     * @param id 主键值
     * @return ignore
     */
    boolean first(String id);

    /**
     * 下移
     *
     * @param id 主键值
     * @return ignore
     */
    boolean next(String id);

    /**
     * 获取单据流水号
     *
     * @param enCode  流水编码
     * @param isCache 是否缓存：每个用户会自动占用一个流水号，这个刷新页面也不会跳号
     * @return ignore
     * @throws DataBaseException ignore
     */
    String getBillNumber(String enCode, boolean isCache) throws DataBaseException;

    /**
     * 使用单据流水号（注意：必须是缓存的单据才可以调用这个方法，否则无效）
     *
     * @param enCode 流水编码
     */
    void useBillNumber(String enCode);

    /**
     * 单据规则导入
     *
     * @param entity 实体对象
     * @param type
     * @return ignore
     * @throws DataBaseException ignore
     */
    ServiceResult ImportData(BillRuleEntity entity, Integer type) throws DataBaseException;


    /**
     *
     *
     * @param pagination 根据业务条件
     * @return  单据规则列表
     */
    List<BillRuleEntity> getListByCategory(String id,Pagination pagination);

}
