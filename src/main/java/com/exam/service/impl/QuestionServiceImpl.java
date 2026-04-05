package com.exam.service.impl;

import com.exam.common.api.ResultCode;
import com.exam.common.exception.BusinessException;
import com.exam.common.result.PageResult;
import com.exam.common.util.JsonUtils;
import com.exam.convert.QuestionConvert;
import com.exam.dto.question.QuestionQueryDTO;
import com.exam.dto.question.QuestionSaveDTO;
import com.exam.entity.Question;
import com.exam.mapper.QuestionMapper;
import com.exam.security.context.SecurityContextUtils;
import com.exam.service.QuestionService;
import com.exam.vo.question.QuestionVO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 题库服务实现。
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;

    public QuestionServiceImpl(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    @Override
    public PageResult<QuestionVO> page(QuestionQueryDTO queryDTO) {
        List<QuestionVO> records = questionMapper.selectPage(queryDTO).stream()
                .map(QuestionConvert::toVO)
                .toList();
        return new PageResult<>(records, questionMapper.selectCount(queryDTO), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public QuestionVO detail(Long id) {
        return QuestionConvert.toVO(getQuestion(id));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(QuestionSaveDTO questionSaveDTO) {
        Question question = questionSaveDTO.getId() == null ? new Question() : getQuestion(questionSaveDTO.getId());
        question.setSubjectId(questionSaveDTO.getSubjectId());
        question.setQuestionType(questionSaveDTO.getQuestionType());
        question.setTitle(questionSaveDTO.getTitle());
        question.setOptionsJson(JsonUtils.toJson(questionSaveDTO.getOptions()));
        question.setAnswerJson(JsonUtils.toJson(questionSaveDTO.getAnswers()));
        question.setAnalysis(questionSaveDTO.getAnalysis());
        question.setScore(questionSaveDTO.getScore());
        question.setDifficulty(questionSaveDTO.getDifficulty());
        question.setKnowledgePoint(questionSaveDTO.getKnowledgePoint());
        question.setCreatorId(SecurityContextUtils.getUserId());
        question.setUpdateTime(LocalDateTime.now());
        if (questionSaveDTO.getId() == null) {
            question.setCreateTime(LocalDateTime.now());
            question.setDeleted(0);
            questionMapper.insert(question);
        } else {
            enforceTeacherOwns(question);
            questionMapper.updateById(question);
        }
    }

    @Override
    public void remove(Long id) {
        Question question = getQuestion(id);
        enforceTeacherOwns(question);
        questionMapper.logicDeleteById(id);
    }

    @Override
    public List<QuestionVO> wrongQuestions(Long studentId) {
        return questionMapper.selectWrongQuestions(studentId).stream().map(QuestionConvert::toVO).toList();
    }

    private Question getQuestion(Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "题目不存在");
        }
        return question;
    }

    private void enforceTeacherOwns(Question question) {
        if (!SecurityContextUtils.hasAnyRole("ADMIN") && !question.getCreatorId().equals(SecurityContextUtils.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "教师只能管理自己创建的题目");
        }
    }
}
