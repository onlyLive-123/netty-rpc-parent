package com.example.demo;

import com.example.demo.rpc.MyRpcHandler;
import com.exmaple.demo.protocol.core.NettyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class RpcServiceApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RpcServiceApplication.class, args);
    }

    @Value("${server.rpcPort}")
    int port;

    @Override
    public void run(String... args) throws Exception {
        NettyService.start(port, new MyRpcHandler());
    }
}
