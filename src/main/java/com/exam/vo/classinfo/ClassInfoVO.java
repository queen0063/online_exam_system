package com.exam.vo.classinfo;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * 班级返回对象。
 */
@Data
@Builder
public class ClassInfoVO {

    private Long id;
    private String classCode;
    private String className;
    private String gradeName;
    private Long teacherId;
    private Integer status;
    private LocalDateTime createTime;
}
