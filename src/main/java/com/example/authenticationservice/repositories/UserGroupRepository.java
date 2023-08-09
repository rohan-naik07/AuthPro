package com.example.authenticationservice.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.authenticationservice.entity.UserGroup;

public interface UserGroupRepository extends JpaRepository<UserGroup,Long> {
    Optional<UserGroup> findByName(String name);
}
