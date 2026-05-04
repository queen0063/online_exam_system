package com.exam.service.impl;

import com.exam.common.api.ResultCode;
import com.exam.common.enums.AnswerStatusEnum;
import com.exam.common.enums.ExamStatusEnum;
import com.exam.common.exception.BusinessException;
import com.exam.common.result.PageResult;
import com.exam.dto.exam.ExamQueryDTO;
import com.exam.dto.exam.ExamSaveDTO;
import com.exam.entity.Exam;
import com.exam.entity.ExamStudent;
import com.exam.entity.Paper;
import com.exam.mapper.ExamMapper;
import com.exam.mapper.ExamScoreMapper;
import com.exam.mapper.ExamStudentMapper;
import com.exam.mapper.PaperMapper;
import com.exam.security.context.SecurityContextUtils;
import com.exam.service.ExamService;
import com.exam.vo.exam.ExamVO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 考试服务实现。
 */
@Service
public class ExamServiceImpl implements ExamService {

    private final ExamMapper examMapper;
    private final ExamStudentMapper examStudentMapper;
    private final PaperMapper paperMapper;
    private final ExamScoreMapper examScoreMapper;

    public ExamServiceImpl(
            ExamMapper examMapper,
            ExamStudentMapper examStudentMapper,
            PaperMapper paperMapper,
            ExamScoreMapper examScoreMapper) {
        this.examMapper = examMapper;
        this.examStudentMapper = examStudentMapper;
        this.paperMapper = paperMapper;
        this.examScoreMapper = examScoreMapper;
    }

    @Override
    public PageResult<ExamVO> page(ExamQueryDTO queryDTO) {
        boolean admin = SecurityContextUtils.hasAnyRole("ADMIN");
        List<Exam> records = examMapper.selectPage(buildStatusAgnosticQuery(queryDTO), SecurityContextUtils.getUserId(), admin).stream()
                .map(this::normalizeExamStatus)
                .filter(exam -> matchesStatus(exam, queryDTO.getStatus()))
                .toList();
        return buildPagedResult(records, queryDTO);
    }

    @Override
    public PageResult<ExamVO> myPage(ExamQueryDTO queryDTO) {
        List<Exam> records = examMapper
                .selectStudentExamPage(SecurityContextUtils.getUserId(), null, 0, Integer.MAX_VALUE)
                .stream()
                .map(this::normalizeExamStatus)
                .filter(exam -> matchesStatus(exam, queryDTO.getStatus()))
                .toList();
        return buildPagedResult(records, queryDTO);
    }

    @Override
    public ExamVO detail(Long id) {
        Exam exam = getExam(id);
        List<Long> studentIds = examStudentMapper.selectByExamId(id).stream()
                .map(ExamStudent::getStudentId)
                .toList();
        return toVO(exam, studentIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(ExamSaveDTO examSaveDTO) {
        if (!examSaveDTO.getEndTime().isAfter(examSaveDTO.getStartTime())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "结束时间必须晚于开始时间");
        }
        Paper paper = paperMapper.selectById(examSaveDTO.getPaperId());
        if (paper == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "试卷不存在");
        }
        if (!SecurityContextUtils.hasAnyRole("ADMIN") && !paper.getCreatorId().equals(SecurityContextUtils.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "教师只能使用自己创建的试卷创建考试");
        }
        Exam exam = examSaveDTO.getId() == null ? new Exam() : getExam(examSaveDTO.getId());
        if (examSaveDTO.getId() != null) {
            enforceOwnExam(exam);
        }
        exam.setExamName(examSaveDTO.getExamName());
        exam.setPaperId(examSaveDTO.getPaperId());
        exam.setSubjectId(examSaveDTO.getSubjectId());
        exam.setCreatorId(SecurityContextUtils.getUserId());
        exam.setStartTime(examSaveDTO.getStartTime());
        exam.setEndTime(examSaveDTO.getEndTime());
        exam.setDurationMinutes(examSaveDTO.getDurationMinutes());
        exam.setPassScore(examSaveDTO.getPassScore());
        exam.setStatus(ExamStatusEnum.DRAFT.name());
        exam.setResultPublished(0);
        exam.setUpdateTime(LocalDateTime.now());
        if (examSaveDTO.getId() == null) {
            exam.setCreateTime(LocalDateTime.now());
            exam.setDeleted(0);
            examMapper.insert(exam);
        } else {
            examMapper.updateById(exam);
            examStudentMapper.deleteByExamId(exam.getId());
        }
        saveExamStudents(exam.getId(), examSaveDTO.getStudentIds());
    }

