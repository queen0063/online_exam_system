package com.exam.dto.question;

import com.exam.dto.common.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 题目查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionQueryDTO extends PageQueryDTO {

    private Long subjectId;
    private String questionType;
    private String difficulty;
    private String knowledgePoint;
    private String keyword;
}
