package com.exam.dto.paper;

import com.exam.dto.common.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试卷查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaperQueryDTO extends PageQueryDTO {

    private String paperName;
    private Long subjectId;
}
