package com.example.authenticationservice.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.authenticationservice.dto.MappingRequest;
import com.example.authenticationservice.entity.Mapping;
import com.example.authenticationservice.entity.Realm;
import com.example.authenticationservice.error.AuthException;
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

   
    @PostMapping("/addMapping")
    public ResponseEntity<String> addRealmMappings(
            @RequestParam Long realmId,
            @RequestParam String mappingLocation,
            @RequestParam Long parentMappingId
    ) {
        try {
            realmService.addRealmMapping(realmId, mappingLocation, parentMappingId);
            return new ResponseEntity<>("Mapping added successfully", HttpStatus.OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/addMappings")
    public ResponseEntity<String> addRealmMappings(
            @RequestParam Long realmId,
            @RequestParam List<MappingRequest> mappings
    ) {
        try {
            realmService.addRealmMappings(realmId, mappings);
            return new ResponseEntity<>("Mappings added successfully", HttpStatus.OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/removeMapping")
    public ResponseEntity<String> removeRealmMapping(
            @RequestParam String mappingLocation,
            @RequestParam Long parentMappingId
    ) {
        try {
            realmService.removeRealmMapping(mappingLocation, parentMappingId);
            return new ResponseEntity<>("Mapping removed successfully", HttpStatus.OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/mappings/{realmId}")
    public ResponseEntity<List<Mapping>> getRealmMappings(@PathVariable Long realmId) {
        try {
            List<Mapping> mappings = realmService.getRealmMappings(realmId);
            return new ResponseEntity<>(mappings, HttpStatus.OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/mappings/parent/{parentId}")
    public ResponseEntity<List<Mapping>> getMappingsByParent(@PathVariable Long parentId) {
        try {
            List<Mapping> mappings = realmService.getMappingsbyParent(parentId);
            return new ResponseEntity<>(mappings, HttpStatus.OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/mappings/location/{location}")
    public ResponseEntity<List<Mapping>> getMappingsByLocation(@PathVariable String location) {
        try {
            List<Mapping> mappings = realmService.getMappingsbyLocation(location);
            return new ResponseEntity<>(mappings, HttpStatus.OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
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

