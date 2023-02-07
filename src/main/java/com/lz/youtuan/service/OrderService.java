package com.lz.youtuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lz.youtuan.entity.Orders;
import com.lz.youtuan.entity.User;

public interface OrderService extends IService<Orders> {
    //用户下单
    public void submit(Orders orders);
}
