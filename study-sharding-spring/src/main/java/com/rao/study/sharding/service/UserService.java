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
            user.setUserId(Long.valueOf(i+1));//如果使用user_id作为分配策略字段,则这里必须要传递值,sharding才能根据这个值,再根据配置的分片策略算法进行数据分发
            user.setUserName("test_"+i);
            userMapper.save(user);
        }

    }

}
