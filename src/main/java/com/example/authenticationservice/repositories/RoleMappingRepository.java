package com.example.authenticationservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.authenticationservice.entity.RoleMapping;
import com.example.authenticationservice.entity.UserGroup;

public interface RoleMappingRepository extends JpaRepository<RoleMapping,Long>{
    RoleMapping findByUserGroup(UserGroup userGroup);
}
