package com.exam.service;

import com.exam.common.result.PageResult;
import com.exam.dto.question.QuestionQueryDTO;
import com.exam.dto.question.QuestionSaveDTO;
import com.exam.vo.question.QuestionVO;
import java.util.List;

public interface QuestionService {

    PageResult<QuestionVO> page(QuestionQueryDTO queryDTO);

    QuestionVO detail(Long id);

    void save(QuestionSaveDTO questionSaveDTO);

    void remove(Long id);

    List<QuestionVO> wrongQuestions(Long studentId);
}
