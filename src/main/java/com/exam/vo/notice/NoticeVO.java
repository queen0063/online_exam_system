package com.exam.vo.notice;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * 公告返回对象。
 */
@Data
@Builder
public class NoticeVO {

    private Long id;
    private String title;
    private String content;
    private String noticeStatus;
    private Long publisherId;
    private LocalDateTime createTime;
}
