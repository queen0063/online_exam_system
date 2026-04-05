package com.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Notice extends BaseEntity {

    private String title;
    private String content;
    private String noticeStatus;
    private Long publisherId;
}
