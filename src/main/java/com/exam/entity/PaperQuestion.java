package com.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试卷题目关联实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaperQuestion extends BaseEntity {

    private Long paperId;
    private Long questionId;
    private Integer questionScore;
    private Integer sortNo;
}
