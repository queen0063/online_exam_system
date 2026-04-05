package com.exam.vo.score;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分数段统计返回对象。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreSegmentVO {

    private String segmentName;
    private Integer count;
}
