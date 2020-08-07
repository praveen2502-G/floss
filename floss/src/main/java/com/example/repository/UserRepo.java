package com.example.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.User;


public interface UserRepo extends JpaRepository<User, 	Long> {

	@Query("from User where username=?1")
     List<User> findByUsername(String username);

	@Query("from User where username=?1 and password=?2")
	 User findByUsernamePassword(String username,String password);

}