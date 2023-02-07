package com.lz.youtuan.controller;

import com.lz.youtuan.common.R;
import com.lz.youtuan.dto.OrdersDto;
import com.lz.youtuan.entity.Orders;
import com.lz.youtuan.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("用户下单");
    }

    @GetMapping("/page")
    public R<OrdersDto> page(int page, int pageSize, String number){
        return null;
    }

}
