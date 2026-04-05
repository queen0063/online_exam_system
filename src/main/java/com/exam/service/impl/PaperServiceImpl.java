package com.exam.service.impl;

import com.exam.common.api.ResultCode;
import com.exam.common.enums.PaperGenerateTypeEnum;
import com.exam.common.exception.BusinessException;
import com.exam.common.result.PageResult;
import com.exam.common.util.JsonUtils;
import com.exam.dto.paper.PaperQueryDTO;
import com.exam.dto.paper.PaperQuestionDTO;
import com.exam.dto.paper.PaperSaveDTO;
import com.exam.dto.paper.RandomRuleDTO;
import com.exam.entity.Paper;
import com.exam.entity.PaperQuestion;
import com.exam.entity.Question;
import com.exam.mapper.PaperMapper;
import com.exam.mapper.PaperQuestionMapper;
import com.exam.mapper.QuestionMapper;
import com.exam.security.context.SecurityContextUtils;
import com.exam.service.PaperService;
import com.exam.vo.paper.PaperQuestionVO;
import com.exam.vo.paper.PaperVO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 试卷服务实现。
 */
@Service
public class PaperServiceImpl implements PaperService {

    private final PaperMapper paperMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final QuestionMapper questionMapper;

    public PaperServiceImpl(PaperMapper paperMapper, PaperQuestionMapper paperQuestionMapper, QuestionMapper questionMapper) {
        this.paperMapper = paperMapper;
        this.paperQuestionMapper = paperQuestionMapper;
        this.questionMapper = questionMapper;
    }

