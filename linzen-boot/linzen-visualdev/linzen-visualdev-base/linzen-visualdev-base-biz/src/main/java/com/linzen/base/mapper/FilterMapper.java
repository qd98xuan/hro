package com.linzen.base.mapper;

import com.linzen.base.entity.FilterEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FilterMapper extends  SuperMapper<FilterEntity>  {
}