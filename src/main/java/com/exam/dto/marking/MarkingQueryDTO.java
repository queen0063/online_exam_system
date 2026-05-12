package com.exam.dto.marking;

import com.exam.dto.common.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 待阅卷查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MarkingQueryDTO extends PageQueryDTO {

    private Long examId;
}
