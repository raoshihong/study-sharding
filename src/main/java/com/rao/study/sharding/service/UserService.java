package com.rao.study.sharding.service;

import com.rao.study.sharding.mapper.UserMapper;
import com.rao.study.sharding.model.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;


    public void test(){
        for (int i=0;i<10;i++){
            User user = new User();
            user.setUserName("test_"+i);
            userMapper.save(user);


        }

    }

}
