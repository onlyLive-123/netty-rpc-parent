package com.exmaple.demo.protocol.constants;

import lombok.Data;

@Data
public class RpcRequest {

    public String reqId;
    public String service;
    public String method;
    public Object[] args;
    public Class[] paramterType;


}
