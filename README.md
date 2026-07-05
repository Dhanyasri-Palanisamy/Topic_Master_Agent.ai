# TopicMaster AI — Step by Step (Setup → Deployment)

## What this project does
1. You give a topic → agent teaches it (`/api/teach`)
2. You click "Generate Quiz" → agent makes 10 MCQs + 5 interview questions **from that lesson** (`/api/quiz`)
3. You answer MCQs → auto-graded instantly (`/api/submit-mcq`)
4. You answer interview questions → agent grades + gives feedback (`/api/submit-interview`)

No database. No React. Just Spring Boot serving both the API and a plain HTML page. One deployment, done.

---

## STEP 1 — Get a free Groq API key
Groq gives free, fast LLM access, OpenAI-API-compatible (so Spring AI's OpenAI starter works with it directly).

1. Go to https://console.groq.com
2. Sign up / log in
3. Go to "API Keys" → Create key → copy it
4. Keep it safe, you'll need it twice (local run + Render)

---

## STEP 2 — Install prerequisites (if not already)
- JDK 17 or higher — check with `java -version`
- Maven — check with `mvn -version`
- (Optional) IntelliJ / VS Code with Java extensions

---

## STEP 3 — Run it locally
1. Unzip the project
2. Open a terminal in the project folder
3. Set your API key as an environment variable:

   **Windows (cmd):**
   ```
   set GROQ_API_KEY=your_key_here
   ```
   **Mac/Linux:**
   ```
   export GROQ_API_KEY=your_key_here
   ```
4. Run:
   ```
   mvn spring-boot:run
   ```
5. Open your browser at `http://localhost:8080`
6. Type a topic (e.g. "JDBC"), click "Teach me", read the lesson, click "Generate Quiz", answer, submit.

If it errors on startup, 99% of the time it's the API key not being set — double check step 3.

---

## STEP 4 — Understand the code (read this before your interview!)
- `AiService.java` — the actual "agent" brain. 3 methods = 3 agent steps: teach → generateQuiz → gradeInterviewAnswer
- `.entity(Lesson.class)` — this is the important bit to explain in interviews. Spring AI auto-adds
  formatting instructions to the prompt and parses the LLM's JSON response directly into your Java object.
  No manual JSON parsing, no regex.
- `TopicController.java` — thin REST layer, just wires HTTP requests to the AiService
- `SessionStore.java` — deliberately simple in-memory store (mention as a "V2 improvement: add DB + user sessions" in interviews)
- `index.html` — plain JS with `fetch()` calls, no framework needed

---

## STEP 5 — Push to GitHub
```
git init
git add .
git commit -m "TopicMaster AI - initial version"
```
Create a new repo on GitHub, then:
```
git remote add origin https://github.com/YOUR_USERNAME/topicmaster-ai.git
git branch -M main
git push -u origin main
```

---

## STEP 6 — Deploy to Render
1. Go to https://render.com → sign up/log in with GitHub
2. Click "New +" → "Web Service"
3. Connect your GitHub repo (topicmaster-ai)
4. Render will detect the `Dockerfile` automatically — select "Docker" as the environment
5. Under "Environment Variables", add:
   - Key: `GROQ_API_KEY`
   - Value: your Groq key
6. Click "Create Web Service"
7. Wait for the build (first build takes a few minutes since Maven downloads dependencies)
8. Once live, Render gives you a URL like `https://topicmaster-ai.onrender.com` — open it, same UI as localhost

**Note:** Render's free tier spins down after inactivity and takes ~30-60 seconds to wake up on the next request. Totally fine for a demo/interview — just mention it if the interviewer notices a delay on first load.

---

## STEP 7 — What to say in interviews
- "It's an agentic pipeline — teach, generate assessment grounded in what was taught, grade, give feedback — not just a single prompt wrapper."
- "Used Spring AI's structured output (`BeanOutputConverter` under the hood via `.entity()`) to get reliable JSON instead of parsing raw LLM text."
- "Grounded the quiz generation strictly in the lesson content to avoid the LLM asking about unrelated things — a basic form of context control."
- Mention planned improvements: persistence (JPA + DB), the re-teach loop for wrong answers, multi-user sessions.
