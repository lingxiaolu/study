package com.zwq.demo.service.impl;

import com.zwq.mvcframework.annotation.MyService;
import com.zwq.demo.service.IDemoService;

@MyService
public class DemoServiceImpl implements IDemoService {
    @Override
    public String getName(String name) {
        System.out.println("service 传递过来的参数为"+name);
        return name;
    }
}
