package com.topicmaster.service;

import com.topicmaster.model.InterviewQuestion;
import com.topicmaster.model.Lesson;
import com.topicmaster.model.QuizSet;
import com.topicmaster.model.SubTopic;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AiService {

    private final ChatClient chatClient;

    // Spring auto-injects ChatClient.Builder because we added the spring-ai-openai starter.
    public AiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    // STEP 1 of the agent: TEACH (deep, per-subtopic content)
    public Lesson teach(String topic) {
        String prompt = """
                You are an expert tutor. Teach the topic "%s" to a beginner Java Full Stack
                developer preparing for placement interviews.

                Break it into 5-7 clear subtopics. For EACH subtopic, provide a DETAILED
                explanation in its own "content" field that includes:
                - A thorough explanation (8-12 sentences minimum)
                - Real-world analogies or scenarios to aid understanding
                - At least one concrete code example or practical illustration where applicable
                - Key points an interviewer would expect the candidate to know
                - Common mistakes or misconceptions to avoid

                Make each subtopic self-contained so a student can read them one at a time
                and deeply understand that specific concept before moving on.

                The "subtopics" array should contain objects, each with a "title" (short name)
                and "content" (the detailed explanation for that subtopic).
                CRITICAL: The JSON keys MUST be exactly lowercase "title" and "content".
                """.formatted(topic);

        // .entity(Lesson.class) is the key trick: Spring AI appends format instructions
        // to the prompt and parses the model's JSON reply straight into a Lesson object.
        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(Lesson.class);
    }

    // STEP 2 of the agent: GENERATE QUIZ (grounded in the lesson we just taught)
    public QuizSet generateQuiz(Lesson lesson) {
        // Build combined content from all subtopics for quiz grounding
        String combinedContent = lesson.subtopics().stream()
                .map(st -> st.title() + ":\n" + st.content())
                .collect(Collectors.joining("\n\n"));

        String prompt = """
                Based ONLY on the following lesson content, generate:
                - Exactly 10 multiple choice questions (4 options each, one correct answer,
                  correctAnswer must exactly match one of the options text).
                - For each MCQ, you MUST provide an "explanation" field that briefly explains why the correct answer is correct.
                - Exactly 5 interview-style open questions with a short ideal model answer.
                
                Do not ask about anything outside this lesson content.

                LESSON TOPIC: %s
                LESSON CONTENT:
                %s
                """.formatted(lesson.topic(), combinedContent);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(QuizSet.class);
    }

    // STEP 3 of the agent: RE-TEACH / GRADE open-ended interview answers
    public String gradeInterviewAnswer(InterviewQuestion question, String userAnswer) {
        String prompt = """
                Question: %s
                Ideal answer: %s
                Candidate's answer: %s

                As an interviewer, in 2-3 sentences: tell the candidate what they got right,
                what they missed, and correct any wrong statements. Be direct and specific.
                """.formatted(question.question(), question.idealAnswer(), userAnswer);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
