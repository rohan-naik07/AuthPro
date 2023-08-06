package com.example.authenticationservice.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.authenticationservice.entity.User;


@Repository
public interface UserRepository extends JpaRepository<User,String>{
    Optional<User> findByUserName(String userName);

}
