package com.topicmaster.model;

import java.util.List;

public record MCQ(String question, List<String> options, String correctAnswer, String explanation) {}
