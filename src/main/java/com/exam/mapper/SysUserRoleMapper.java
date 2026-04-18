package com.exam.mapper;

import com.exam.entity.SysUserRole;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserRoleMapper {

    int batchInsert(@Param("list") List<SysUserRole> list);

    int deleteByUserId(@Param("userId") Long userId);

    int deleteByRoleId(@Param("roleId") Long roleId);
}
