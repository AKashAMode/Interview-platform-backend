package com.example.interview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.interview.entity.Interview;
import com.example.interview.entity.User;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
	
	List<Interview> findByUserIdOrderByCreatedAtDesc(Long userId);
	
	List<Interview> findByUserIdAndStatus(Long userId, String status);

}
