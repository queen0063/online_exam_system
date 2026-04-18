package com.exam.vo.exam;

import com.exam.vo.answer.AnswerVO;
import java.time.LocalDateTime;
import java.util.List;
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
    private List<Long> studentIds;
    private List<AnswerVO> questions;
}