    @Override
    public PageResult<PaperVO> page(PaperQueryDTO queryDTO) {
        boolean admin = SecurityContextUtils.hasAnyRole("ADMIN");
        Long creatorId = SecurityContextUtils.getUserId();
        List<PaperVO> records = paperMapper.selectPage(queryDTO, creatorId, admin).stream().map(this::toVO).toList();
        Long total = paperMapper.selectCount(queryDTO, creatorId, admin);
        return new PageResult<>(records, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public PaperVO detail(Long id) {
        return toVO(getPaper(id));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(PaperSaveDTO paperSaveDTO) {
        List<PaperQuestionDTO> paperQuestions = buildPaperQuestions(paperSaveDTO);
        Integer totalScore = paperQuestions.stream().map(PaperQuestionDTO::getQuestionScore).reduce(0, Integer::sum);
        Paper paper = paperSaveDTO.getId() == null ? new Paper() : getPaper(paperSaveDTO.getId());
        if (paperSaveDTO.getId() != null) {
            enforceOwnPaper(paper);
        }
        paper.setPaperName(paperSaveDTO.getPaperName());
        paper.setSubjectId(paperSaveDTO.getSubjectId());
        paper.setGenerateType(paperSaveDTO.getGenerateType());
        paper.setTotalScore(totalScore);
        paper.setDurationMinutes(paperSaveDTO.getDurationMinutes());
        paper.setCreatorId(SecurityContextUtils.getUserId());
        paper.setDescription(paperSaveDTO.getDescription());
        paper.setUpdateTime(LocalDateTime.now());
        if (paperSaveDTO.getId() == null) {
            paper.setCreateTime(LocalDateTime.now());
            paper.setDeleted(0);
            paperMapper.insert(paper);
        } else {
            paperMapper.updateById(paper);
            paperQuestionMapper.deleteByPaperId(paper.getId());
        }
        savePaperQuestions(paper.getId(), paperQuestions);
    }

    @Override
    public void remove(Long id) {
        Paper paper = getPaper(id);
        enforceOwnPaper(paper);
        paperMapper.logicDeleteById(id);
        paperQuestionMapper.deleteByPaperId(id);
    }

    @Override
    public PaperVO preview(Long id) {
        return toVO(getPaper(id));
    }

    private List<PaperQuestionDTO> buildPaperQuestions(PaperSaveDTO paperSaveDTO) {
        if (PaperGenerateTypeEnum.MANUAL.name().equals(paperSaveDTO.getGenerateType())) {
            if (paperSaveDTO.getQuestions() == null || paperSaveDTO.getQuestions().isEmpty()) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "手动组卷必须选择题目");
            }
            validateDuplicateQuestion(paperSaveDTO.getQuestions());
            return paperSaveDTO.getQuestions();
        }
        if (paperSaveDTO.getRandomRules() == null || paperSaveDTO.getRandomRules().isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "随机组卷规则不能为空");
        }
        List<PaperQuestionDTO> result = new ArrayList<>();
        int sortNo = 1;
        for (RandomRuleDTO randomRule : paperSaveDTO.getRandomRules()) {
            List<Question> questions = questionMapper.selectByRule(
                    randomRule.getSubjectId(),
                    randomRule.getQuestionType(),
                    randomRule.getDifficulty(),
                    randomRule.getKnowledgePoint(),
                    randomRule.getQuestionCount());
            if (questions.size() < randomRule.getQuestionCount()) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "随机组卷题目数量不足");
            }
            for (Question question : questions) {
                PaperQuestionDTO dto = new PaperQuestionDTO();
                dto.setQuestionId(question.getId());
                dto.setQuestionScore(randomRule.getQuestionScore());
                dto.setSortNo(sortNo++);
                result.add(dto);
            }
        }
        validateDuplicateQuestion(result);
        return result;
    }

    private void validateDuplicateQuestion(List<PaperQuestionDTO> questions) {
        Set<Long> questionIds = new HashSet<>();
        for (PaperQuestionDTO item : questions) {
            if (!questionIds.add(item.getQuestionId())) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "试卷中不能出现重复题目");
            }
        }
    }

    private void savePaperQuestions(Long paperId, List<PaperQuestionDTO> questions) {
        List<PaperQuestion> list = new ArrayList<>();
        for (PaperQuestionDTO item : questions) {
            PaperQuestion paperQuestion = new PaperQuestion();
            paperQuestion.setPaperId(paperId);
            paperQuestion.setQuestionId(item.getQuestionId());
            paperQuestion.setQuestionScore(item.getQuestionScore());
            paperQuestion.setSortNo(item.getSortNo());
            paperQuestion.setCreateTime(LocalDateTime.now());
            paperQuestion.setUpdateTime(LocalDateTime.now());
            paperQuestion.setDeleted(0);
            list.add(paperQuestion);
        }
        if (!list.isEmpty()) {
            paperQuestionMapper.batchInsert(list);
        }
    }

    private Paper getPaper(Long id) {
        Paper paper = paperMapper.selectById(id);
        if (paper == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "试卷不存在");
        }
        return paper;
    }

    private void enforceOwnPaper(Paper paper) {
        if (!SecurityContextUtils.hasAnyRole("ADMIN") && !paper.getCreatorId().equals(SecurityContextUtils.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "教师只能操作自己创建的试卷");
        }
    }

    private PaperVO toVO(Paper paper) {
        List<PaperQuestionVO> questionVOList = paperQuestionMapper.selectByPaperId(paper.getId()).stream()
                .sorted(Comparator.comparing(PaperQuestion::getSortNo))
                .map(item -> {
                    Question question = questionMapper.selectById(item.getQuestionId());
                    return PaperQuestionVO.builder()
                            .questionId(item.getQuestionId())
                            .questionScore(item.getQuestionScore())
                            .sortNo(item.getSortNo())
                            .questionType(question.getQuestionType())
                            .title(question.getTitle())
                            .options(JsonUtils.toStringList(question.getOptionsJson()))
                            .answers(JsonUtils.toStringList(question.getAnswerJson()))
                            .analysis(question.getAnalysis())
                            .build();
                })
                .toList();
        return PaperVO.builder()
                .id(paper.getId())
                .paperName(paper.getPaperName())
                .subjectId(paper.getSubjectId())
                .generateType(paper.getGenerateType())
                .totalScore(paper.getTotalScore())
                .durationMinutes(paper.getDurationMinutes())
                .creatorId(paper.getCreatorId())
                .description(paper.getDescription())
                .questions(questionVOList)
                .createTime(paper.getCreateTime())
                .build();
    }
}
