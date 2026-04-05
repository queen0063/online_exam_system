package com.exam.service.impl;

import com.exam.common.api.ResultCode;
import com.exam.common.enums.AnswerStatusEnum;
import com.exam.common.enums.ExamStatusEnum;
import com.exam.common.enums.QuestionTypeEnum;
import com.exam.common.exception.BusinessException;
import com.exam.common.result.PageResult;
import com.exam.common.util.JsonUtils;
import com.exam.dto.answer.AnswerItemDTO;
import com.exam.dto.answer.AnswerSaveDTO;
import com.exam.dto.exam.ExamQueryDTO;
import com.exam.entity.Exam;
import com.exam.entity.ExamScore;
import com.exam.entity.ExamStudent;
import com.exam.entity.Paper;
import com.exam.entity.PaperQuestion;
import com.exam.entity.Question;
import com.exam.entity.StudentAnswer;
import com.exam.mapper.ExamMapper;
import com.exam.mapper.ExamScoreMapper;
import com.exam.mapper.ExamStudentMapper;
import com.exam.mapper.PaperMapper;
import com.exam.mapper.PaperQuestionMapper;
import com.exam.mapper.QuestionMapper;
import com.exam.mapper.StudentAnswerMapper;
import com.exam.security.context.SecurityContextUtils;
import com.exam.service.AnswerService;
import com.exam.service.ScoringService;
import com.exam.vo.answer.AnswerVO;
import com.exam.vo.exam.ExamVO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 在线答题服务实现。
 */
@Service
public class AnswerServiceImpl implements AnswerService {

    private final ExamMapper examMapper;
    private final ExamStudentMapper examStudentMapper;
    private final PaperMapper paperMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final QuestionMapper questionMapper;
    private final StudentAnswerMapper studentAnswerMapper;
    private final ExamScoreMapper examScoreMapper;
    private final ScoringService scoringService;

    public AnswerServiceImpl(
            ExamMapper examMapper,
            ExamStudentMapper examStudentMapper,
            PaperMapper paperMapper,
            PaperQuestionMapper paperQuestionMapper,
            QuestionMapper questionMapper,
            StudentAnswerMapper studentAnswerMapper,
            ExamScoreMapper examScoreMapper,
            ScoringService scoringService) {
        this.examMapper = examMapper;
        this.examStudentMapper = examStudentMapper;
        this.paperMapper = paperMapper;
        this.paperQuestionMapper = paperQuestionMapper;
        this.questionMapper = questionMapper;
        this.studentAnswerMapper = studentAnswerMapper;
        this.examScoreMapper = examScoreMapper;
        this.scoringService = scoringService;
    }

