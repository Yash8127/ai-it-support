package com.yaswanth.itsupport.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yaswanth.itsupport.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

}