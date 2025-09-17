package com.bus.com.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bus.com.entities.User;



public interface UserRepository extends JpaRepository<User, Long> {
//load use details by user name(email)
	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);
	boolean existsByMobileNo(Long mobileNo);
}
