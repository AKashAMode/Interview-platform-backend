package com.example.interview.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.interview.entity.Interview;
import com.example.interview.entity.InterviewAnswer;
import com.example.interview.entity.User;
import com.example.interview.repository.InterviewAnswerRepository;
import com.example.interview.repository.InterviewRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InterviewService {
    
    @Autowired
    private InterviewRepository interviewRepository;
    
    @Autowired
    private InterviewAnswerRepository answerRepository;
    
    public Interview createInterview(User user, String role, String difficulty, 
                                   String language, String questionType, 
                                   Integer questionCount, Integer timeLimit) {
        Interview interview = new Interview();
        interview.setUser(user);
        interview.setRole(role);
        interview.setDifficulty(difficulty);
        interview.setLanguage(language);
        interview.setQuestionType(questionType);
        interview.setQuestionCount(questionCount);
        interview.setTimeLimit(timeLimit);
        
        return interviewRepository.save(interview);
    }
    
    public Interview completeInterview(Long interviewId, List<InterviewAnswer> answers, 
                                     Integer timeElapsed) {
        Optional<Interview> interviewOpt = interviewRepository.findById(interviewId);
        if (interviewOpt.isPresent()) {
            Interview interview = interviewOpt.get();
            interview.setStatus("COMPLETED");
            interview.setCompletedAt(LocalDateTime.now());
            interview.setTimeElapsed(timeElapsed);
            

            for (InterviewAnswer answer : answers) {
                answer.setInterview(interview);
                answerRepository.save(answer);
            }
            
            
            int totalScore = answers.stream()
                    .mapToInt(a -> a.getScore() != null ? a.getScore() : 70)
                    .sum();
            interview.setOverallScore(totalScore / answers.size());
            
            return interviewRepository.save(interview);
        }
        throw new RuntimeException("Interview not found");
    }
    
    public List<Interview> getUserInterviews(Long userId) {
        return interviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public Optional<Interview> getInterviewById(Long id) { 
        return interviewRepository.findById(id);
    }
    
    public void saveAnswer(Long interviewId, String question, String answer, Integer questionNumber) {
        Optional<Interview> interviewOpt = interviewRepository.findById(interviewId);
        if (interviewOpt.isPresent()) {
            InterviewAnswer interviewAnswer = new InterviewAnswer(
                interviewOpt.get(), question, answer, questionNumber
            ); 
            answerRepository.save(interviewAnswer);
        }
    }
}