package com.exam.mapper;

import com.exam.dto.user.UserQueryDTO;
import com.exam.entity.SysUser;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper {

    SysUser selectById(@Param("id") Long id);

    SysUser selectByUsername(@Param("username") String username);

    List<SysUser> selectPage(UserQueryDTO queryDTO);

    Long selectCount(UserQueryDTO queryDTO);

    int insert(SysUser sysUser);

    int updateById(SysUser sysUser);

    int logicDeleteById(@Param("id") Long id);

    List<SysUser> selectByIds(@Param("ids") List<Long> ids);
}
