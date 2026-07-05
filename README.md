# 🧠 TopicMaster AI

> An AI-powered learning platform that teaches you any topic, quizzes your knowledge, and prepares you for interviews — all in one flow.

Built with **Spring Boot** + **Spring AI** + **Groq's LLaMA 3.3** model. No database. No frontend framework. Just clean Java serving a beautiful dark-themed UI.

---

## ✨ Features

| Feature | Description |
|---|---|
| 📖 **Deep Lessons** | AI breaks your topic into subtopics, each with detailed explanations, analogies & examples |
| 🧭 **Tabbed Navigation** | Browse subtopics one at a time with Previous/Next controls and a progress tracker |
| ❓ **10-Question MCQ Quiz** | Auto-graded instantly with correct answer highlighting |
| 💡 **MCQ Explanations** | Each question reveals *why* the correct answer is right after submission |
| 🎤 **5 Interview Questions** | AI grades your open-ended answers and gives personalized feedback |
| 🎨 **Modern Dark UI** | Glassmorphism design with smooth animations, no external CSS framework |

---

## 🏗️ Architecture

```
Browser (index.html)
    │
    │  POST /api/teach        → AiService.teach()       → Lesson (topic + subtopics[])
    │  POST /api/quiz         → AiService.generateQuiz() → QuizSet (10 MCQs + 5 interview Qs)
    │  POST /api/submit-mcq   → TopicController         → Score + Explanation per question
    │  POST /api/submit-interview → AiService.gradeInterviewAnswer() → AI Feedback
    │
Spring Boot (REST API + Static HTML)
    │
Groq API (LLaMA 3.3 70B via OpenAI-compatible endpoint)
```

**Key Spring AI trick:** `.entity(Lesson.class)` — Spring AI automatically appends JSON format instructions to the prompt and deserializes the LLM's JSON response directly into Java records. Zero manual parsing.

---

## 🚀 Quick Start (Local)

### Prerequisites
- JDK 17 or higher → `java -version`
- Maven → `mvn -version`
- A free Groq API key from [console.groq.com](https://console.groq.com)

### Run it

**Windows (PowerShell):**
```powershell
$env:GROQ_API_KEY="your_key_here"
cd topicmaster-ai   # make sure you're in the folder with pom.xml
mvn spring-boot:run
```

**Mac/Linux:**
```bash
export GROQ_API_KEY=your_key_here
mvn spring-boot:run
```

Open your browser at **http://localhost:8080**, type a topic (e.g. "Java Threads"), and click **Teach Me** 🎓

> ⚠️ If the app fails to start, 99% of the time it's the API key not being set. Double-check the step above.

---

## 📁 Project Structure

```
src/main/java/com/topicmaster/
├── controller/
│   └── TopicController.java      # REST endpoints: /api/teach, /api/quiz, /api/submit-*
├── model/
│   ├── Lesson.java               # record: topic + List<SubTopic>
│   ├── SubTopic.java             # record: title + content (one per tab)
│   ├── MCQ.java                  # record: question, options, correctAnswer, explanation
│   ├── QuizSet.java              # record: List<MCQ> + List<InterviewQuestion>
│   └── InterviewQuestion.java    # record: question + modelAnswer
├── service/
│   └── AiService.java            # The AI agent brain: teach → generateQuiz → grade
└── store/
    └── SessionStore.java         # In-memory store (V2: add JPA + user sessions)

src/main/resources/static/
└── index.html                    # Full UI: dark theme, tabs, quiz, interview — no framework
```

---

## 🌐 Deploy to Render (Free Hosting)

This project ships with a `Dockerfile` so deployment is just a few clicks:

1. Push your code to a GitHub repository
2. Go to [render.com](https://render.com) → **New +** → **Web Service**
3. Connect your GitHub repository
4. Render auto-detects the `Dockerfile` — select **Docker** runtime
5. Under **Environment Variables**, add:
   - **Key:** `GROQ_API_KEY`
   - **Value:** *(your Groq API key)*
6. Click **Create Web Service** and wait ~3 minutes for the first build

You'll get a live URL like `https://topicmaster-ai.onrender.com` 🎉

> **Note:** Render's free tier spins down after inactivity. The first request after a sleep takes ~30-60 seconds to wake up.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.3 |
| AI Integration | Spring AI 1.0.6 |
| LLM | Groq — LLaMA 3.3 70B Versatile |
| Frontend | Vanilla HTML + CSS + JavaScript |
| Containerization | Docker |

---

## 💬 Interview Talking Points

- **Agentic pipeline:** "Teach → Quiz grounded in lesson → Grade" — not a single prompt wrapper
- **Structured output:** `BeanOutputConverter` via `.entity()` converts raw LLM JSON directly into Java records
- **Context control:** Quiz generation is strictly grounded in lesson subtopics to prevent hallucination
- **Planned V2 improvements:** Persistence (JPA + PostgreSQL), multi-user sessions, re-teach loop for wrong answers

---

## 📄 License

MIT License — feel free to use, fork, and extend!
