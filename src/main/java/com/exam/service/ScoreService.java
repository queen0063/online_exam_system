package com.exam.service;

import com.exam.common.result.PageResult;
import com.exam.dto.score.ScoreQueryDTO;
import com.exam.vo.score.ScoreStatisticsVO;
import com.exam.vo.score.ScoreVO;
import java.util.List;

public interface ScoreService {

    PageResult<ScoreVO> page(ScoreQueryDTO queryDTO);

    PageResult<ScoreVO> myScores(ScoreQueryDTO queryDTO);

    ScoreVO detail(Long examId, Long studentId);

    ScoreStatisticsVO statistics(Long examId);

    List<ScoreVO> ranking(Long examId);

    byte[] export(Long examId);
}
