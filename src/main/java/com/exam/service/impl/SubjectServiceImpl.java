package com.exam.service.impl;

import com.exam.common.api.ResultCode;
import com.exam.common.exception.BusinessException;
import com.exam.dto.subject.SubjectSaveDTO;
import com.exam.entity.Subject;
import com.exam.mapper.SubjectMapper;
import com.exam.service.SubjectService;
import com.exam.vo.subject.SubjectVO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 科目服务实现。
 */
@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectMapper subjectMapper;

    public SubjectServiceImpl(SubjectMapper subjectMapper) {
        this.subjectMapper = subjectMapper;
    }

    @Override
    public List<SubjectVO> list() {
        return subjectMapper.selectAll().stream().map(this::toVO).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(SubjectSaveDTO subjectSaveDTO) {
        Subject sameCode = subjectMapper.selectByCode(subjectSaveDTO.getSubjectCode());
        if (sameCode != null && !sameCode.getId().equals(subjectSaveDTO.getId())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "科目编码已存在");
        }
        Subject subject = subjectSaveDTO.getId() == null ? new Subject() : getSubject(subjectSaveDTO.getId());
        subject.setSubjectCode(subjectSaveDTO.getSubjectCode());
        subject.setSubjectName(subjectSaveDTO.getSubjectName());
        subject.setDescription(subjectSaveDTO.getDescription());
        subject.setStatus(subjectSaveDTO.getStatus());
        subject.setUpdateTime(LocalDateTime.now());
        if (subjectSaveDTO.getId() == null) {
            subject.setCreateTime(LocalDateTime.now());
            subject.setDeleted(0);
            subjectMapper.insert(subject);
        } else {
            subjectMapper.updateById(subject);
        }
    }

    @Override
    public void remove(Long id) {
        getSubject(id);
        subjectMapper.logicDeleteById(id);
    }

    private Subject getSubject(Long id) {
        Subject subject = subjectMapper.selectById(id);
        if (subject == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "科目不存在");
        }
        return subject;
    }

    private SubjectVO toVO(Subject subject) {
        return SubjectVO.builder()
                .id(subject.getId())
                .subjectCode(subject.getSubjectCode())
                .subjectName(subject.getSubjectName())
                .description(subject.getDescription())
                .status(subject.getStatus())
                .createTime(subject.getCreateTime())
                .build();
    }
}
