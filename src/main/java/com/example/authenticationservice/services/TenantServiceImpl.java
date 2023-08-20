package com.example.authenticationservice.services;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.authenticationservice.config.data_source.DataSourceConfigService;
import com.example.authenticationservice.config.data_source.DataSourceRoutingService;
import com.example.authenticationservice.dao_holder.DatabaseCreationStatus;
import com.example.authenticationservice.dao_holder.TenantDao;
import com.example.authenticationservice.entity.Tenant;
import com.example.authenticationservice.intf.AuthService;
import com.example.authenticationservice.intf.UserService;
import com.example.authenticationservice.repositories.TenantRepository;


@Service
public class TenantServiceImpl {

    Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private final TenantRepository tenantRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private LiquibaseService liquibaseService;

    @Autowired
    DataSourceConfigService datasourceConfigService;
    
    @Autowired
    DataSourceRoutingService dataSourceRoutingService;

    @Autowired
    private UserService userService;

    @Autowired
    public TenantServiceImpl(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public List<Tenant> findTenantsByName(String name) {
        return tenantRepository.findByName(name);
    }

    public List<Tenant> findTenantsBySecretKey(String secretKey) {
        return tenantRepository.findBySecretKey(secretKey);
    }

    public List<Tenant> findTenantsByCreatedAt(Date createdAt) {
        return tenantRepository.findByCreatedAt(createdAt);
    }


    public Tenant saveTenant(Tenant tenant) throws Exception {
        // create superadmin user
        String userNameString = tenant.getName() + ":" + tenant.getDbPassword();
        authService.createUser(userNameString);
        tenant.setCreationStatus(DatabaseCreationStatus.IN_PROGRESS.toString());

        try {
            tenantDao.createTenantDb(tenant.getDbName(), tenant.getName(), tenant.getDbPassword());
            liquibaseService.enableMigrationsToTenantDatasource(tenant.getDbName(), tenant.getName(), tenant.getDbPassword());
            Map<Object, Object> configuredDataSources = datasourceConfigService.configureDataSources();
            dataSourceRoutingService.updateResolvedDataSources(configuredDataSources);
            tenant.setCreatedAt(new java.sql.Date(System.currentTimeMillis()));
            tenant.setSecretKey(UUID.randomUUID().toString());
            tenant.setCreationStatus(DatabaseCreationStatus.CREATED.toString());
            try {
                userService.createSuperAdminUserGroupandRole();
            } catch (Exception e) {
                logger.error("Failed to create super-admin user and role: " + e.getMessage());
            }
            return tenantRepository.save(tenant);
        } catch (Exception e) {
            logger.error("Failed to create tenant db: " + e.getMessage());
            tenant.setCreationStatus(DatabaseCreationStatus.FAILED_TO_CREATE.toString());
        }

        return null;
    }

    public Optional<Tenant> getTenantById(Long id) {
        return tenantRepository.findById(id);
    }

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    public void deleteTenantById(Long id) {
        tenantRepository.deleteById(id);
    }
}
