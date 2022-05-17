package com.exmaple.demo.protocol.core;

import io.netty.util.concurrent.Promise;
import lombok.Data;

@Data
public class RpcFuture<T> {

    Promise<T> promise;

    public RpcFuture(Promise<T> promise) {
        this.promise = promise;
    }
}
