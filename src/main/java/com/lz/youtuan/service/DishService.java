package com.lz.youtuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lz.youtuan.dto.DishDto;
import com.lz.youtuan.entity.Category;
import com.lz.youtuan.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品  同时插入口味数据 需要两张表 dish dishflavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息  同时更新对应口味信息
    public void updateWithFlavor(DishDto dishDto);

    //根据id修改状态 Status=0
    public void updateStatusById0(Long id);

    //根据id修改状态 Status=0
    public void updateStatusById1(Long id);

    //删除菜品
    public void removeWithDish(List<Long> ids);
}
