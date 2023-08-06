package com.example.authenticationservice.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authenticationservice.entity.Tenant;
import com.example.authenticationservice.services.TenantServiceImpl;

@RestController
@RequestMapping("/tenants")
public class TenantController {

    @Autowired
    private TenantServiceImpl tenantService;


    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getTenantById(@PathVariable Long id) {
        try {
            Optional<Tenant> tenant = tenantService.getTenantById(id);
            if (tenant.isPresent()) {
                return new ResponseEntity<>(tenant.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Tenant>> getAllTenants() {
        try {
            List<Tenant> tenants = tenantService.getAllTenants();
            return new ResponseEntity<>(tenants, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Tenant> createTenant(@RequestBody Tenant tenant) {
        try {
            Tenant savedTenant = tenantService.saveTenant(tenant);
            return new ResponseEntity<>(savedTenant, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tenant> updateTenant(@PathVariable Long id, @RequestBody Tenant updatedTenant) {
        try {
            Optional<Tenant> existingTenant = tenantService.getTenantById(id);
            if (existingTenant.isPresent()) {
                updatedTenant.setId(id);
                Tenant updatedTenantObj = tenantService.saveTenant(updatedTenant);
                return new ResponseEntity<>(updatedTenantObj, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenantById(@PathVariable Long id) {
        try {
            Optional<Tenant> existingTenant = tenantService.getTenantById(id);
            if (existingTenant.isPresent()) {
                tenantService.deleteTenantById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // You can add more endpoints and error handling for other service methods as needed
}
