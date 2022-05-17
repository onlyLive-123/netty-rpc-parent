package com.example.demo.rpc;

import com.alibaba.fastjson.JSONObject;
import com.exmaple.demo.protocol.constants.RpcResponse;
import com.exmaple.demo.protocol.core.NettyConstans;
import com.exmaple.demo.protocol.handler.RpcHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyRpcClientHandler extends RpcHandler {


    /**
     * 协议 RpcResponse
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("RpcResponse receive msg：{}", msg);
        RpcResponse response = JSONObject.parseObject(msg, RpcResponse.class);
        if (response == null || !NettyConstans.rpcFutureMap.containsKey(response.getReqId())) return;
        //给指定的ReqId回调
        NettyConstans.rpcFutureMap.get(response.getReqId()).getPromise().setSuccess(response);
        NettyConstans.rpcFutureMap.remove(response.getReqId());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("连接出现异常,重置连接:{}", ctx.channel().remoteAddress());
        NettyConstans.clientMap.remove(ctx.channel().remoteAddress().toString());
    }


}
