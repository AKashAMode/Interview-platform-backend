package com.example.interview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.interview.entity.InterviewAnswer;


@Repository
public interface InterviewAnswerRepository  extends JpaRepository<InterviewAnswer, Long>{

	List<InterviewAnswer> findByInterviewIdOrderByQuestionNumber(Long interviewId);
	
}
