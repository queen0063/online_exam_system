package com.exam.vo.exam;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 考试监测学生状态。
 */
@Data
public class ExamMonitorVO {

    private Long examId;
    private Long studentId;
    private String studentNo;
    private String studentName;
    private String className;
    private String gradeName;
    private String answerStatus;
    private Integer switchCount;
    private LocalDateTime startTime;
}
