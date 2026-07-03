package com.exam.mapper;

import com.exam.dto.score.ScoreQueryDTO;
import com.exam.dto.marking.MarkingQueryDTO;
import com.exam.entity.ExamScore;
import com.exam.vo.marking.MarkingVO;
import com.exam.vo.score.ScoreSegmentVO;
import com.exam.vo.score.ScoreVO;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ExamScoreMapper {

    ExamScore selectByExamIdAndStudentId(@Param("examId") Long examId, @Param("studentId") Long studentId);

    List<MarkingVO> selectPendingMarkingPage(@Param("query") MarkingQueryDTO queryDTO, @Param("teacherId") Long teacherId, @Param("offset") Integer offset, @Param("admin") boolean admin);

    Long countPendingMarking(@Param("query") MarkingQueryDTO queryDTO, @Param("teacherId") Long teacherId, @Param("admin") boolean admin);

    List<ScoreVO> selectScorePage(@Param("query") ScoreQueryDTO queryDTO, @Param("teacherId") Long teacherId, @Param("admin") boolean admin);

    Long selectScoreCount(@Param("query") ScoreQueryDTO queryDTO, @Param("teacherId") Long teacherId, @Param("admin") boolean admin);

    List<ScoreVO> selectMyScorePage(@Param("query") ScoreQueryDTO queryDTO, @Param("studentId") Long studentId);

    Long selectMyScoreCount(@Param("query") ScoreQueryDTO queryDTO, @Param("studentId") Long studentId);

    List<ScoreSegmentVO> selectSegmentStatistics(@Param("examId") Long examId);

    Integer selectPassCount(@Param("examId") Long examId);

    Integer selectExcellentCount(@Param("examId") Long examId);

    Integer selectTotalCount(@Param("examId") Long examId);

    List<ScoreVO> selectRanking(@Param("examId") Long examId);

    Long countUnmarkedByExamId(@Param("examId") Long examId);

    int insert(ExamScore examScore);

    int updateById(ExamScore examScore);

    int updateRankByExamId(@Param("examId") Long examId);

    int updatePublishTimeByExamId(@Param("examId") Long examId, @Param("publishTime") LocalDateTime publishTime);
}
