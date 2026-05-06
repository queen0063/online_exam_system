package com.exam.service.impl;

import com.exam.common.api.ResultCode;
import com.exam.common.enums.AnswerStatusEnum;
import com.exam.common.enums.ExamStatusEnum;
import com.exam.common.exception.BusinessException;
import com.exam.common.result.PageResult;
import com.exam.common.util.JsonUtils;
import com.exam.dto.common.PageQueryDTO;
import com.exam.dto.marking.MarkingScoreDTO;
import com.exam.entity.Exam;
import com.exam.entity.ExamScore;
import com.exam.entity.StudentAnswer;
import com.exam.entity.SysUser;
import com.exam.mapper.ExamMapper;
import com.exam.mapper.ExamScoreMapper;
import com.exam.mapper.ExamStudentMapper;
import com.exam.mapper.QuestionMapper;
import com.exam.mapper.StudentAnswerMapper;
import com.exam.mapper.SysUserMapper;
import com.exam.security.context.SecurityContextUtils;
import com.exam.service.MarkingService;
import com.exam.vo.answer.AnswerVO;
import com.exam.vo.marking.MarkingVO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 阅卷服务实现。
 */
@Service
public class MarkingServiceImpl implements MarkingService {

    private final StudentAnswerMapper studentAnswerMapper;
    private final QuestionMapper questionMapper;
    private final ExamMapper examMapper;
    private final SysUserMapper sysUserMapper;
    private final ExamScoreMapper examScoreMapper;
    private final ExamStudentMapper examStudentMapper;

    public MarkingServiceImpl(
            StudentAnswerMapper studentAnswerMapper,
            QuestionMapper questionMapper,
            ExamMapper examMapper,
            SysUserMapper sysUserMapper,
            ExamScoreMapper examScoreMapper,
            ExamStudentMapper examStudentMapper) {
        this.studentAnswerMapper = studentAnswerMapper;
        this.questionMapper = questionMapper;
        this.examMapper = examMapper;
        this.sysUserMapper = sysUserMapper;
        this.examScoreMapper = examScoreMapper;
        this.examStudentMapper = examStudentMapper;
    }

    @Override
    public PageResult<MarkingVO> pendingPage(PageQueryDTO pageQueryDTO) {
        int offset = (pageQueryDTO.getPageNum() - 1) * pageQueryDTO.getPageSize();
        boolean admin = SecurityContextUtils.hasAnyRole("ADMIN");
        List<MarkingVO> records =
                examScoreMapper.selectPendingMarkingPage(SecurityContextUtils.getUserId(), offset, pageQueryDTO.getPageSize(), admin);
        Long total = examScoreMapper.countPendingMarking(SecurityContextUtils.getUserId(), admin);
        return new PageResult<>(records, total, pageQueryDTO.getPageNum(), pageQueryDTO.getPageSize());
    }

    @Override
    public MarkingVO detail(Long examId, Long studentId) {
        Exam exam = getOwnedExam(examId);
        SysUser student = sysUserMapper.selectById(studentId);
        List<AnswerVO> answers = studentAnswerMapper.selectByExamIdAndStudentId(examId, studentId).stream()
                .map(answer -> {
                    var question = questionMapper.selectById(answer.getQuestionId());
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
        ExamScore score = examScoreMapper.selectByExamIdAndStudentId(examId, studentId);
        return MarkingVO.builder()
                .examId(examId)
                .examName(exam.getExamName())
                .studentId(studentId)
                .studentName(student.getRealName())
                .objectiveScore(score == null ? 0 : score.getObjectiveScore())
                .subjectiveScore(score == null ? 0 : score.getSubjectiveScore())
                .totalScore(score == null ? 0 : score.getTotalScore())
                .scoreStatus(score == null ? AnswerStatusEnum.WAIT_MARKING.name() : score.getScoreStatus())
                .answers(answers)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void markQuestion(Long answerId, MarkingScoreDTO markingScoreDTO) {
        StudentAnswer answer = getAnswer(answerId);
        Exam exam = getOwnedExam(answer.getExamId());
        if (exam == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "考试不存在");
        }
        if (markingScoreDTO.getActualScore() > answer.getQuestionScore()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "得分不能超过题目分值");
        }
        if (answer.getObjectiveFlag() != null && answer.getObjectiveFlag() == 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "客观题无需人工评分");
        }
        answer.setActualScore(markingScoreDTO.getActualScore());
        answer.setCorrectFlag(markingScoreDTO.getActualScore() != null
                && markingScoreDTO.getActualScore().equals(answer.getQuestionScore()) ? 1 : 0);
        answer.setTeacherComment(markingScoreDTO.getTeacherComment());
        answer.setMarkingStatus(AnswerStatusEnum.MARKED.name());
        answer.setUpdateTime(LocalDateTime.now());
        studentAnswerMapper.updateById(answer);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void finishMarking(Long examId, Long studentId) {
        Exam exam = getOwnedExam(examId);
        List<StudentAnswer> answers = studentAnswerMapper.selectByExamIdAndStudentId(examId, studentId);
        int objectiveScore = 0;
        int subjectiveScore = 0;
        for (StudentAnswer answer : answers) {
            if (AnswerStatusEnum.WAIT_MARKING.name().equals(answer.getMarkingStatus())) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "仍存在未批改主观题");
            }
            if (answer.getObjectiveFlag() != null && answer.getObjectiveFlag() == 1) {
                objectiveScore += answer.getActualScore() == null ? 0 : answer.getActualScore();
            } else {
                subjectiveScore += answer.getActualScore() == null ? 0 : answer.getActualScore();
            }
        }
        int totalScore = objectiveScore + subjectiveScore;
        ExamScore examScore = examScoreMapper.selectByExamIdAndStudentId(examId, studentId);
        if (examScore == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "成绩记录不存在");
        }
        examScore.setObjectiveScore(objectiveScore);
        examScore.setSubjectiveScore(subjectiveScore);
        examScore.setTotalScore(totalScore);
        examScore.setPassFlag(totalScore >= exam.getPassScore() ? 1 : 0);
        examScore.setExcellentFlag(totalScore >= 90 ? 1 : 0);
        examScore.setScoreStatus(AnswerStatusEnum.MARKED.name());
        examScore.setUpdateTime(LocalDateTime.now());
        examScoreMapper.updateById(examScore);
        examStudentMapper.updateAnswerStatus(examId, studentId, AnswerStatusEnum.MARKED.name(), LocalDateTime.now());
        examMapper.updateById(buildGradedExam(exam));
        examScoreMapper.updateRankByExamId(examId);
    }

    private Exam getOwnedExam(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "考试不存在");
        }
        if (!SecurityContextUtils.hasAnyRole("ADMIN") && !exam.getCreatorId().equals(SecurityContextUtils.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "教师只能批改自己创建考试的答卷");
        }
        return exam;
    }

    private StudentAnswer getAnswer(Long answerId) {
        StudentAnswer answer = studentAnswerMapper.selectById(answerId);
        if (answer == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "答题记录不存在");
        }
        getOwnedExam(answer.getExamId());
        return answer;
    }

    private Exam buildGradedExam(Exam exam) {
        exam.setStatus(ExamStatusEnum.GRADED.name());
        exam.setUpdateTime(LocalDateTime.now());
        return exam;
    }
}
