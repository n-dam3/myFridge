package com.cos.myFridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cos.myFridge.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{
	public User findByUserId(String userId);
	public User findByUserName(String userName);
	boolean existsByUserName(String userName);
}