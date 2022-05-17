package com.example.demo.rpc;

import com.exmaple.demo.protocol.constants.RpcRequest;
import com.exmaple.demo.protocol.core.NettyClient;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 动态代理回调类
 */
public class ProxyHandler implements InvocationHandler, Serializable {

    String rpcHost;
    int rpcPort;
    Object target;
    String service;

    public ProxyHandler(Object target, String service, String rpcHost, int rpcPort) {
        this.target = target;
        this.service = service;
        this.rpcHost = rpcHost;
        this.rpcPort = rpcPort;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //组装协议
        RpcRequest request = new RpcRequest();
        request.setReqId(UUID.randomUUID().toString());
        request.setService(this.service);
        request.setMethod(method.getName());
        request.setParamterType(method.getParameterTypes());
        request.setArgs(args);

        //发起服务调用
        NettyClient nettyClient = new NettyClient();
        nettyClient.start(rpcHost, rpcPort, new MyRpcClientHandler());
        //返回结果
        return nettyClient.sendRequest(request);
    }


}
