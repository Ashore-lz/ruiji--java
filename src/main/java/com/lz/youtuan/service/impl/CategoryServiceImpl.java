package com.lz.youtuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lz.youtuan.common.CustomException;
import com.lz.youtuan.entity.Category;
import com.lz.youtuan.entity.Dish;
import com.lz.youtuan.entity.Setmeal;
import com.lz.youtuan.mapper.CategoryMapper;
import com.lz.youtuan.service.CategoryService;
import com.lz.youtuan.service.DishService;
import com.lz.youtuan.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类  删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前菜品是否关联菜品 关联了抛出异常
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        //添加查询条件 根据id查询
        lambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(lambdaQueryWrapper);
        if(count>0){
            //关联菜品  抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        //查询当前菜品是否关联套餐 关联了抛出异常
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(Setmeal::getCategoryId, id);
        int count1 = setmealService.count(lambdaQueryWrapper1);
        if(count1>0){
            //关联套餐  抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        //正常删除
        super.removeById(id);
    }
}
