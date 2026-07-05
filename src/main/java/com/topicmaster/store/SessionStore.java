package com.topicmaster.store;

import com.topicmaster.model.Lesson;
import com.topicmaster.model.QuizSet;
import org.springframework.stereotype.Component;

// NOTE: This is intentionally simple - one shared lesson/quiz "slot" in memory.
// Fine for a solo demo. If multiple people use it at once, they'd overwrite
// each other's session. That's a known limitation you can mention as a
// "future improvement: add DB + session/user ID" in interviews.
@Component
public class SessionStore {
    private Lesson currentLesson;
    private QuizSet currentQuiz;

    public Lesson getCurrentLesson() { return currentLesson; }
    public void setCurrentLesson(Lesson lesson) { this.currentLesson = lesson; }

    public QuizSet getCurrentQuiz() { return currentQuiz; }
    public void setCurrentQuiz(QuizSet quiz) { this.currentQuiz = quiz; }
}
