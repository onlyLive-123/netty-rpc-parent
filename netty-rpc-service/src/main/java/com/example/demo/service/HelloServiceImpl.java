package com.example.demo.service;

import com.exmaple.demo.annotation.RpcService;
import com.exmaple.demo.api.HellService;
import com.exmaple.demo.api.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RpcService
@Component
public class HelloServiceImpl implements HellService {


    @Autowired
    OrderService orderService;

    @Override
    public String hello(String name) {
        return "hello my name is " + name;
    }

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
