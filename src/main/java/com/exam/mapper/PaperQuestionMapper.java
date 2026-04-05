package com.exam.mapper;

import com.exam.entity.PaperQuestion;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaperQuestionMapper {

    List<PaperQuestion> selectByPaperId(@Param("paperId") Long paperId);

    int batchInsert(@Param("list") List<PaperQuestion> list);

    int deleteByPaperId(@Param("paperId") Long paperId);
}
