package com.exam.service;

import com.exam.dto.classinfo.ClassInfoSaveDTO;
import com.exam.vo.classinfo.ClassInfoVO;
import java.util.List;

public interface ClassInfoService {

    List<ClassInfoVO> listAll();

    List<ClassInfoVO> listVisible();

    void save(ClassInfoSaveDTO classInfoSaveDTO);

    void remove(Long id);
}
