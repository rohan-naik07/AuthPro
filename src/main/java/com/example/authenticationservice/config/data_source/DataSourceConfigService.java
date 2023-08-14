package com.example.authenticationservice.config.data_source;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import com.example.authenticationservice.dao_holder.DatabaseCreationStatus;
import com.example.authenticationservice.dao_holder.TenantDao;
import com.example.authenticationservice.dto.TenantDbInfoDto;
import com.example.authenticationservice.services.LiquibaseService;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataSourceConfigService {

    @NonFinal
    @Value("${spring.datasource.driver-class-name}")
    String mainDatasourceName;

    @NonFinal
    @Value("${spring.datasource.username}")
    String mainDatasourceUsername;

    @NonFinal
    @Value("${spring.datasource.password}")
    String mainDatasourcePassword;

    @NonFinal
    @Value("${spring.datasource.url}")
    String datasourceBaseUrl;

    @NonFinal
    Boolean wasMainDatasourceConfigured = false;

    DataSource mainDataSource;
    LiquibaseService liquibaseService;

    public DataSourceConfigService(@Qualifier("mainDataSource") DataSource mainDataSource,
                                   LiquibaseService liquibaseService) {
        this.mainDataSource = mainDataSource;
        this.liquibaseService = liquibaseService;
    }

    public Map<Object, Object> configureDataSources() {
        Map<Object, Object> dataSources = new HashMap<>();
        if (!wasMainDatasourceConfigured)  {
            liquibaseService.enableMigrationsToMainDatasource(mainDatasourceName, mainDatasourceUsername, mainDatasourcePassword);
            wasMainDatasourceConfigured = true;
        }
        List<TenantDbInfoDto> dtos = new TenantDao(mainDataSource).getTenantDbInfo(DatabaseCreationStatus.CREATED);
        dataSources.put(null, mainDataSource);
        for (TenantDbInfoDto dto : dtos) {
            dataSources.put(dto.getId(), configureDataSource(dto));
        }
        return dataSources;
    }

    private DataSource configureDataSource(TenantDbInfoDto dto) {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setUrl(getUrl(dto));
        dataSource.setUsername(dto.getUserName());
        dataSource.setPassword(dto.getDbPassword());

        return dataSource;
    }

    private String getUrl(TenantDbInfoDto dto) {

        return datasourceBaseUrl + dto.getDbName();
    }
}
