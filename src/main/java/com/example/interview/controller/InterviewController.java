package com.example.interview.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.interview.config.JwtUtil;
import com.example.interview.entity.Interview;
import com.example.interview.entity.InterviewAnswer;
import com.example.interview.entity.User;
import com.example.interview.service.InterviewService;
import com.example.interview.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/api/interview")
@CrossOrigin(origins = "*")
public class InterviewController {
    
    @Autowired
    private InterviewService interviewService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createInterview(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        try {
            String email = getUserEmailFromToken(token);
            Optional<User> userOpt = userService.findByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                Interview interview = interviewService.createInterview(
                    user,
                    (String) request.get("role"),
                    (String) request.get("difficulty"),
                    (String) request.get("language"),
                    (String) request.get("questionType"),
                    (Integer) request.get("questionCount"),
                    (Integer) request.get("timeLimit")
                );
                
                Map<String, Object> response = new HashMap<>();
                response.put("interviewId", interview.getId());
                response.put("questions", generateQuestionsForRole(
                    (String) request.get("role"),
                    (String) request.get("questionType"),
                    (Integer) request.get("questionCount")
                ));
                response.put("message", "Interview created successfully");
                
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> completeInterview(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        try {
            Long interviewId = Long.valueOf(request.get("interviewId").toString());
            Integer timeElapsed = (Integer) request.get("timeElapsed");
            List<Map<String, Object>> answersData = 
                (List<Map<String, Object>>) request.get("answers");
            
            List<InterviewAnswer> answers = new ArrayList<>();
            for (int i = 0; i < answersData.size(); i++) {
                Map<String, Object> answerData = answersData.get(i);
                InterviewAnswer answer = new InterviewAnswer();
                answer.setQuestion((String) answerData.get("question"));
                answer.setAnswer((String) answerData.get("answer"));
                answer.setQuestionNumber(i + 1);
                answer.setScore(calculateAnswerScore((String) answerData.get("answer")));
                answers.add(answer);
            }
            
            Interview completedInterview = interviewService.completeInterview(
                interviewId, answers, timeElapsed
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("interviewId", completedInterview.getId());
            response.put("overallScore", completedInterview.getOverallScore());
            response.put("status", completedInterview.getStatus());
            response.put("message", "Interview completed successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getInterviewHistory(
            @RequestHeader("Authorization") String token) {
        try {
            String email = getUserEmailFromToken(token);
            Optional<User> userOpt = userService.findByEmail(email);
            
            if (userOpt.isPresent()) {
                List<Interview> interviews = interviewService.getUserInterviews(userOpt.get().getId());
                
                List<Map<String, Object>> interviewData = new ArrayList<>();
                for (Interview interview : interviews) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", interview.getId());
                    data.put("role", interview.getRole());
                    data.put("difficulty", interview.getDifficulty());
                    data.put("overallScore", interview.getOverallScore());
                    data.put("status", interview.getStatus());
                    data.put("createdAt", interview.getCreatedAt());
                    data.put("completedAt", interview.getCompletedAt());
                    interviewData.add(data);
                }
                
                Map<String, Object> response = new HashMap<>();
                response.put("interviews", interviewData);
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/details")
    public ResponseEntity<Map<String, Object>> getInterviewDetails(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        try {
            Optional<Interview> interviewOpt = interviewService.getInterviewById(id);
            
            if (interviewOpt.isPresent()) {
                Interview interview = interviewOpt.get();
                
                Map<String, Object> response = new HashMap<>();
                response.put("interview", Map.of(
                    "id", interview.getId(),
                    "role", interview.getRole(),
                    "difficulty", interview.getDifficulty(),
                    "language", interview.getLanguage(),
                    "questionType", interview.getQuestionType(),
                    "overallScore", interview.getOverallScore(),
                    "timeElapsed", interview.getTimeElapsed(),
                    "status", interview.getStatus(),
                    "createdAt", interview.getCreatedAt(),
                    "completedAt", interview.getCompletedAt()
                ));
                
                List<Map<String, Object>> answersData = new ArrayList<>();
                for (InterviewAnswer answer : interview.getAnswer()) {
                    answersData.add(Map.of(
                        "question", answer.getQuestion(),
                        "answer", answer.getAnswer(),
                        "questionNumber", answer.getQuestionNumber(),
                        "score", answer.getScore()
                    ));
                }
                response.put("answers", answersData);
                
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.badRequest().body(Map.of("error", "Interview not found"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    private String getUserEmailFromToken(String token) {
        String jwt = token.replace("Bearer ", "");
        return jwtUtil.extractUsername(token);
    }
    
    private List<String> generateQuestionsForRole(String role, String questionType, Integer count) {
        Map<String, List<String>> technicalQuestions = new HashMap<>();
        technicalQuestions.put("Frontend Developer", Arrays.asList(
            "Explain the Virtual DOM in React and its benefits",
            "What are React hooks and how do they work?",
            "How would you optimize a slow React application?",
            "Explain CSS-in-JS approaches and their pros/cons",
            "What is the difference between controlled and uncontrolled components?",
            "How do you handle state management in large React applications?",
            "Explain the concept of code splitting in React",
            "What are the differences between useMemo and useCallback?"
        ));
        
        technicalQuestions.put("Backend Developer", Arrays.asList(
            "Explain RESTful API design principles",
            "What are the differences between SQL and NoSQL databases?",
            "How would you handle authentication in a microservices architecture?",
            "Explain database indexing and its impact on performance",
            "What is the difference between synchronous and asynchronous processing?",
            "How do you ensure data consistency in distributed systems?",
            "Explain caching strategies and when to use them",
            "What are design patterns and give examples of commonly used ones?"
        ));
        
        List<String> behavioralQuestions = Arrays.asList(
            "Tell me about a time you faced a challenging technical problem",
            "Describe a situation where you had to work with a difficult team member",
            "How do you handle tight deadlines and pressure?",
            "Give an example of how you've mentored or helped junior developers",
            "Describe a time when you had to learn a new technology quickly",
            "Tell me about a project you're most proud of and why",
            "How do you stay updated with new technologies and industry trends?",
            "Describe a time when you had to make a difficult technical decision"
        );
        
        List<String> selectedQuestions = new ArrayList<>();
        
        if (role != null && technicalQuestions.containsKey(role)) {
            List<String> roleQuestions = technicalQuestions.get(role);
            Collections.shuffle(roleQuestions);
            selectedQuestions.addAll(roleQuestions.subList(0, Math.min(count/2, roleQuestions.size())));
        }
        
        if (!"Technical Only".equals(questionType)) {
            Collections.shuffle(behavioralQuestions);
            int remainingCount = count - selectedQuestions.size();
            selectedQuestions.addAll(behavioralQuestions.subList(0, Math.min(remainingCount, behavioralQuestions.size())));
        }
        
       
        while (selectedQuestions.size() < count) {
            selectedQuestions.add("Tell me about your experience with " + role + " technologies");
        }
        
        return selectedQuestions.subList(0, Math.min(count, selectedQuestions.size()));
    }
    
    private Integer calculateAnswerScore(String answer) {
        if (answer == null || answer.trim().isEmpty() || "Skipped".equals(answer)) {
            return 0;
        }
        
        int baseScore = 50;
        int lengthBonus = Math.min(answer.length() / 10, 30);
        
        
        String[] keywords = {"implement", "design", "algorithm", "database", "performance", 
                           "security", "scalability", "testing", "debugging", "optimization"};
        int keywordBonus = 0;
        String lowerAnswer = answer.toLowerCase();
        for (String keyword : keywords) {
            if (lowerAnswer.contains(keyword)) {
                keywordBonus += 5;
            }
        }
        
        return Math.min(baseScore + lengthBonus + keywordBonus, 100);
    }
}
