package com.example.demo.service;

import com.exmaple.demo.annotation.RpcService;
import com.exmaple.demo.api.OrderService;
import org.springframework.stereotype.Service;

@RpcService
@Service
public class OrderServiceImpl implements OrderService {


    @Override
    public String getOrder(String orderId) {
        return "select order service by orderId: " + orderId;
    }
}
