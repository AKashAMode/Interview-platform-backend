package com.example.interview.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String password;
	
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Interview> Interviews;
	
	public User() {
		this.createdAt = LocalDateTime.now();
	}
	
	
	public User(String firstName, String lastName, String email, String password) {
		this();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}





	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}


	public List<Interview> getInterviews() {
		return Interviews;
	}




	public void setInterviews(List<Interview> interviews) {
		Interviews = interviews;
	}

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}



	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	

}
