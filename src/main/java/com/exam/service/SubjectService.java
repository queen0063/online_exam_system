package com.exam.service;

import com.exam.dto.subject.SubjectSaveDTO;
import com.exam.vo.subject.SubjectVO;
import java.util.List;

/**
 * 科目服务。
 */
public interface SubjectService {

    List<SubjectVO> list();

    void save(SubjectSaveDTO subjectSaveDTO);

    void remove(Long id);
}
