package com.exam.mapper;

import com.exam.entity.SysRole;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysRoleMapper {

    List<SysRole> selectAll();

    SysRole selectById(@Param("id") Long id);

    SysRole selectByRoleCode(@Param("roleCode") String roleCode);

    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);

    List<SysRole> selectByIds(@Param("ids") List<Long> ids);

    int insert(SysRole sysRole);

    int updateById(SysRole sysRole);

    int logicDeleteById(@Param("id") Long id);
}
