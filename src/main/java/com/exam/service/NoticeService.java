package com.exam.service;

import com.exam.common.result.PageResult;
import com.exam.dto.notice.NoticeQueryDTO;
import com.exam.dto.notice.NoticeSaveDTO;
import com.exam.vo.notice.NoticeVO;

public interface NoticeService {

    PageResult<NoticeVO> page(NoticeQueryDTO queryDTO);

    NoticeVO detail(Long id);

    void save(NoticeSaveDTO noticeSaveDTO);

    void remove(Long id);
}
