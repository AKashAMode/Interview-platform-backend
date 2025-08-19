package com.example.interview.entity;

import java.time.LocalDateTime;
import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "interviews")
public class Interview {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	private String role;
	
	private String difficulty;
	
	private String language;
	
	private String questionType;
	
	private Integer questionCount;
	
	private Integer timeLimit;
	
	private Integer timeElapsed;
	
	private Integer overallScore;
	
	private String status;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "completed_at")
	private LocalDateTime completedAt;
	
	@OneToMany(mappedBy = "interview", cascade = CascadeType.ALL)
	private List<InterviewAnswer> answer;
    
	
	public Interview() {
		this.createdAt = LocalDateTime.now();
		this.status = "IN_PROGRESS";
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public String getDifficulty() {
		return difficulty;
	}


	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public String getQuestionType() {
		return questionType;
	}


	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}


	public Integer getQuestionCount() {
		return questionCount;
	}


	public void setQuestionCount(Integer questionCount) {
		this.questionCount = questionCount;
	}


	public Integer getTimeLimit() {
		return timeLimit;
	}


	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
	}


	public Integer getTimeElapsed() {
		return timeElapsed;
	}


	public void setTimeElapsed(Integer timeElapsed) {
		this.timeElapsed = timeElapsed;
	}


	public Integer getOverallScore() {
		return overallScore;
	}


	public void setOverallScore(Integer overallScore) {
		this.overallScore = overallScore;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public LocalDateTime getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}


	public LocalDateTime getCompletedAt() {
		return completedAt;
	}


	public void setCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
	}


	public List<InterviewAnswer> getAnswer() {
		return answer;
	}


	public void setAnswer(List<InterviewAnswer> answer) {
		this.answer = answer;
	}
	
	
	
	
}