    @Override
    public PageResult<ExamVO> myExamPage(ExamQueryDTO queryDTO) {
        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        List<ExamVO> records = examMapper
                .selectStudentExamPage(SecurityContextUtils.getUserId(), queryDTO.getStatus(), offset, queryDTO.getPageSize())
                .stream()
                .map(this::toExamVO)
                .toList();
        Long total = examMapper.selectStudentExamCount(SecurityContextUtils.getUserId(), queryDTO.getStatus());
        return new PageResult<>(records, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public ExamVO examDetail(Long examId) {
        Exam exam = getAccessibleExam(examId);
        return toExamVO(exam);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void startExam(Long examId) {
        Exam exam = getAccessibleExam(examId);
        validateExamAccess(exam);
        examStudentMapper.updateAnswerStatus(examId, SecurityContextUtils.getUserId(), AnswerStatusEnum.ANSWERING.name());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAnswers(Long examId, AnswerSaveDTO answerSaveDTO) {
        Exam exam = getAccessibleExam(examId);
        validateExamAccess(exam);
        Paper paper = paperMapper.selectById(exam.getPaperId());
        Map<Long, PaperQuestion> paperQuestionMap = paperQuestionMapper.selectByPaperId(paper.getId()).stream()
                .collect(HashMap::new, (map, item) -> map.put(item.getQuestionId(), item), HashMap::putAll);
        for (AnswerItemDTO answerItem : answerSaveDTO.getAnswers()) {
            PaperQuestion paperQuestion = paperQuestionMap.get(answerItem.getQuestionId());
            if (paperQuestion == null) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "题目不属于当前试卷");
            }
            StudentAnswer dbAnswer = studentAnswerMapper.selectByExamIdAndStudentIdAndQuestionId(
                    examId, SecurityContextUtils.getUserId(), answerItem.getQuestionId());
            if (dbAnswer == null) {
                dbAnswer = new StudentAnswer();
                dbAnswer.setExamId(examId);
                dbAnswer.setPaperId(paper.getId());
                dbAnswer.setQuestionId(answerItem.getQuestionId());
                dbAnswer.setStudentId(SecurityContextUtils.getUserId());
                dbAnswer.setQuestionScore(paperQuestion.getQuestionScore());
                dbAnswer.setObjectiveFlag(isObjective(answerItem.getQuestionId()) ? 1 : 0);
                dbAnswer.setMarkingStatus(AnswerStatusEnum.ANSWERING.name());
                dbAnswer.setCreateTime(LocalDateTime.now());
                dbAnswer.setUpdateTime(LocalDateTime.now());
                dbAnswer.setDeleted(0);
                dbAnswer.setAnswerContent(JsonUtils.toJson(answerItem.getAnswers()));
                studentAnswerMapper.insert(dbAnswer);
            } else {
                dbAnswer.setAnswerContent(JsonUtils.toJson(answerItem.getAnswers()));
                dbAnswer.setUpdateTime(LocalDateTime.now());
                studentAnswerMapper.updateById(dbAnswer);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void submit(Long examId) {
        Exam exam = getAccessibleExam(examId);
        validateExamAccess(exam);
        ExamStudent examStudent = examStudentMapper.selectByExamIdAndStudentId(examId, SecurityContextUtils.getUserId());
        if (AnswerStatusEnum.SUBMITTED.name().equals(examStudent.getAnswerStatus())
                || AnswerStatusEnum.MARKED.name().equals(examStudent.getAnswerStatus())
                || AnswerStatusEnum.WAIT_MARKING.name().equals(examStudent.getAnswerStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "试卷已提交，不能重复提交");
        }

        List<StudentAnswer> answers = studentAnswerMapper.selectByExamIdAndStudentId(examId, SecurityContextUtils.getUserId());
        Map<Long, StudentAnswer> answerMap = new HashMap<>();
        for (StudentAnswer answer : answers) {
            answerMap.put(answer.getQuestionId(), answer);
        }
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectByPaperId(exam.getPaperId());
        int objectiveScore = 0;
        boolean hasSubjective = false;
        for (PaperQuestion paperQuestion : paperQuestions) {
            Question question = questionMapper.selectById(paperQuestion.getQuestionId());
            StudentAnswer studentAnswer = answerMap.get(question.getId());
            if (studentAnswer == null) {
                studentAnswer = buildEmptyAnswer(exam, paperQuestion, question);
                studentAnswerMapper.insert(studentAnswer);
                answerMap.put(question.getId(), studentAnswer);
            }
            if (scoringService.isObjectiveQuestion(question.getQuestionType())) {
                Integer score = scoringService.scoreObjectiveQuestion(
                        question, JsonUtils.toStringList(studentAnswer.getAnswerContent()), paperQuestion.getQuestionScore());
                studentAnswer.setActualScore(score);
                studentAnswer.setCorrectFlag(score.equals(paperQuestion.getQuestionScore()) ? 1 : 0);
                studentAnswer.setMarkingStatus(AnswerStatusEnum.MARKED.name());
                studentAnswer.setSubmitTime(LocalDateTime.now());
                studentAnswer.setUpdateTime(LocalDateTime.now());
                studentAnswerMapper.updateById(studentAnswer);
                objectiveScore += score;
            } else {
                hasSubjective = true;
                studentAnswer.setActualScore(0);
                studentAnswer.setCorrectFlag(0);
                studentAnswer.setMarkingStatus(AnswerStatusEnum.WAIT_MARKING.name());
                studentAnswer.setSubmitTime(LocalDateTime.now());
                studentAnswer.setUpdateTime(LocalDateTime.now());
                studentAnswerMapper.updateById(studentAnswer);
            }
        }

        ExamScore examScore = examScoreMapper.selectByExamIdAndStudentId(examId, SecurityContextUtils.getUserId());
        if (examScore == null) {
            examScore = new ExamScore();
            examScore.setExamId(examId);
            examScore.setStudentId(SecurityContextUtils.getUserId());
            examScore.setCreateTime(LocalDateTime.now());
            examScore.setDeleted(0);
        }
        examScore.setObjectiveScore(objectiveScore);
        examScore.setSubjectiveScore(0);
        examScore.setTotalScore(objectiveScore);
        examScore.setPassFlag(objectiveScore >= exam.getPassScore() ? 1 : 0);
        examScore.setExcellentFlag(0);
        examScore.setScoreStatus(hasSubjective ? AnswerStatusEnum.WAIT_MARKING.name() : AnswerStatusEnum.MARKED.name());
        examScore.setSubmitTime(LocalDateTime.now());
        examScore.setUpdateTime(LocalDateTime.now());
        if (examScore.getId() == null) {
            examScoreMapper.insert(examScore);
        } else {
            examScoreMapper.updateById(examScore);
        }
        examStudentMapper.updateAnswerStatus(
                examId, SecurityContextUtils.getUserId(), hasSubjective ? AnswerStatusEnum.WAIT_MARKING.name() : AnswerStatusEnum.MARKED.name());
        if (!hasSubjective) {
            examScoreMapper.updateRankByExamId(examId);
        }
    }

    @Override
    public List<AnswerVO> answerDetail(Long examId) {
        getAccessibleExam(examId);
        return buildAnswerVOList(examId, SecurityContextUtils.getUserId());
    }

    private List<AnswerVO> buildAnswerVOList(Long examId, Long studentId) {
        List<StudentAnswer> answers = studentAnswerMapper.selectByExamIdAndStudentId(examId, studentId);
        return answers.stream()
                .sorted(Comparator.comparing(StudentAnswer::getQuestionId))
                .map(answer -> {
                    Question question = questionMapper.selectById(answer.getQuestionId());
                    return AnswerVO.builder()
                            .id(answer.getId())
                            .questionId(answer.getQuestionId())
                            .questionType(question.getQuestionType())
                            .title(question.getTitle())
                            .options(JsonUtils.toStringList(question.getOptionsJson()))
                            .standardAnswers(JsonUtils.toStringList(question.getAnswerJson()))
                            .studentAnswers(JsonUtils.toStringList(answer.getAnswerContent()))
                            .questionScore(answer.getQuestionScore())
                            .actualScore(answer.getActualScore())
                            .teacherComment(answer.getTeacherComment())
                            .markingStatus(answer.getMarkingStatus())
                            .submitTime(answer.getSubmitTime())
                            .build();
                })
                .toList();
    }

    private StudentAnswer buildEmptyAnswer(Exam exam, PaperQuestion paperQuestion, Question question) {
        StudentAnswer studentAnswer = new StudentAnswer();
        studentAnswer.setExamId(exam.getId());
        studentAnswer.setPaperId(exam.getPaperId());
        studentAnswer.setQuestionId(question.getId());
        studentAnswer.setStudentId(SecurityContextUtils.getUserId());
        studentAnswer.setAnswerContent(JsonUtils.toJson(List.of()));
        studentAnswer.setQuestionScore(paperQuestion.getQuestionScore());
        studentAnswer.setObjectiveFlag(scoringService.isObjectiveQuestion(question.getQuestionType()) ? 1 : 0);
        studentAnswer.setCreateTime(LocalDateTime.now());
        studentAnswer.setUpdateTime(LocalDateTime.now());
        studentAnswer.setDeleted(0);
        return studentAnswer;
    }

    private Exam getAccessibleExam(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "考试不存在");
        }
        ExamStudent examStudent = examStudentMapper.selectByExamIdAndStudentId(examId, SecurityContextUtils.getUserId());
        if (examStudent == null) {
            throw new BusinessException(ResultCode.FORBIDDEN, "没有考试权限");
        }
        return exam;
    }

    private void validateExamAccess(Exam exam) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartTime())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "学生只能在考试开始后进入考试");
        }
        if (now.isAfter(exam.getEndTime())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "超过考试结束时间不能进入");
        }
        if (ExamStatusEnum.RESULT_PUBLISHED.name().equals(exam.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "考试已结束");
        }
    }

    private boolean isObjective(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        return !QuestionTypeEnum.SHORT_ANSWER.name().equals(question.getQuestionType());
    }

    private ExamVO toExamVO(Exam exam) {
        return ExamVO.builder()
                .id(exam.getId())
                .examName(exam.getExamName())
                .paperId(exam.getPaperId())
                .subjectId(exam.getSubjectId())
                .creatorId(exam.getCreatorId())
                .startTime(exam.getStartTime())
                .endTime(exam.getEndTime())
                .durationMinutes(exam.getDurationMinutes())
                .passScore(exam.getPassScore())
                .status(exam.getStatus())
                .resultPublished(exam.getResultPublished())
                .build();
    }
}
