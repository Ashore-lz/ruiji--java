package com.lz.youtuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lz.youtuan.entity.OrderDetail;
import com.lz.youtuan.mapper.OrderDetailMapper;
import com.lz.youtuan.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}
