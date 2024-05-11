package com.linzen.base.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.FilterEntity;
import com.linzen.base.model.filter.FilterInfo;
import com.linzen.base.model.filter.FilterQuery;
import com.linzen.base.service.FilterService;
import com.linzen.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/filter")
public class  FilterController {
    @Autowired
    private FilterService filterService;
    /**
     * 获取列表
     * @param page
     * @return
     */
    @PostMapping("list")
    public ServiceResult<?> list(@RequestBody @Validated FilterQuery page) {
        QueryWrapper<FilterEntity> wrapper = new QueryWrapper<>();


        FilterQuery info = filterService.page(page, wrapper);

        return ServiceResult.success(info);
    }

    /**
     * 查询信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ServiceResult<?> info(@PathVariable String id) {
        FilterEntity info = filterService.getById(id);
        return ServiceResult.success(info);
    }
    /**
     * 保存信息
     * @param info
     * @return
     */
    @PostMapping("save")
    public ServiceResult<?> save(@RequestBody @Validated FilterInfo info) {
        FilterEntity filterEntity = BeanUtil.copyProperties(info, FilterEntity.class);
        filterEntity.setId(RandomUtil.uuId());
        filterService.save(filterEntity);
        return ServiceResult.success("保存成功");
    }
    /**
     * 更新信息
     * @param info
     * @return
     */
    @PutMapping("update")
    public ServiceResult<?> update(@RequestBody @Validated FilterInfo info) {

        FilterEntity filterEntity = BeanUtil.copyProperties(info, FilterEntity.class);
        filterService.updateById(filterEntity);
        return ServiceResult.success("更新成功");
    }
    /**
     * 删除信息
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ServiceResult<?> delete(@PathVariable String id) {
        filterService.removeById(id);
        return ServiceResult.success("删除成功");
    }

}
