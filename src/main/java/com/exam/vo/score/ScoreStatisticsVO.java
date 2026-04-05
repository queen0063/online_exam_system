package com.exam.vo.score;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * 成绩统计返回对象。
 */
@Data
@Builder
public class ScoreStatisticsVO {

    private Long examId;
    private Integer totalCount;
    private Integer passCount;
    private Integer excellentCount;
    private Double passRate;
    private Double excellentRate;
    private List<ScoreSegmentVO> scoreSegments;
}
