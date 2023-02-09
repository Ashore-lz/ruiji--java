package com.lz.youtuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lz.youtuan.common.CustomException;
import com.lz.youtuan.dto.DishDto;
import com.lz.youtuan.entity.Dish;
import com.lz.youtuan.entity.DishFlavor;
import com.lz.youtuan.entity.Setmeal;
import com.lz.youtuan.entity.SetmealDish;
import com.lz.youtuan.mapper.DishMapper;
import com.lz.youtuan.service.DishFlavorService;
import com.lz.youtuan.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品 同时保持对应口味数据
     * @param dishDto
     */
    @Transactional  //事务注解
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保持菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();  //菜品id

        List<DishFlavor> flavors = dishDto.getFlavors();  //菜品口味
        flavors.stream().map((item) ->{  //循环另外写法
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保持菜品口味数据到菜品口味表 dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询对应菜品信息和口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = this.getById(id);

        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        //查询当前菜品对应口味信息  从dish_flavor表中查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 更新菜品信息
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表
        this.updateById(dishDto);

        //清理当前菜品信息口味数据--dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来了的口味数据--dist_flavor表的插入操作
        List<DishFlavor> flavors = dishDto.getFlavors();  //菜品口味
        flavors.stream().map((item) ->{  //循环另外写法
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id修改状态
     * @param id
     */
    @Override
    public void updateStatusById0(Long id) {
        Dish dish = this.getById(id);
        if(dish.getStatus()==1){
            dish.setStatus(0);
        }
        this.updateById(dish);
    }

    @Override
    public void updateStatusById1(Long id) {
        Dish dish = this.getById(id);
        if(dish.getStatus()==0){
            dish.setStatus(1);
        }
        this.updateById(dish);
    }

    @Override
    public void removeWithDish(List<Long> ids) {
        //查询菜品状态  确认是否可以删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, 1);
        int count = this.count(queryWrapper);
        if(count>0){
            throw new CustomException("菜品正在售卖，不能删除");
        }

        //可以删除 先删除dish
        this.removeByIds(ids);

        //删除关系表中数据  dish_flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(DishFlavor::getDishId, ids);
        //删除关系表中的数据
        dishFlavorService.remove(queryWrapper1);
    }

}
