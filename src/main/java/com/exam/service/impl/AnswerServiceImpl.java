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
import java.util.Collections;
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
        List<ExamVO> records = examMapper
                .selectStudentExamPage(SecurityContextUtils.getUserId(), null, 0, Integer.MAX_VALUE)
                .stream()
                .map(this::normalizeExamStatus)
                .filter(exam -> matchesStatus(exam, queryDTO.getStatus()))
                .map((exam) -> {
                    ExamStudent examStudent =
                            examStudentMapper.selectByExamIdAndStudentId(exam.getId(), SecurityContextUtils.getUserId());
                    return toExamVO(exam, List.of(), exam.getEndTime(), examStudent == null ? null : examStudent.getAnswerStatus());
                })
                .toList();
        int fromIndex = Math.max((queryDTO.getPageNum() - 1) * queryDTO.getPageSize(), 0);
        if (fromIndex >= records.size()) {
            return new PageResult<>(Collections.emptyList(), (long) records.size(), queryDTO.getPageNum(), queryDTO.getPageSize());
        }
        int toIndex = Math.min(fromIndex + queryDTO.getPageSize(), records.size());
        return new PageResult<>(records.subList(fromIndex, toIndex), (long) records.size(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public ExamVO examDetail(Long examId) {
        Exam exam = getAccessibleExam(examId);
        ExamStudent examStudent = examStudentMapper.selectByExamIdAndStudentId(examId, SecurityContextUtils.getUserId());
        validateExamAccess(exam);
        validateExamParticipation(examStudent);
        return toExamVO(
                exam,
                buildExamQuestionList(exam),
                resolveCountdownEndTime(exam, examStudent),
                examStudent == null ? null : examStudent.getAnswerStatus());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void startExam(Long examId) {
        Exam exam = getAccessibleExam(examId);
        validateExamAccess(exam);
        ExamStudent examStudent = examStudentMapper.selectByExamIdAndStudentId(examId, SecurityContextUtils.getUserId());
        if (examStudent == null) {
            throw new BusinessException(ResultCode.FORBIDDEN, "没有考试权限");
        }
        if (AnswerStatusEnum.ANSWERING.name().equals(examStudent.getAnswerStatus())) {
            return;
        }
        if (AnswerStatusEnum.SUBMITTED.name().equals(examStudent.getAnswerStatus())
                || AnswerStatusEnum.MARKED.name().equals(examStudent.getAnswerStatus())
                || AnswerStatusEnum.WAIT_MARKING.name().equals(examStudent.getAnswerStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "试卷已提交，不能重新进入考试");
        }
        examStudentMapper.updateAnswerStatus(
                examId, SecurityContextUtils.getUserId(), AnswerStatusEnum.ANSWERING.name(), LocalDateTime.now());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAnswers(Long examId, AnswerSaveDTO answerSaveDTO) {
        Exam exam = getAccessibleExam(examId);
        validateExamAccess(exam);
        validateExamParticipation(examStudentMapper.selectByExamIdAndStudentId(examId, SecurityContextUtils.getUserId()));
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
            if (question == null) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "试卷包含已删除题目，题目ID：" + paperQuestion.getQuestionId());
            }
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
                examId,
                SecurityContextUtils.getUserId(),
                hasSubjective ? AnswerStatusEnum.WAIT_MARKING.name() : AnswerStatusEnum.MARKED.name(),
                LocalDateTime.now());
        if (!hasSubjective) {
            examScoreMapper.updateRankByExamId(examId);
        }
    }

    @Override
    public List<AnswerVO> answerDetail(Long examId) {
        Exam exam = getAccessibleExam(examId);
        validateAnswerReviewAccess(exam, examStudentMapper.selectByExamIdAndStudentId(examId, SecurityContextUtils.getUserId()));
        return buildExamQuestionList(exam);
    }

    private List<AnswerVO> buildExamQuestionList(Exam exam) {
        Long studentId = SecurityContextUtils.getUserId();
        Map<Long, StudentAnswer> answerMap = new HashMap<>();
        for (StudentAnswer answer : studentAnswerMapper.selectByExamIdAndStudentId(exam.getId(), studentId)) {
            answerMap.put(answer.getQuestionId(), answer);
        }
        return paperQuestionMapper.selectByPaperId(exam.getPaperId()).stream()
                .sorted(Comparator.comparing(PaperQuestion::getSortNo))
                .map(paperQuestion -> {
                    Question question = questionMapper.selectById(paperQuestion.getQuestionId());
                    if (question == null) {
                        throw new BusinessException(ResultCode.BAD_REQUEST, "试卷包含已删除题目，题目ID：" + paperQuestion.getQuestionId());
                    }
                    StudentAnswer answer = answerMap.get(question.getId());
                    return AnswerVO.builder()
                            .id(answer == null ? null : answer.getId())
                            .questionId(question.getId())
                            .questionType(question.getQuestionType())
                            .title(question.getTitle())
                            .imageUrls(JsonUtils.toStringList(question.getImageJson()))
                            .options(JsonUtils.toStringList(question.getOptionsJson()))
                            .standardAnswers(JsonUtils.toStringList(question.getAnswerJson()))
                            .studentAnswers(answer == null ? List.of() : JsonUtils.toStringList(answer.getAnswerContent()))
                            .questionScore(paperQuestion.getQuestionScore())
                            .actualScore(answer == null ? 0 : answer.getActualScore())
                            .teacherComment(answer == null ? null : answer.getTeacherComment())
                            .markingStatus(answer == null ? AnswerStatusEnum.NOT_STARTED.name() : answer.getMarkingStatus())
                            .submitTime(answer == null ? null : answer.getSubmitTime())
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
        studentAnswer.setActualScore(0);
        studentAnswer.setObjectiveFlag(scoringService.isObjectiveQuestion(question.getQuestionType()) ? 1 : 0);
        studentAnswer.setCorrectFlag(0);
        studentAnswer.setMarkingStatus(AnswerStatusEnum.ANSWERING.name());
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
        return normalizeExamStatus(exam);
    }

    private void validateExamAccess(Exam exam) {
        if (ExamStatusEnum.DRAFT.name().equals(exam.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "考试尚未发布");
        }
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
        if (question == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "题目不存在或已删除，题目ID：" + questionId);
        }
        return !QuestionTypeEnum.SHORT_ANSWER.name().equals(question.getQuestionType());
    }

    private ExamVO toExamVO(Exam exam) {
        return toExamVO(exam, List.of(), exam.getEndTime(), null);
    }

    private ExamVO toExamVO(Exam exam, List<AnswerVO> questions, LocalDateTime countdownEndTime, String answerStatus) {
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
                .answerStatus(answerStatus)
                .countdownEndTime(countdownEndTime)
                .questions(questions)
                .build();
    }

    private LocalDateTime resolveCountdownEndTime(Exam exam, ExamStudent examStudent) {
        LocalDateTime examDeadline = exam.getEndTime();
        if (examStudent == null) {
            return examDeadline;
        }
        if (!AnswerStatusEnum.ANSWERING.name().equals(examStudent.getAnswerStatus())) {
            return examDeadline;
        }
        LocalDateTime personalDeadline = examStudent.getUpdateTime().plusMinutes(exam.getDurationMinutes());
        return personalDeadline.isBefore(examDeadline) ? personalDeadline : examDeadline;
    }

    private void validateExamParticipation(ExamStudent examStudent) {
        if (examStudent == null) {
            throw new BusinessException(ResultCode.FORBIDDEN, "没有考试权限");
        }
        if (AnswerStatusEnum.SUBMITTED.name().equals(examStudent.getAnswerStatus())
                || AnswerStatusEnum.WAIT_MARKING.name().equals(examStudent.getAnswerStatus())
                || AnswerStatusEnum.MARKED.name().equals(examStudent.getAnswerStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "试卷已提交，不能再次参加考试");
        }
    }

    private void validateAnswerReviewAccess(Exam exam, ExamStudent examStudent) {
        if (examStudent == null) {
            throw new BusinessException(ResultCode.FORBIDDEN, "没有考试权限");
        }
        if (!AnswerStatusEnum.SUBMITTED.name().equals(examStudent.getAnswerStatus())
                && !AnswerStatusEnum.WAIT_MARKING.name().equals(examStudent.getAnswerStatus())
                && !AnswerStatusEnum.MARKED.name().equals(examStudent.getAnswerStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "提交试卷后才能查看答题详情");
        }
        if (exam.getResultPublished() == null || exam.getResultPublished() != 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "成绩尚未发布");
        }
    }

    private Exam normalizeExamStatus(Exam exam) {
        String currentStatus = resolveCurrentStatus(exam);
        if (!currentStatus.equals(exam.getStatus())) {
            exam.setStatus(currentStatus);
            exam.setUpdateTime(LocalDateTime.now());
            examMapper.updateById(exam);
        }
        return exam;
    }

    private String resolveCurrentStatus(Exam exam) {
        if (ExamStatusEnum.DRAFT.name().equals(exam.getStatus())
                || ExamStatusEnum.GRADED.name().equals(exam.getStatus())
                || ExamStatusEnum.RESULT_PUBLISHED.name().equals(exam.getStatus())) {
            return exam.getStatus();
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartTime())) {
            return ExamStatusEnum.NOT_STARTED.name();
        }
        if (now.isAfter(exam.getEndTime())) {
            return ExamStatusEnum.ENDED.name();
        }
        return ExamStatusEnum.IN_PROGRESS.name();
    }

    private boolean matchesStatus(Exam exam, String status) {
        if (status == null || status.isBlank()) {
            return true;
        }
        if (ExamStatusEnum.ENDED.name().equals(status)) {
            return ExamStatusEnum.ENDED.name().equals(exam.getStatus())
                    || ExamStatusEnum.GRADED.name().equals(exam.getStatus())
                    || ExamStatusEnum.RESULT_PUBLISHED.name().equals(exam.getStatus());
        }
        return status.equals(exam.getStatus());
    }
}
