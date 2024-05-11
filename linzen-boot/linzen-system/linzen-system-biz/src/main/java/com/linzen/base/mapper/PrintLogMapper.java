package com.linzen.base.mapper;

import com.linzen.base.entity.PrintLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PrintLogMapper extends SuperMapper<PrintLogEntity> {
}