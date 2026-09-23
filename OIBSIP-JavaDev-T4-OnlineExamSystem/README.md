# Online Examination System (Java Swing)

A desktop GUI exam application built with plain Java Swing — no external
dependencies. Students log in, optionally update their profile, take a
timed multiple-choice exam, and view a scored result breakdown.

## How to Run

**Option A — run the prebuilt jar (Java 8+):**
```
java -jar OnlineExamSystem.jar
```

**Option B — compile from source:**
```
javac -d out src/examsystem/*.java
java -cp out examsystem.Main
```

## Demo Accounts

| Username | Password | Display Name  |
|----------|----------|----------------|
| student1 | pass123  | Alice Johnson  |
| student2 | pass123  | Brian Lee      |
| admin    | admin123 | Admin User     |

Passwords and display names can be changed on the Profile Update screen;
changes are kept in memory for the running session (see "Design Notes").

## Feature Walkthrough

1. **Login screen** — enter username/password. Invalid credentials show
   an inline error; Enter key in the password field also submits.
2. **Profile update screen** — change display name and/or password
   (optional), then click **Start Exam** to proceed as-is.
3. **Exam screen**
   - One question at a time, 4 radio-button options (`ButtonGroup` +
     `JRadioButton`), selections are remembered when navigating.
   - **Previous / Next** buttons move between questions.
   - A live countdown (`javax.swing.Timer`, 30:00 default) is always
     visible and turns red in the final minute.
   - Reaching **00:00** auto-submits the exam and shows a notice.
   - **Submit Exam** button asks for confirmation (and warns about
     unanswered questions) before submitting early.
   - Closing the window mid-exam (the frame's close button) prompts
     "Are you sure you want to quit?" via a `WindowListener`.
4. **Result screen** — shows score (X out of Y), time taken (mm:ss),
   and a color-coded table of every question with the user's answer,
   the correct answer, and Correct/Incorrect.
5. **Logout** button on the result screen returns to the Login screen
   and resets exam state.

## Project Structure

```
src/examsystem/
  Main.java              Application shell — CardLayout screen switching,
                          window-close confirmation
  User.java               User model (username, password, display name)
  UserStore.java           In-memory user "database" + authentication
  Question.java            MCQ model (text, 4 options, correct index)
  QuestionBank.java        Sample question set (swap in your own here)
  ExamSession.java          Captures answers + timing for scoring
  LoginPanel.java           Login screen
  ProfileUpdatePanel.java    Profile update screen
  ExamPanel.java             Exam-taking screen (timer, MCQs, nav, submit)
  ResultPanel.java            Result/breakdown screen
```

## Design Notes / Where to Extend

- **Persistence**: `UserStore` is an in-memory `HashMap`, so profile
  changes and scores are lost on exit. Swap it for file/DB-backed storage
  if you need persistence across runs.
- **Question bank**: `QuestionBank.getQuestions()` returns a hardcoded
  list — replace with a file/CSV/DB loader to make it configurable.
  Every question currently requires exactly 4 options (enforced in
  `Question`'s constructor); adjust there if you want a variable count.
  A specific requirement of this build (per the original checklist) is
  fixed 4-option MCQs, so this constraint is intentional.
- **Exam duration**: change `EXAM_DURATION_SECONDS` in `ExamPanel.java`.
- **Scoring**: currently 1 point per correct answer with no negative
  marking; adjust `ExamSession.getScore()` if you need weighting.
