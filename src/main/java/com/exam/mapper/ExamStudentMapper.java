package com.exam.mapper;

import com.exam.entity.ExamStudent;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ExamStudentMapper {

    List<ExamStudent> selectByExamId(@Param("examId") Long examId);

    ExamStudent selectByExamIdAndStudentId(@Param("examId") Long examId, @Param("studentId") Long studentId);

    int batchInsert(@Param("list") List<ExamStudent> list);

    int deleteByExamId(@Param("examId") Long examId);

    int updateAnswerStatus(@Param("examId") Long examId, @Param("studentId") Long studentId, @Param("answerStatus") String answerStatus);
}
