package com.lz.youtuan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lz.youtuan.common.BaseContext;
import com.lz.youtuan.common.R;
import com.lz.youtuan.dto.OrdersDto;
import com.lz.youtuan.entity.Orders;
import com.lz.youtuan.entity.User;
import com.lz.youtuan.service.OrderService;
import com.lz.youtuan.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;
    /**
     * 用户下单
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("用户下单");
    }

    /**
     * 查询订单
     * @param page
     * @param pageSize
     * @param number
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number){

        //订单显示功能
        Page<Orders> pageInfo=new Page<>(page, pageSize);
        Page<OrdersDto> dtoInfo=new Page<>();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        //根据条件查询
        queryWrapper.like(number!=null,Orders::getNumber, number);
        orderService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(dtoInfo, pageInfo,"records");
        List<Orders> records = pageInfo.getRecords();

        List<OrdersDto> list=records.stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);

            //用户信息
            Long userId = item.getUserId();
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getId, userId);
            User user = userService.getOne(lambdaQueryWrapper);
            ordersDto.setUserName(user.getName());
            return ordersDto;
        }).collect(Collectors.toList());

        dtoInfo.setRecords(list);
        return R.success(dtoInfo);
    }

    @PutMapping
    public R<String> list(@RequestBody OrdersDto ordersDto){
        orderService.updateById(ordersDto);
        return R.success("修改成功");
    }
}
