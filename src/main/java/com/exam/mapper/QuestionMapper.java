package com.exam.mapper;

import com.exam.dto.question.QuestionQueryDTO;
import com.exam.entity.Question;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuestionMapper {

    Question selectById(@Param("id") Long id);

    List<Question> selectPage(QuestionQueryDTO queryDTO);

    Long selectCount(QuestionQueryDTO queryDTO);

    int insert(Question question);

    int updateById(Question question);

    int logicDeleteById(@Param("id") Long id);

    List<Question> selectByIds(@Param("ids") List<Long> ids);

    List<Question> selectByRule(
            @Param("subjectId") Long subjectId,
            @Param("questionType") String questionType,
            @Param("difficulty") String difficulty,
            @Param("knowledgePoint") String knowledgePoint,
            @Param("limitCount") Integer limitCount);

    List<Question> selectWrongQuestions(@Param("studentId") Long studentId);
}
