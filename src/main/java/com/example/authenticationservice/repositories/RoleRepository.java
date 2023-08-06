package com.example.authenticationservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.authenticationservice.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // You can add custom query methods here if needed
}

