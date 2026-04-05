package com.exam.mapper;

import com.exam.dto.paper.PaperQueryDTO;
import com.exam.entity.Paper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaperMapper {

    Paper selectById(@Param("id") Long id);

    List<Paper> selectPage(@Param("query") PaperQueryDTO queryDTO, @Param("creatorId") Long creatorId, @Param("admin") boolean admin);

    Long selectCount(@Param("query") PaperQueryDTO queryDTO, @Param("creatorId") Long creatorId, @Param("admin") boolean admin);

    int insert(Paper paper);

    int updateById(Paper paper);

    int logicDeleteById(@Param("id") Long id);
}
