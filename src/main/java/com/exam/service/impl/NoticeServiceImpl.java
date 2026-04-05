package com.exam.service.impl;

import com.exam.common.api.ResultCode;
import com.exam.common.exception.BusinessException;
import com.exam.common.result.PageResult;
import com.exam.dto.notice.NoticeQueryDTO;
import com.exam.dto.notice.NoticeSaveDTO;
import com.exam.entity.Notice;
import com.exam.mapper.NoticeMapper;
import com.exam.security.context.SecurityContextUtils;
import com.exam.service.NoticeService;
import com.exam.vo.notice.NoticeVO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公告服务实现。
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

    public NoticeServiceImpl(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    @Override
    public PageResult<NoticeVO> page(NoticeQueryDTO queryDTO) {
        List<NoticeVO> records = noticeMapper.selectPage(queryDTO).stream().map(this::toVO).toList();
        return new PageResult<>(records, noticeMapper.selectCount(queryDTO), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public NoticeVO detail(Long id) {
        return toVO(getNotice(id));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(NoticeSaveDTO noticeSaveDTO) {
        Notice notice = noticeSaveDTO.getId() == null ? new Notice() : getNotice(noticeSaveDTO.getId());
        notice.setTitle(noticeSaveDTO.getTitle());
        notice.setContent(noticeSaveDTO.getContent());
        notice.setNoticeStatus(noticeSaveDTO.getNoticeStatus());
        notice.setPublisherId(SecurityContextUtils.getUserId());
        notice.setUpdateTime(LocalDateTime.now());
        if (noticeSaveDTO.getId() == null) {
            notice.setCreateTime(LocalDateTime.now());
            notice.setDeleted(0);
            noticeMapper.insert(notice);
        } else {
            noticeMapper.updateById(notice);
        }
    }

    @Override
    public void remove(Long id) {
        getNotice(id);
        noticeMapper.logicDeleteById(id);
    }

    private Notice getNotice(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "公告不存在");
        }
        return notice;
    }

    private NoticeVO toVO(Notice notice) {
        return NoticeVO.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .noticeStatus(notice.getNoticeStatus())
                .publisherId(notice.getPublisherId())
                .createTime(notice.getCreateTime())
                .build();
    }
}
