package com.exam.dto.notice;

import com.exam.dto.common.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NoticeQueryDTO extends PageQueryDTO {

    private String keyword;
    private String noticeStatus;
}
