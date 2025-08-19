package com.example.interview.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "interview_answers")
public class InterviewAnswer {
	
	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
	  
	  @ManyToOne
	  @JoinColumn(name = "interview_id", nullable = false)
	  private Interview interview;
	  
	  private String question;
	  
	  @Column(length = 2000)
	  private String answer;
	  
	  private Integer questionNumber;
	  
	  private Integer score;
	  
	  
	  public InterviewAnswer() {
		  
	  }
	  
	  


	public InterviewAnswer(Interview interview, String question, String answer, Integer questionNumber) {
		this.interview = interview;
		this.question = question;
		this.answer = answer;
		this.questionNumber = questionNumber;
	}



	public Long getId() {
		return id;
	}




	public void setId(Long id) {
		this.id = id;
	}


	public Interview getInterview() {
		return interview;
	}


	public void setInterview(Interview interview) {
		this.interview = interview;
	}


	public String getQuestion() {
		return question;
	}


	public void setQuestion(String question) {
		this.question = question;
	}


	public String getAnswer() {
		return answer;
	}


	public void setAnswer(String answer) {
		this.answer = answer;
	}


	public Integer getQuestionNumber() {
		return questionNumber;
	}


	public void setQuestionNumber(Integer questionNumber) {
		this.questionNumber = questionNumber;
	}


	public Integer getScore() {
		return score;
	}


	public void setScore(Integer score) {
		this.score = score;
	}
	  
  
	  
}
