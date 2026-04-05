package com.exam.dto.exam;

import com.exam.dto.common.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考试查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExamQueryDTO extends PageQueryDTO {

    private String examName;
    private String status;
}
