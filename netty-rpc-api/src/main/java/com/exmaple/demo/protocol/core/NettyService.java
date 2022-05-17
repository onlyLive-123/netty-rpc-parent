package com.exmaple.demo.protocol.core;

import com.exmaple.demo.protocol.handler.RpcHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyService {

    public static void start(int port, RpcHandler rpcHandler) {
        new Thread(() -> {
            NioEventLoopGroup b1 = new NioEventLoopGroup();
            NioEventLoopGroup w1 = new NioEventLoopGroup();
            ServerBootstrap bs = new ServerBootstrap()
                    .group(b1, w1)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new StringEncoder());
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(rpcHandler);
                        }
                    });
            ChannelFuture future = bs.bind(port).addListener(listen -> {
                if (listen.isSuccess()) {
                    log.info("start remote service：" + port);
                }
            });

            try {
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                b1.shutdownGracefully();
                w1.shutdownGracefully();
            }
        }).start();
    }


}
