package com.exam.service;

import com.exam.common.result.PageResult;
import com.exam.dto.exam.ExamQueryDTO;
import com.exam.dto.exam.ExamSaveDTO;
import com.exam.vo.exam.ExamVO;

public interface ExamService {

    PageResult<ExamVO> page(ExamQueryDTO queryDTO);

    PageResult<ExamVO> myPage(ExamQueryDTO queryDTO);

    ExamVO detail(Long id);

    void save(ExamSaveDTO examSaveDTO);

    void publish(Long id);

    void publishScore(Long id);

    void remove(Long id);
}
