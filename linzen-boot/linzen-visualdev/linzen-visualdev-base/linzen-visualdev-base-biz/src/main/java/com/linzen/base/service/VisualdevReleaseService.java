package com.linzen.base.service;


import com.linzen.base.entity.VisualdevReleaseEntity;

import java.util.List;


/**
 *
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
public interface VisualdevReleaseService extends SuperService<VisualdevReleaseEntity> {

    long beenReleased(String id);

    List<VisualdevReleaseEntity> selectorList();

}

