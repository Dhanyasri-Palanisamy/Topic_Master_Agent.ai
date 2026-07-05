package com.topicmaster.model;

import java.util.List;

public record QuizSet(List<MCQ> mcqs, List<InterviewQuestion> interviewQuestions) {}
