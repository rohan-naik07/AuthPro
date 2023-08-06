package com.example.authenticationservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.authenticationservice.entity.Realm;

@Repository
public interface RealmRepository extends JpaRepository<Realm, Long> {
    // You can add custom query methods here if needed
}

