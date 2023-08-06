package com.example.authenticationservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.authenticationservice.entity.Role;
import com.example.authenticationservice.entity.RoleMapping;
import com.example.authenticationservice.entity.UserGroup;
import com.example.authenticationservice.error.UserException;
import com.example.authenticationservice.repositories.RoleMappingRepository;
import com.example.authenticationservice.repositories.RoleRepository;
import com.example.authenticationservice.repositories.UserGroupRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private RoleMappingRepository roleMappingRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role saveRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        role.setPrivilegeLevel("HIGH");
        role.setCreatedAt(new Date(System.currentTimeMillis()));
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // add user group to role
    public RoleMapping addUserGrouptoRole(Long userGroupId,Long roleId) throws Exception{
        UserGroup userGroup = userGroupRepository.findById(userGroupId).orElseThrow(()->{
            try {
                return new UserException(new Exception("Cannot find user group"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        });
        Role role = roleRepository.findById(roleId).orElseThrow(()->{
            try {
                return new UserException(new Exception("Cannot find role"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        });
        RoleMapping roleMapping = new RoleMapping();
        roleMapping.setRole(role);
        roleMapping.setUserGroup(userGroup);
        roleMapping.setCreatedAt(new Date(System.currentTimeMillis()));
        return roleMappingRepository.save(roleMapping);
    }

    // update role name
    public Role updateRoleName(String roleName,Long roleId) throws UserException{
        Role role = roleRepository.findById(roleId).orElseThrow(
            ()->{
                try {
                    return new UserException(new Exception("Role not found"));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }
        );
        role.setName(roleName);
        role.setUpdatedAt(new Date(System.currentTimeMillis()));
        return roleRepository.save(role);
    }

    // remove user group from role

    public Role getRoleById(Long id) {
        Optional<Role> optionalRole = roleRepository.findById(id);
        return optionalRole.orElse(null);
    }

    public void deleteRoleById(Long id) {
        roleRepository.deleteById(id);
    }
}
