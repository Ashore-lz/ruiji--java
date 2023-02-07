package com.lz.youtuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lz.youtuan.entity.Dish;
import com.lz.youtuan.entity.DishFlavor;
import com.lz.youtuan.mapper.DishFlavorMapper;
import com.lz.youtuan.mapper.DishMapper;
import com.lz.youtuan.service.DishFlavorService;
import com.lz.youtuan.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
