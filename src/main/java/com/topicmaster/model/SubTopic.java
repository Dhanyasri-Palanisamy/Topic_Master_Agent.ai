package com.topicmaster.model;

import com.fasterxml.jackson.annotation.JsonAlias;

// Each subtopic has its own dedicated title and detailed content.
// This lets the frontend show one subtopic at a time in a tabbed view.
public record SubTopic(
    @JsonAlias({"title", "Title", "TITLE"}) String title, 
    @JsonAlias({"content", "Content", "CONTENT"}) String content
) {}
