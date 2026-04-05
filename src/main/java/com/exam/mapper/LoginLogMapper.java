package com.exam.mapper;

import com.exam.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginLogMapper {

    int insert(LoginLog loginLog);
}
