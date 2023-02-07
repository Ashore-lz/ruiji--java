package com.lz.youtuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lz.youtuan.entity.Orders;
import com.lz.youtuan.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
