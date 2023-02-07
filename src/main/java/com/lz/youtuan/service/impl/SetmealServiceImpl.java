package com.lz.youtuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lz.youtuan.common.CustomException;
import com.lz.youtuan.dto.DishDto;
import com.lz.youtuan.dto.SetmealDto;
import com.lz.youtuan.entity.Dish;
import com.lz.youtuan.entity.DishFlavor;
import com.lz.youtuan.entity.Setmeal;
import com.lz.youtuan.entity.SetmealDish;
import com.lz.youtuan.mapper.DishMapper;
import com.lz.youtuan.mapper.SetmealMapper;
import com.lz.youtuan.service.DishService;
import com.lz.youtuan.service.SetmealDishService;
import com.lz.youtuan.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐 还需报错套餐和菜品关联关系
     * @param setmealDto
     */
    @Transactional  //操作两张表
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) ->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品关系
        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 删除套餐 同时删除相关套餐和菜品
     * @param ids
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态  确认是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(queryWrapper);
        if(count>0){
            throw new CustomException("套餐正在售卖，不能删除");
        }

        //可以删除 先删除setmeal
        this.removeByIds(ids);

        //删除关系表中数据  setmeal_dish
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId, ids);
        //删除关系表中的数据
        setmealDishService.remove(queryWrapper1);
    }

    /**
     * 根据id查找套餐信息
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        //查询套餐基本信息
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        //查询当前套餐对应菜品信息  从setmeal_dish表中查询
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    /**
     * 更新套餐信息
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //更新setmeal表
        this.updateById(setmealDto);

        //清理当前套餐信息菜品数据--setmeal_dish表的delete操作
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        //添加当前提交过来了的菜品数据--setmeal_dish表的插入操作
        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();//菜品口味
        setmealDishList.stream().map((item) ->{  //循环另外写法
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishList);
    }

    /**
     * 根据id修改状态
     * @param id
     */
    @Override
    public void updateStatusById0(Long id) {
        Setmeal setmeal = this.getById(id);
        if(setmeal.getStatus()==1){
            setmeal.setStatus(0);
        }
        this.updateById(setmeal);
    }

    @Override
    public void updateStatusById1(Long id) {
        Setmeal setmeal = this.getById(id);
        if(setmeal.getStatus()==0){
            setmeal.setStatus(1);
        }
        this.updateById(setmeal);
    }

}
