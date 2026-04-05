package com.exam.vo.score;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * 成绩返回对象。
 */
@Data
@Builder
public class ScoreVO {

    private Long id;
    private Long examId;
    private String examName;
    private Long studentId;
    private String studentName;
    private Integer objectiveScore;
    private Integer subjectiveScore;
    private Integer totalScore;
    private Integer passFlag;
    private Integer excellentFlag;
    private Integer rankNo;
    private String scoreStatus;
    private LocalDateTime publishTime;
}
