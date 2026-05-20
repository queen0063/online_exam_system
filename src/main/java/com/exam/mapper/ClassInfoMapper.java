package com.exam.mapper;

import com.exam.entity.ClassInfo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ClassInfoMapper {

    List<ClassInfo> selectAll();

    List<ClassInfo> selectVisible(@Param("teacherId") Long teacherId, @Param("admin") boolean admin);
}
