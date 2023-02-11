package com.lz.youtuan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lz.youtuan.common.R;
import com.lz.youtuan.dto.DishDto;
import com.lz.youtuan.entity.Category;
import com.lz.youtuan.entity.Dish;
import com.lz.youtuan.entity.DishFlavor;
import com.lz.youtuan.service.CategoryService;
import com.lz.youtuan.service.DishFlavorService;
import com.lz.youtuan.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);

        //更新或者添加都需要清理Redis缓存，清理所有菜品信息
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);

        //精确清理
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //构建分页对象
        Page<Dish> pageInfo=new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage=new Page<>();

        //构建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        //执行分页查询查询
        queryWrapper.like(name!=null, Dish::getName ,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list=records.stream().map((item) ->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();  //分类id
            Category category = categoryService.getById(categoryId);  //根据id查询目标对象

            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询对应菜品信息和口味信息
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);

        //更新或者添加都需要清理Redis缓存，清理所有菜品信息
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);

        //精确清理
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        return R.success("修改菜品成功");
    }

    /**
     * 删除菜品 需要删除两个表
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.removeWithDish(ids);

        //删除需要清理Redis缓存，清理所有菜品信息 因为没有传CategoryId 无法精准删除
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        return R.success("删除成功");
    }

    /**
     * 修改status 停售或者起售
     * @param ids
     * @return
     */
    @PostMapping(value = {"/status/0"})
    public R<String> status0(Long[] ids){
        for(Long id:ids){
            dishService.updateStatusById0(id);
        }
        return R.success("修改状态成功");
    }

    @PostMapping( "/status/1")
    public R<String> status1(Long[] ids){
        for(Long id:ids){
            dishService.updateStatusById1(id);
        }
        return R.success("修改状态成功");
    }

    /**
     * 根据条件查询该菜品的数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId()!=null, Dish::getCategoryId, dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus, 1); //查询未停售的菜品
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }

    /**
     * 根据条件查询该菜品的数据  加入了口味数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList=null;

        //动态构造key
        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus(); //dish_49988258_1

        //先从Redis中缓存数据
        dishDtoList =(List<DishDto>) redisTemplate.opsForValue().get(key);

        if(dishDtoList!=null){
            //如果存在 直接返回 无需查询数据库/
            return R.success(dishDtoList);
        }

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1); //查询未停售的菜品
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList=list.stream().map((item) ->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();  //分类id
            Category category = categoryService.getById(categoryId);  //根据id查询目标对象

            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品id   查找口味表
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        //不存在则范围数据库 并将菜品信息缓存到Redis中
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }
}
