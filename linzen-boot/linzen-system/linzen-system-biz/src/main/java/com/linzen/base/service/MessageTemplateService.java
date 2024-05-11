package com.linzen.base.service;

import com.linzen.base.Pagination;
import com.linzen.base.entity.MessageTemplateEntity;

import java.util.List;

/**
 * 消息模板表
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface MessageTemplateService extends SuperService<MessageTemplateEntity> {

    /**
     * 列表（无分页）
     *
     * @return
     */
    List<MessageTemplateEntity> getList();

    /**
     * 列表
     *
     * @param pagination 条件
     * @return 单据规则列表
     */
    List<MessageTemplateEntity> getList(Pagination pagination, Boolean filter);

    /**
     * 信息
     *
     * @param id 主键值
     * @return 单据规则
     */
    MessageTemplateEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体
     */
    void create(MessageTemplateEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return ignore
     */
    boolean update(String id, MessageTemplateEntity entity);

    /**
     * 删除
     *
     * @param entity 实体
     */
    void delete(MessageTemplateEntity entity);

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
     * @param id       主键值
     * @return ignore
     */
    boolean isExistByEnCode(String enCode, String id);
}
