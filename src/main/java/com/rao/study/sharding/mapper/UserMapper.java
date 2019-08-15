package com.rao.study.sharding.mapper;

import com.rao.study.sharding.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void save(User user);
}
