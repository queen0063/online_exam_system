package com.exam.vo.exam;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * 考试返回对象。
 */
@Data
@Builder
public class ExamVO {

    private Long id;
    private String examName;
    private Long paperId;
    private Long subjectId;
    private Long creatorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private Integer passScore;
    private String status;
    private Integer resultPublished;
}
