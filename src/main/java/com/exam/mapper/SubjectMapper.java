package com.exam.mapper;

import com.exam.entity.Subject;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SubjectMapper {

    List<Subject> selectAll();
}
