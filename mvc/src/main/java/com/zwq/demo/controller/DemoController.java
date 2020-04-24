package com.zwq.demo.controller;

import com.zwq.mvcframework.annotation.MyAutowired;
import com.zwq.mvcframework.annotation.MyController;
import com.zwq.mvcframework.annotation.MyRequestMapping;
import com.zwq.demo.service.IDemoService;
import com.zwq.mvcframework.annotation.MySecurity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@MyController
@MyRequestMapping("/demo")
@MySecurity({"admin,user"})
public class DemoController {

    @MyAutowired
    public IDemoService demoService;

    @MyRequestMapping("/query")
    public String handler(HttpServletRequest request, HttpServletResponse response,String username){
        return demoService.getName(username);
    }

    @MyRequestMapping("/test")
    public void handle01(HttpServletRequest request, HttpServletResponse response,String username) throws IOException {
        response.getWriter().write(username);
    }
}
