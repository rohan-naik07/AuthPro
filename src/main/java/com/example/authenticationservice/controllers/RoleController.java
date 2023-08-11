package com.example.authenticationservice.controllers;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.authenticationservice.entity.Role;
import com.example.authenticationservice.entity.RoleMapping;
import com.example.authenticationservice.services.RoleServiceImpl;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleServiceImpl roleService;

    @PostMapping
    public ResponseEntity<Object> createRole(@RequestBody String role) {
        try {
            Role savedRole = roleService.saveRole(role);
            return new ResponseEntity<>(savedRole, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllRoles() {
        try {
            List<Role> roles = roleService.getAllRoles();
            return new ResponseEntity<>(roles, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRoleById(@PathVariable Long id) {
        try {
            Role role = roleService.getRoleById(id);
            if (role == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(role, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateRole(@PathVariable Long id, @RequestBody Role updatedRole) {
        try {
            Role existingRole = roleService.getRoleById(id);
            if (existingRole == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            existingRole.setName(updatedRole.getName());
            existingRole.setPrivilegeLevel(updatedRole.getPrivilegeLevel());

            Role updatedRoleObj = roleService.saveRole(existingRole.getName());
            return new ResponseEntity<>(updatedRoleObj, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("addUserGrouptoRole/{id}")
    public ResponseEntity<Object> addUserGrouptoRole(@PathVariable Long id, @RequestParam("roleId") Long roleId) {
        try {
            RoleMapping mapping = roleService.addUserGrouptoRole(id, roleId);
            return new ResponseEntity<>(mapping, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteRoleById(@PathVariable Long id) {
        try {
            Role role = roleService.getRoleById(id);
            if (role == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            roleService.deleteRoleById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
