package com.example.authenticationservice.services;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.authenticationservice.entity.Tenant;
import com.example.authenticationservice.repositories.TenantRepository;
import com.example.authenticationservice.services.dao_holder.DatabaseCreationStatus;
import com.example.authenticationservice.services.dao_holder.TenantDao;
import com.example.authenticationservice.services.data_source.DataSourceConfigService;
import com.example.authenticationservice.services.data_source.DataSourceRoutingService;

@Service
public class TenantServiceImpl {

    Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private final TenantRepository tenantRepository;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private LiquibaseService liquibaseService;

    @Autowired
    DataSourceConfigService datasourceConfigService;
    
    @Autowired
    DataSourceRoutingService dataSourceRoutingService;

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


    public Tenant saveTenant(Tenant tenant) {

        tenant.setCreationStatus(DatabaseCreationStatus.IN_PROGRESS.toString());

        try {
            tenantDao.createTenantDb(tenant.getDbName(), tenant.getName(), tenant.getDbPassword());
            tenant.setCreationStatus(DatabaseCreationStatus.CREATED.toString());
        } catch (Exception e) {
            logger.error("Failed to create tenant db: " + e.getMessage());
            tenant.setCreationStatus(DatabaseCreationStatus.FAILED_TO_CREATE.toString());
        }

        if (DatabaseCreationStatus.CREATED.toString().equals(tenant.getCreationStatus())) {
            liquibaseService.enableMigrationsToTenantDatasource(tenant.getDbName(), tenant.getName(), tenant.getDbPassword());
            Map<Object, Object> configuredDataSources = datasourceConfigService.configureDataSources();
            dataSourceRoutingService.updateResolvedDataSources(configuredDataSources);
        }

        return tenantRepository.save(tenant);
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
