package com.exam.service;

import com.exam.common.result.PageResult;
import com.exam.dto.answer.AnswerSaveDTO;
import com.exam.dto.exam.ExamQueryDTO;
import com.exam.vo.answer.AnswerVO;
import com.exam.vo.exam.ExamVO;
import java.util.List;

public interface AnswerService {

    PageResult<ExamVO> myExamPage(ExamQueryDTO queryDTO);

    ExamVO examDetail(Long examId);

    void startExam(Long examId);

    void saveAnswers(Long examId, AnswerSaveDTO answerSaveDTO);

    void submit(Long examId);

    void reportSwitchCount(Long examId, Integer switchCount);

    List<AnswerVO> answerDetail(Long examId);
}
