package com.exam.service.impl;

import com.exam.common.api.ResultCode;
import com.exam.common.exception.BusinessException;
import com.exam.common.result.PageResult;
import com.exam.dto.score.ScoreQueryDTO;
import com.exam.entity.Exam;
import com.exam.entity.ExamScore;
import com.exam.entity.ExamStudent;
import com.exam.entity.SysUser;
import com.exam.mapper.ExamMapper;
import com.exam.mapper.ExamScoreMapper;
import com.exam.mapper.ExamStudentMapper;
import com.exam.mapper.SysUserMapper;
import com.exam.security.context.SecurityContextUtils;
import com.exam.service.ScoreService;
import com.exam.vo.score.ScoreStatisticsVO;
import com.exam.vo.score.ScoreVO;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 成绩服务实现。
 */
@Service
public class ScoreServiceImpl implements ScoreService {

    private final ExamScoreMapper examScoreMapper;
    private final ExamMapper examMapper;
    private final ExamStudentMapper examStudentMapper;
    private final SysUserMapper sysUserMapper;

    public ScoreServiceImpl(
            ExamScoreMapper examScoreMapper,
            ExamMapper examMapper,
            ExamStudentMapper examStudentMapper,
            SysUserMapper sysUserMapper) {
        this.examScoreMapper = examScoreMapper;
        this.examMapper = examMapper;
        this.examStudentMapper = examStudentMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public PageResult<ScoreVO> page(ScoreQueryDTO queryDTO) {
        boolean admin = SecurityContextUtils.hasAnyRole("ADMIN");
        List<ScoreVO> records = examScoreMapper.selectScorePage(queryDTO, SecurityContextUtils.getUserId(), admin);
        Long total = examScoreMapper.selectScoreCount(queryDTO, SecurityContextUtils.getUserId(), admin);
        return new PageResult<>(records, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public PageResult<ScoreVO> myScores(ScoreQueryDTO queryDTO) {
        List<ScoreVO> records = examScoreMapper.selectMyScorePage(queryDTO, SecurityContextUtils.getUserId());
        Long total = examScoreMapper.selectMyScoreCount(queryDTO, SecurityContextUtils.getUserId());
        return new PageResult<>(records, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public ScoreVO detail(Long examId, Long studentId) {
        Exam exam = getExam(examId);
        if (studentId == null) {
            studentId = SecurityContextUtils.getUserId();
        }
        if (SecurityContextUtils.hasAnyRole("ADMIN", "TEACHER")) {
            validateTeacherScoreAccess(exam);
        } else {
            validateStudentScoreAccess(examId, studentId);
        }
        ExamScore examScore = examScoreMapper.selectByExamIdAndStudentId(examId, studentId);
        if (examScore == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "成绩不存在");
        }
        SysUser student = sysUserMapper.selectById(studentId);
        return ScoreVO.builder()
                .id(examScore.getId())
                .examId(examId)
                .examName(exam.getExamName())
                .studentId(studentId)
                .studentName(student.getRealName())
                .objectiveScore(examScore.getObjectiveScore())
                .subjectiveScore(examScore.getSubjectiveScore())
                .totalScore(examScore.getTotalScore())
                .passFlag(examScore.getPassFlag())
                .excellentFlag(examScore.getExcellentFlag())
                .rankNo(examScore.getRankNo())
                .scoreStatus(examScore.getScoreStatus())
                .publishTime(examScore.getPublishTime())
                .build();
    }

    @Override
    public ScoreStatisticsVO statistics(Long examId) {
        validateTeacherScoreAccess(getExam(examId));
        Integer totalCount = defaultZero(examScoreMapper.selectTotalCount(examId));
        Integer passCount = defaultZero(examScoreMapper.selectPassCount(examId));
        Integer excellentCount = defaultZero(examScoreMapper.selectExcellentCount(examId));
        double passRate = totalCount == 0 ? 0 : passCount * 100.0 / totalCount;
        double excellentRate = totalCount == 0 ? 0 : excellentCount * 100.0 / totalCount;
        return ScoreStatisticsVO.builder()
                .examId(examId)
                .totalCount(totalCount)
                .passCount(passCount)
                .excellentCount(excellentCount)
                .passRate(passRate)
                .excellentRate(excellentRate)
                .scoreSegments(examScoreMapper.selectSegmentStatistics(examId))
                .build();
    }

    @Override
    public List<ScoreVO> ranking(Long examId) {
        validateTeacherScoreAccess(getExam(examId));
        return examScoreMapper.selectRanking(examId);
    }

    @Override
    public byte[] export(Long examId) {
        Exam exam = getExam(examId);
        validateTeacherScoreAccess(exam);
        List<ScoreVO> ranking = examScoreMapper.selectRanking(examId);
        StringBuilder builder = new StringBuilder("\uFEFF");
        builder.append("考试ID,考试名称,学生ID,学生姓名,客观题得分,主观题得分,总分,排名,及格,优秀,成绩状态,发布时间\n");
        for (ScoreVO score : ranking) {
            builder.append(examId).append(',')
                    .append(csvValue(exam.getExamName())).append(',')
                    .append(score.getStudentId()).append(',')
                    .append(csvValue(score.getStudentName())).append(',')
                    .append(defaultZero(score.getObjectiveScore())).append(',')
                    .append(defaultZero(score.getSubjectiveScore())).append(',')
                    .append(defaultZero(score.getTotalScore())).append(',')
                    .append(defaultZero(score.getRankNo())).append(',')
                    .append(score.getPassFlag() != null && score.getPassFlag() == 1 ? "是" : "否").append(',')
                    .append(score.getExcellentFlag() != null && score.getExcellentFlag() == 1 ? "是" : "否").append(',')
                    .append(csvValue(score.getScoreStatus())).append(',')
                    .append(csvValue(score.getPublishTime() == null ? "" : score.getPublishTime().toString()))
                    .append('\n');
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private Exam getExam(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "考试不存在");
        }
        return exam;
    }

    private void validateTeacherScoreAccess(Exam exam) {
        if (!SecurityContextUtils.hasAnyRole("ADMIN") && !exam.getCreatorId().equals(SecurityContextUtils.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "教师只能查看自己考试的成绩");
        }
    }

    private void validateStudentScoreAccess(Long examId, Long studentId) {
        Long currentUserId = SecurityContextUtils.getUserId();
        if (!studentId.equals(currentUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "学生只能查看自己的成绩");
        }
        ExamStudent examStudent = examStudentMapper.selectByExamIdAndStudentId(examId, currentUserId);
        if (examStudent == null) {
            throw new BusinessException(ResultCode.FORBIDDEN, "没有考试权限");
        }
    }

    private String csvValue(String value) {
        String text = value == null ? "" : value;
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    private Integer defaultZero(Integer value) {
        return value == null ? 0 : value;
    }
}
