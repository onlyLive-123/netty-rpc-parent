package com.example.demo.rpc;

import com.alibaba.fastjson.JSONObject;
import com.exmaple.demo.protocol.constants.RpcRequest;
import com.exmaple.demo.protocol.constants.RpcResponse;
import com.exmaple.demo.protocol.handler.RpcHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class MyRpcHandler extends RpcHandler {


    /**
     * 协议 RpcRequest
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("RpcRequest receive msg：{}", msg);

        RpcRequest request = JSONObject.parseObject(msg, RpcRequest.class);
        if (request == null || request.getReqId() == null) return;

        String service = request.getService();
        Object bean = RpcBeanPostProcessor.beanMap.get(service);
        //根据方法名称和参数类型获取类中的方法
        Method method = bean.getClass().getMethod(request.getMethod(), request.getParamterType());
        Object result = method.invoke(bean, request.getArgs());

        //响应协议
        RpcResponse response = new RpcResponse();
        response.setReqId(request.getReqId());
        response.setContent(result);
        //写出
        ctx.writeAndFlush(JSONObject.toJSONString(response));
    }
}
