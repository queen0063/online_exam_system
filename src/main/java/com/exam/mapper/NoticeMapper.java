package com.exam.mapper;

import com.exam.dto.notice.NoticeQueryDTO;
import com.exam.entity.Notice;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NoticeMapper {

    Notice selectById(@Param("id") Long id);

    List<Notice> selectPage(NoticeQueryDTO queryDTO);

    Long selectCount(NoticeQueryDTO queryDTO);

    int insert(Notice notice);

    int updateById(Notice notice);

    int logicDeleteById(@Param("id") Long id);
}
