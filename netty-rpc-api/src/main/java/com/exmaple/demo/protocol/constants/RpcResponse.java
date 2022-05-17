package com.exmaple.demo.protocol.constants;

import lombok.Data;

@Data
public class RpcResponse {

    String reqId;
    Object content;
}
