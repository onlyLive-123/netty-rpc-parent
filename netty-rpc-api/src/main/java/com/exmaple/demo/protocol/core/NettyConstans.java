package com.exmaple.demo.protocol.core;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyConstans {

    public static Map<String, RpcFuture> rpcFutureMap = new ConcurrentHashMap<>();
    public static Map<String, Channel> clientMap = new ConcurrentHashMap<>();

}
