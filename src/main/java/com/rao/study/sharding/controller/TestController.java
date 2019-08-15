package com.rao.study.sharding.controller;

import com.rao.study.sharding.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.sql.DataSource;

@RestController
public class TestController {

    @Resource
    private UserService userService;

    @GetMapping("/test")
    public void test(){
        userService.test();
    }

}
