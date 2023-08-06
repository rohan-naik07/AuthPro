package com.example.authenticationservice.repositories;

import java.util.List;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.authenticationservice.entity.Mapping;
import com.example.authenticationservice.entity.Realm;

public interface MappingRepository extends JpaRepository<Mapping,Long> {
    List<Mapping> findByParent(Mapping parent);
    List<Mapping> findByRealm(Realm realm);
    Optional<Mapping> findByLocation(String location);
}

