package com.exam.convert;

import com.exam.common.util.JsonUtils;
import com.exam.entity.Question;
import com.exam.vo.question.QuestionVO;

/**
 * 题目对象转换器。
 */
public final class QuestionConvert {

    private QuestionConvert() {
    }

    public static QuestionVO toVO(Question question) {
        return QuestionVO.builder()
                .id(question.getId())
                .subjectId(question.getSubjectId())
                .questionType(question.getQuestionType())
                .title(question.getTitle())
                .imageUrls(JsonUtils.toStringList(question.getImageJson()))
                .options(JsonUtils.toStringList(question.getOptionsJson()))
                .answers(JsonUtils.toStringList(question.getAnswerJson()))
                .analysis(question.getAnalysis())
                .score(question.getScore())
                .difficulty(question.getDifficulty())
                .knowledgePoint(question.getKnowledgePoint())
                .creatorId(question.getCreatorId())
                .createTime(question.getCreateTime())
                .build();
    }
}
