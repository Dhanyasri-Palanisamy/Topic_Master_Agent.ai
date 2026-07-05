package com.topicmaster.model;

import java.util.List;

// Restructured: instead of a flat "content" string, each subtopic now carries
// its own deep content. The frontend renders these as clickable tabs.
public record Lesson(String topic, List<SubTopic> subtopics) {}
