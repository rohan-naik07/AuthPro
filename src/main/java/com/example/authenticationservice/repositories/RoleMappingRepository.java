package com.example.authenticationservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.authenticationservice.entity.Mapping;
import com.example.authenticationservice.entity.RoleMapping;

public interface RoleMappingRepository extends JpaRepository<RoleMapping,Long>{
    RoleMapping findByMapping(Mapping mapping);
}
