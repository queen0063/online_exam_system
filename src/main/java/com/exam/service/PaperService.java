package com.exam.service;

import com.exam.common.result.PageResult;
import com.exam.dto.paper.PaperQueryDTO;
import com.exam.dto.paper.PaperSaveDTO;
import com.exam.vo.paper.PaperVO;

public interface PaperService {

    PageResult<PaperVO> page(PaperQueryDTO queryDTO);

    PaperVO detail(Long id);

    void save(PaperSaveDTO paperSaveDTO);

    void remove(Long id);

    PaperVO preview(Long id);
}
