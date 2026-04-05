package com.exam.service;

import com.exam.entity.Question;
import java.util.List;

public interface ScoringService {

    Integer scoreObjectiveQuestion(Question question, List<String> studentAnswers, Integer questionScore);

    boolean isObjectiveQuestion(String questionType);
}
