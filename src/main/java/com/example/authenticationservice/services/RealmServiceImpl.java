package com.example.authenticationservice.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.authenticationservice.entity.Realm;
import com.example.authenticationservice.repositories.RealmRepository;

@Service
public class RealmServiceImpl {

    private final RealmRepository realmRepository;

    public RealmServiceImpl(RealmRepository realmRepository) {
        this.realmRepository = realmRepository;
    }

    public Realm saveRealm(Realm realm) {
        return realmRepository.save(realm);
    }

    public List<Realm> getAllRealms() {
        return realmRepository.findAll();
    }

    public Realm getRealmById(Long id) {
        return realmRepository.findById(id).orElse(null);
    }

    public void deleteRealmById(Long id) {
        realmRepository.deleteById(id);
    }
}
