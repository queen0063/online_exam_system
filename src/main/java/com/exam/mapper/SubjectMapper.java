package com.exam.mapper;

import com.exam.entity.Subject;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SubjectMapper {

    List<Subject> selectAll();

    Subject selectById(@Param("id") Long id);

    Subject selectByCode(@Param("subjectCode") String subjectCode);

    int insert(Subject subject);

    int updateById(Subject subject);

    int logicDeleteById(@Param("id") Long id);
}
