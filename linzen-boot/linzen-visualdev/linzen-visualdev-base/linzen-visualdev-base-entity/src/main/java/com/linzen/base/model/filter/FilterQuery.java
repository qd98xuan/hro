package com.linzen.base.model.filter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.entity.FilterEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class FilterQuery extends Page<FilterEntity> {

}
