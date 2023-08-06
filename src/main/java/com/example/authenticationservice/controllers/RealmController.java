package com.example.authenticationservice.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authenticationservice.entity.Realm;
import com.example.authenticationservice.services.RealmServiceImpl;

@RestController
@RequestMapping("/realms")
public class RealmController {

    @Autowired
    private RealmServiceImpl realmService;

    public RealmController(RealmServiceImpl realmService) {
        this.realmService = realmService;
    }

    @PostMapping
    public ResponseEntity<Realm> createRealm(@RequestBody Realm realm) {
        Realm savedRealm = realmService.saveRealm(realm);
        return ResponseEntity.ok(savedRealm);
    }

    @GetMapping
    public ResponseEntity<List<Realm>> getAllRealms() {
        List<Realm> realms = realmService.getAllRealms();
        return ResponseEntity.ok(realms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Realm> getRealmById(@PathVariable Long id) {
        Realm realm = realmService.getRealmById(id);
        if (realm != null) {
            return ResponseEntity.ok(realm);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRealmById(@PathVariable Long id) {
        realmService.deleteRealmById(id);
        return ResponseEntity.noContent().build();
    }
}

