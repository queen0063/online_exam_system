package com.exam.dto.score;

import com.exam.dto.common.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 成绩查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScoreQueryDTO extends PageQueryDTO {

    private Long examId;
    private String keyword;
}
