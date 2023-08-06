package com.example.authenticationservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.authenticationservice.entity.Mapping;

public interface MappingRepository extends JpaRepository<Mapping,Long> {
    
}