    @Override
    public void publish(Long id) {
        Exam exam = getExam(id);
        enforceOwnExam(exam);
        exam.setStatus(resolvePublishStatus(exam));
        exam.setUpdateTime(LocalDateTime.now());
        examMapper.updateById(exam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishScore(Long id) {
        Exam exam = getExam(id);
        enforceOwnExam(exam);
        Long unmarkedCount = examScoreMapper.countUnmarkedByExamId(id);
        if (unmarkedCount != null && unmarkedCount > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "仍有未完成阅卷的成绩，不能发布成绩");
        }
        LocalDateTime publishTime = LocalDateTime.now();
        exam.setResultPublished(1);
        exam.setStatus(ExamStatusEnum.RESULT_PUBLISHED.name());
        exam.setUpdateTime(publishTime);
        examMapper.updateById(exam);
        examScoreMapper.updatePublishTimeByExamId(id, publishTime);
    }

    @Override
    public void remove(Long id) {
        Exam exam = getExam(id);
        enforceOwnExam(exam);
        examMapper.logicDeleteById(id);
        examStudentMapper.deleteByExamId(id);
    }

    private void saveExamStudents(Long examId, List<Long> studentIds) {
        List<ExamStudent> list = new ArrayList<>();
        for (Long studentId : studentIds) {
            ExamStudent examStudent = new ExamStudent();
            examStudent.setExamId(examId);
            examStudent.setStudentId(studentId);
            examStudent.setAnswerStatus(AnswerStatusEnum.NOT_STARTED.name());
            examStudent.setCreateTime(LocalDateTime.now());
            examStudent.setUpdateTime(LocalDateTime.now());
            examStudent.setDeleted(0);
            list.add(examStudent);
        }
        if (!list.isEmpty()) {
            examStudentMapper.batchInsert(list);
        }
    }

    private Exam getExam(Long id) {
        Exam exam = examMapper.selectById(id);
        if (exam == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "考试不存在");
        }
        return normalizeExamStatus(exam);
    }

    private void enforceOwnExam(Exam exam) {
        if (!SecurityContextUtils.hasAnyRole("ADMIN") && !exam.getCreatorId().equals(SecurityContextUtils.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "教师只能操作自己创建的考试");
        }
    }

    private String resolvePublishStatus(Exam exam) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartTime())) {
            return ExamStatusEnum.NOT_STARTED.name();
        }
        if (now.isAfter(exam.getEndTime())) {
            return ExamStatusEnum.ENDED.name();
        }
        return ExamStatusEnum.IN_PROGRESS.name();
    }

    private String resolveCurrentStatus(Exam exam) {
        if (ExamStatusEnum.DRAFT.name().equals(exam.getStatus())
                || ExamStatusEnum.GRADED.name().equals(exam.getStatus())
                || ExamStatusEnum.RESULT_PUBLISHED.name().equals(exam.getStatus())) {
            return exam.getStatus();
        }
        return resolvePublishStatus(exam);
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

    private boolean matchesStatus(Exam exam, String status) {
        if (status == null || status.isBlank()) {
            return true;
        }
        return status.equals(exam.getStatus());
    }

    private ExamQueryDTO buildStatusAgnosticQuery(ExamQueryDTO queryDTO) {
        ExamQueryDTO query = new ExamQueryDTO();
        query.setPageNum(1);
        query.setPageSize(Integer.MAX_VALUE);
        query.setExamName(queryDTO.getExamName());
        return query;
    }

    private PageResult<ExamVO> buildPagedResult(List<Exam> exams, ExamQueryDTO queryDTO) {
        int fromIndex = Math.max((queryDTO.getPageNum() - 1) * queryDTO.getPageSize(), 0);
        if (fromIndex >= exams.size()) {
            return new PageResult<>(Collections.emptyList(), (long) exams.size(), queryDTO.getPageNum(), queryDTO.getPageSize());
        }
        int toIndex = Math.min(fromIndex + queryDTO.getPageSize(), exams.size());
        List<ExamVO> records = exams.subList(fromIndex, toIndex).stream().map(this::toVO).toList();
        return new PageResult<>(records, (long) exams.size(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    private ExamVO toVO(Exam exam) {
        return toVO(exam, List.of());
    }

    private ExamVO toVO(Exam exam, List<Long> studentIds) {
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
                .studentIds(studentIds)
                .build();
    }
}
