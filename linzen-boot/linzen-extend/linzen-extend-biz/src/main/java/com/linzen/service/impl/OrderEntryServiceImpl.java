package com.linzen.service.impl;

import com.linzen.base.service.SuperServiceImpl;
import com.linzen.entity.OrderEntryEntity;
import com.linzen.mapper.OrderEntryMapper;
import com.linzen.service.OrderEntryService;
import org.springframework.stereotype.Service;

/**
 * 订单明细
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class OrderEntryServiceImpl extends SuperServiceImpl<OrderEntryMapper, OrderEntryEntity> implements OrderEntryService {

}
