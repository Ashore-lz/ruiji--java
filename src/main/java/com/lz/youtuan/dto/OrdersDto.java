package com.lz.youtuan.dto;

import com.lz.youtuan.entity.OrderDetail;
import com.lz.youtuan.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
