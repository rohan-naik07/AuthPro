package com.example.authenticationservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.authenticationservice.entity.Role;
import com.example.authenticationservice.error.UserException;
import com.example.authenticationservice.repositories.RoleRepository;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role saveRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        role.setCreatedAt(new Date(System.currentTimeMillis()));
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // update role name
    public Role updateRoleName(String roleName,Long roleId) throws UserException{
        Role role = roleRepository.findById(roleId).orElseThrow(
            ()->{
                try {
                    return new UserException(new Exception("Role not found"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        );
        role.setName(roleName);
        role.setUpdatedAt(new Date(System.currentTimeMillis()));
        return roleRepository.save(role);
    }

    public Role getRoleById(Long id) {
        Optional<Role> optionalRole = roleRepository.findById(id);
        return optionalRole.orElse(null);
    }

    public void deleteRoleById(Long id) {
        roleRepository.deleteById(id);
    }
}
