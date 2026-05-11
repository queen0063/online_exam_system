package com.exam.vo.subject;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * 科目返回对象。
 */
@Data
@Builder
public class SubjectVO {

    private Long id;
    private String subjectCode;
    private String subjectName;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
}
