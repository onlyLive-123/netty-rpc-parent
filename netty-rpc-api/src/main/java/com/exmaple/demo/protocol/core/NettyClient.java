package com.exmaple.demo.protocol.core;

import com.alibaba.fastjson.JSONObject;
import com.exmaple.demo.protocol.constants.RpcRequest;
import com.exmaple.demo.protocol.constants.RpcResponse;
import com.exmaple.demo.protocol.handler.RpcHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultPromise;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class NettyClient {

    public Channel channel;

    public void start(String host, int port, RpcHandler rpcHandler) {
        String mapKey = "/" + host + ":" + port;
        if (NettyConstans.clientMap.containsKey(mapKey)) {
            this.channel = NettyConstans.clientMap.get(mapKey);
            return;
        }
        NioEventLoopGroup b1 = new NioEventLoopGroup();
        Bootstrap bs = new Bootstrap()
                .group(b1)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(rpcHandler);
                    }
                });
        try {
            ChannelFuture future = bs.connect(host, port).sync();
            future.addListener(listen -> {
                if (listen.isSuccess()) {
                    log.info("connect rpc service success,{}:{}", host, port);
                }
            });
            channel = future.channel();
            NettyConstans.clientMap.put(mapKey, channel);
//            channel.closeFuture().sync();
        } catch (Exception e) {
            b1.shutdownGracefully();
            log.error("connect rpc service error,{}:{}", host, port);
        }
    }


    public Object sendRequest(RpcRequest rpcRequest) throws Exception {
        //自定义一个返回结果的回调 保存到Map中
        RpcFuture<RpcResponse> rpcFuture = new RpcFuture<>(
                new DefaultPromise<RpcResponse>(new DefaultEventLoop()));
        NettyConstans.rpcFutureMap.put(rpcRequest.getReqId(), rpcFuture);

        //消息发送
        channel.writeAndFlush(JSONObject.toJSONString(rpcRequest));

        //实际上为阻塞等待回调 由接收消息那里回调
        return rpcFuture.getPromise().get().getContent();
    }


}
