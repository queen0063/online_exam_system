package com.exam.dto.exam;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * 考试保存参数。
 */
@Data
public class ExamSaveDTO {

    private Long id;

    @NotBlank(message = "考试名称不能为空")
    private String examName;

    @NotNull(message = "试卷不能为空")
    private Long paperId;

    @NotNull(message = "科目不能为空")
    private Long subjectId;

    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @NotNull(message = "考试时长不能为空")
    private Integer durationMinutes;

    @NotNull(message = "及格线不能为空")
    private Integer passScore;

    @NotEmpty(message = "参与学生不能为空")
    private List<Long> studentIds;
}
