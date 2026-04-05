package com.exam.vo.paper;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * 试卷返回对象。
 */
@Data
@Builder
public class PaperVO {

    private Long id;
    private String paperName;
    private Long subjectId;
    private String generateType;
    private Integer totalScore;
    private Integer durationMinutes;
    private Long creatorId;
    private String description;
    private List<PaperQuestionVO> questions;
    private LocalDateTime createTime;
}
