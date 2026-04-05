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
import com.exam.mapper.ExamStudentMapper;
import com.exam.mapper.PaperMapper;
import com.exam.security.context.SecurityContextUtils;
import com.exam.service.ExamService;
import com.exam.vo.exam.ExamVO;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public ExamServiceImpl(ExamMapper examMapper, ExamStudentMapper examStudentMapper, PaperMapper paperMapper) {
        this.examMapper = examMapper;
        this.examStudentMapper = examStudentMapper;
        this.paperMapper = paperMapper;
    }

    @Override
    public PageResult<ExamVO> page(ExamQueryDTO queryDTO) {
        boolean admin = SecurityContextUtils.hasAnyRole("ADMIN");
        List<ExamVO> records = examMapper.selectPage(queryDTO, SecurityContextUtils.getUserId(), admin).stream()
                .map(this::toVO)
                .toList();
        Long total = examMapper.selectCount(queryDTO, SecurityContextUtils.getUserId(), admin);
        return new PageResult<>(records, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public PageResult<ExamVO> myPage(ExamQueryDTO queryDTO) {
        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        List<ExamVO> records = examMapper
                .selectStudentExamPage(SecurityContextUtils.getUserId(), queryDTO.getStatus(), offset, queryDTO.getPageSize())
                .stream()
                .map(this::toVO)
                .toList();
        Long total = examMapper.selectStudentExamCount(SecurityContextUtils.getUserId(), queryDTO.getStatus());
        return new PageResult<>(records, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public ExamVO detail(Long id) {
        return toVO(getExam(id));
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
        exam.setStatus(LocalDateTime.now().isBefore(examSaveDTO.getStartTime())
                ? ExamStatusEnum.NOT_STARTED.name()
                : ExamStatusEnum.IN_PROGRESS.name());
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
        exam.setStatus(LocalDateTime.now().isBefore(exam.getStartTime())
                ? ExamStatusEnum.NOT_STARTED.name()
                : ExamStatusEnum.IN_PROGRESS.name());
        exam.setUpdateTime(LocalDateTime.now());
        examMapper.updateById(exam);
    }

    @Override
    public void publishScore(Long id) {
        Exam exam = getExam(id);
        enforceOwnExam(exam);
        exam.setResultPublished(1);
        exam.setStatus(ExamStatusEnum.RESULT_PUBLISHED.name());
        exam.setUpdateTime(LocalDateTime.now());
        examMapper.updateById(exam);
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
        return exam;
    }

    private void enforceOwnExam(Exam exam) {
        if (!SecurityContextUtils.hasAnyRole("ADMIN") && !exam.getCreatorId().equals(SecurityContextUtils.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "教师只能操作自己创建的考试");
        }
    }

    private ExamVO toVO(Exam exam) {
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
