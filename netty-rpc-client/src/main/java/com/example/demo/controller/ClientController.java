package com.example.demo.controller;

import com.exmaple.demo.annotation.RpcReference;
import com.exmaple.demo.api.HellService;
import com.exmaple.demo.api.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {

    @RpcReference
    HellService hellService;
    @RpcReference
    OrderService orderService;

    @GetMapping("/hello")
    public String hello(@RequestParam String orderId) {
        return orderService.getOrder(orderId);
    }

    @GetMapping("/add")
    public int add(@RequestParam Integer a, @RequestParam Integer b) {
        return hellService.add(a, b);
    }
}
