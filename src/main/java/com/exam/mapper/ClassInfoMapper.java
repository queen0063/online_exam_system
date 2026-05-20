package com.exam.mapper;

import com.exam.entity.ClassInfo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ClassInfoMapper {

    List<ClassInfo> selectAll();

    List<ClassInfo> selectVisible(@Param("teacherId") Long teacherId, @Param("admin") boolean admin);

    ClassInfo selectById(@Param("id") Long id);

    ClassInfo selectByCode(@Param("classCode") String classCode);

    int insert(ClassInfo classInfo);

    int updateById(ClassInfo classInfo);

    int logicDeleteById(@Param("id") Long id);
}
