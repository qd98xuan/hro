package com.linzen.base.service;

import com.linzen.base.entity.ProvinceAtlasEntity;

import java.util.List;

/**
 * 行政区划
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface ProvinceAtlasService extends SuperService<ProvinceAtlasEntity> {

    List<ProvinceAtlasEntity> getList();

    List<ProvinceAtlasEntity> getListByPid(String pid);

    ProvinceAtlasEntity findOneByCode(String code);
}
