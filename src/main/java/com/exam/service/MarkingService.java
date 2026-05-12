package com.exam.service;

import com.exam.common.result.PageResult;
import com.exam.dto.marking.MarkingQueryDTO;
import com.exam.dto.marking.MarkingScoreDTO;
import com.exam.vo.marking.MarkingVO;

public interface MarkingService {

    PageResult<MarkingVO> pendingPage(MarkingQueryDTO queryDTO);

    MarkingVO detail(Long examId, Long studentId);

    void markQuestion(Long answerId, MarkingScoreDTO markingScoreDTO);

    void finishMarking(Long examId, Long studentId);
}
