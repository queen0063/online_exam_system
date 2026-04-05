package com.exam.mapper;

import com.exam.entity.StudentAnswer;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentAnswerMapper {

    List<StudentAnswer> selectByExamIdAndStudentId(@Param("examId") Long examId, @Param("studentId") Long studentId);

    StudentAnswer selectByExamIdAndStudentIdAndQuestionId(
            @Param("examId") Long examId, @Param("studentId") Long studentId, @Param("questionId") Long questionId);

    List<StudentAnswer> selectPendingByTeacherId(@Param("teacherId") Long teacherId, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    Long countPendingByTeacherId(@Param("teacherId") Long teacherId);

    int insert(StudentAnswer studentAnswer);

    int updateById(StudentAnswer studentAnswer);

    int deleteByExamIdAndStudentId(@Param("examId") Long examId, @Param("studentId") Long studentId);
}
