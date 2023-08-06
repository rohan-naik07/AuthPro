package com.example.authenticationservice.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.authenticationservice.entity.Tenant;

import java.util.Date;
import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    List<Tenant> findByName(String name);

    List<Tenant> findBySecretKey(String secretKey);

    List<Tenant> findByCreatedAt(Date createdAt);

    // You can add more custom query methods here if needed
}
