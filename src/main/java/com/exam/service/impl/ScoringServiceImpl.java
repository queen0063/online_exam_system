package com.exam.service.impl;

import com.exam.common.enums.QuestionTypeEnum;
import com.exam.common.util.JsonUtils;
import com.exam.entity.Question;
import com.exam.service.ScoringService;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 自动判分服务实现。
 */
@Service
public class ScoringServiceImpl implements ScoringService {

    @Override
    public Integer scoreObjectiveQuestion(Question question, List<String> studentAnswers, Integer questionScore) {
        List<String> standardAnswers = JsonUtils.toStringList(question.getAnswerJson());
        List<String> actualAnswers = studentAnswers == null ? List.of() : studentAnswers;
        QuestionTypeEnum questionType = QuestionTypeEnum.valueOf(question.getQuestionType());
        return switch (questionType) {
            case SINGLE_CHOICE, TRUE_FALSE, FILL_BLANK -> standardAnswers.equals(actualAnswers) ? questionScore : 0;
            case MULTIPLE_CHOICE -> standardAnswers.stream().sorted().toList()
                    .equals(actualAnswers.stream().sorted().toList()) ? questionScore : 0;
            case SHORT_ANSWER -> 0;
        };
    }

    @Override
    public boolean isObjectiveQuestion(String questionType) {
        return QuestionTypeEnum.SHORT_ANSWER.name().equals(questionType) == false;
    }
}
