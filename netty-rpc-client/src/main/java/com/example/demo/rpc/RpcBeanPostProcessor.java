package com.example.demo.rpc;

import com.exmaple.demo.annotation.RpcReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
@Component
public class RpcBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryPostProcessor, EnvironmentAware {

    //该类为初始化类之后的回调 还没到注入阶段
    //因此在这里接收环境的回调
    Environment environment;
    //保存动态代理bean缓存
    static ConcurrentHashMap<String, Object> cacheProxyMap = new ConcurrentHashMap<>();

    //注册之前 设置坏境变量
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    //注册之后 依赖注入之前
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        for (String definitionName : beanFactory.getBeanDefinitionNames()) {
//            BeanDefinition definition = beanFactory.getBeanDefinition(definitionName);
//            String beanClassName = definition.getBeanClassName();
//            if (beanClassName == null) return;
//            try {
//                Class<?> clazz = Class.forName(beanClassName);
//                for (Field field : clazz.getDeclaredFields()) {
//                    if (field.isAnnotationPresent(RpcReference.class)) {
//                        log.info(field.getType().getName());
//                        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(field.getType());
//                        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
//                        beanDefinition.setBeanClass(RpcReferenceFactoryBean.class);
//
//                        beanDefinition.getPropertyValues().add("interfaces", field.getType().getName());
//                        ((BeanDefinitionRegistry) beanFactory).registerBeanDefinition(field.getName(), beanDefinition);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(RpcReference.class)) {
                Object instance;
                String beanClassName = field.getType().getName();
                try {
                    if (cacheProxyMap.containsKey(beanClassName)) {
                        instance = cacheProxyMap.get(beanClassName);
                    } else {
                        //根据不同的服务名称传递不同的rpc调用地址
                        RpcReference annotation = field.getAnnotation(RpcReference.class);
                        instance = Proxy.newProxyInstance(
                                field.getType().getClassLoader(),
                                new Class[]{field.getType()},
                                new ProxyHandler(bean, beanClassName,
                                        this.environment.getProperty(annotation.name() + ".rpcHost"),
                                        Integer.valueOf(this.environment.getProperty(annotation.name() + ".rpcPort"))));
                    }
                    log.info("create proxy bean:{}", beanClassName);
                    //反射注入
                    field.setAccessible(true);
                    field.set(bean, instance);
                    cacheProxyMap.put(field.getType().getName(), instance);
                } catch (IllegalAccessException e) {
                    log.error("create bean error,beanClassName {}", beanClassName);
                }
            }
        }
        return bean;
    }
}
