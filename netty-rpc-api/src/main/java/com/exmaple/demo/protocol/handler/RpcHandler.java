package com.exmaple.demo.protocol.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public abstract class RpcHandler extends SimpleChannelInboundHandler<String> {

}
