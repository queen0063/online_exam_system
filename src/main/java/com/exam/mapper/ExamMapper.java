package com.exam.mapper;

import com.exam.dto.exam.ExamQueryDTO;
import com.exam.entity.Exam;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ExamMapper {

    Exam selectById(@Param("id") Long id);

    List<Exam> selectPage(@Param("query") ExamQueryDTO queryDTO, @Param("creatorId") Long creatorId, @Param("admin") boolean admin);

    Long selectCount(@Param("query") ExamQueryDTO queryDTO, @Param("creatorId") Long creatorId, @Param("admin") boolean admin);

    List<Exam> selectStudentExamPage(@Param("studentId") Long studentId, @Param("status") String status, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    Long selectStudentExamCount(@Param("studentId") Long studentId, @Param("status") String status);

    int insert(Exam exam);

    int updateById(Exam exam);

    int logicDeleteById(@Param("id") Long id);
}
