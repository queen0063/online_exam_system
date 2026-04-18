package com.exam.vo.marking;

import com.exam.vo.answer.AnswerVO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 阅卷详情返回对象。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkingVO {

    private Long examId;
    private String examName;
    private Long studentId;
    private String studentName;
    private Integer objectiveScore;
    private Integer subjectiveScore;
    private Integer totalScore;
    private String scoreStatus;
    private List<AnswerVO> answers;
}
