package com.topicmaster.controller;

import com.topicmaster.model.InterviewQuestion;
import com.topicmaster.model.Lesson;
import com.topicmaster.model.MCQ;
import com.topicmaster.model.QuizSet;
import com.topicmaster.service.AiService;
import com.topicmaster.store.SessionStore;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TopicController {

    private final AiService aiService;
    private final SessionStore store;

    public TopicController(AiService aiService, SessionStore store) {
        this.aiService = aiService;
        this.store = store;
    }

    @PostMapping("/teach")
    public Lesson teach(@RequestBody Map<String, String> body) {
        String topic = body.get("topic");
        Lesson lesson = aiService.teach(topic);
        store.setCurrentLesson(lesson);
        return lesson;
    }

    @PostMapping("/quiz")
    public QuizSet quiz() {
        Lesson lesson = store.getCurrentLesson();
        if (lesson == null) {
            throw new IllegalStateException("Teach a topic first via /api/teach");
        }
        QuizSet quiz = aiService.generateQuiz(lesson);
        store.setCurrentQuiz(quiz);
        return quiz;
    }

    @PostMapping("/submit-mcq")
    public Map<String, Object> submitMcq(@RequestBody Map<String, String> answers) {
        QuizSet quiz = store.getCurrentQuiz();
        List<MCQ> mcqs = quiz.mcqs();
        int score = 0;
        List<Map<String, String>> results = new ArrayList<>();

        for (int i = 0; i < mcqs.size(); i++) {
            MCQ mcq = mcqs.get(i);
            String userAnswer = answers.get(String.valueOf(i));
            boolean correct = mcq.correctAnswer().equalsIgnoreCase(
                    userAnswer == null ? "" : userAnswer.trim());
            if (correct) score++;

            Map<String, String> r = new HashMap<>();
            r.put("question", mcq.question());
            r.put("yourAnswer", userAnswer);
            r.put("correctAnswer", mcq.correctAnswer());
            r.put("result", correct ? "correct" : "wrong");
            r.put("explanation", mcq.explanation() != null ? mcq.explanation() : "No explanation provided.");
            results.add(r);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("score", score);
        response.put("total", mcqs.size());
        response.put("details", results);
        return response;
    }

    @PostMapping("/submit-interview")
    public List<Map<String, String>> submitInterview(@RequestBody Map<String, String> answers) {
        QuizSet quiz = store.getCurrentQuiz();
        List<InterviewQuestion> questions = quiz.interviewQuestions();
        List<Map<String, String>> feedbackList = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            InterviewQuestion q = questions.get(i);
            String userAnswer = answers.getOrDefault(String.valueOf(i), "");
            String feedback = aiService.gradeInterviewAnswer(q, userAnswer);

            Map<String, String> r = new HashMap<>();
            r.put("question", q.question());
            r.put("yourAnswer", userAnswer);
            r.put("feedback", feedback);
            feedbackList.add(r);
        }
        return feedbackList;
    }
}
