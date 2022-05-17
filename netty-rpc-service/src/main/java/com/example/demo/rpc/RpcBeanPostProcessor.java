package com.example.demo.rpc;

import com.exmaple.demo.annotation.RpcService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
@Component
public class RpcBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    static Map<String, Object> beanMap = new ConcurrentHashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        if (clazz.isAnnotationPresent(RpcService.class)) {
            beanMap.put(clazz.getInterfaces()[0].getName(), bean);
            log.info("register rpc service：{}", clazz.getInterfaces()[0].getName());
        }
        return bean;
    }
}
