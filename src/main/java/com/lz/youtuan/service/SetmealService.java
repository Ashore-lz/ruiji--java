package com.lz.youtuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lz.youtuan.dto.DishDto;
import com.lz.youtuan.dto.SetmealDto;
import com.lz.youtuan.entity.Dish;
import com.lz.youtuan.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐 还需报错套餐和菜品关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐 同时删除相关套餐和菜品
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

    /**
     * 修改起售或者停售状态
     * @param id
     */
    public void updateStatusById0(Long id);

    public void updateStatusById1(Long id);

    /**
     * 根据id查找套餐信息和菜品信息
     * @param id
     * @return
     */
    public SetmealDto getByIdWithDish(Long id);

    /**
     * 更新套餐信息
     * @param setmealDto
     */
    public void updateWithDish(SetmealDto setmealDto);
}
