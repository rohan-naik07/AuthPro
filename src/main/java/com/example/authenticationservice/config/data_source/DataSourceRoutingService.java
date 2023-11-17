package com.example.authenticationservice.config.data_source;

import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Service;
import com.example.authenticationservice.dao_holder.TenantDaoHolder;
import javax.sql.DataSource;
import java.util.Map;
import static lombok.AccessLevel.PRIVATE;

@RefreshScope
@Service(value = "dataSourceRouting")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DataSourceRoutingService extends AbstractRoutingDataSource implements SmartInitializingSingleton {

    Map<String, TenantDaoHolder> daoHolders;
    DataSourceConfigService datasourceConfigService;

    @NonFinal
    @Value("auth")
    String mainDatasourceName;

    @NonFinal
    @Value("${spring.datasource.username}")
    String mainDatasourceUsername;

    @NonFinal
    @Value("${spring.datasource.password}")
    String mainDatasourcePassword;

    @Autowired
    public DataSourceRoutingService(
        @Lazy DataSourceConfigService datasourceConfigService,
        @Qualifier("mainDataSource") DataSource mainDataSource,
        Map<String, TenantDaoHolder> daoHolders
    ) {
        this.datasourceConfigService = datasourceConfigService;
        //this.liquibaseService.enableMigrationsToMainDatasource(mainDatasourceName,mainDatasourceUsername, mainDatasourcePassword);
        Map<Object, Object> dataSourceMap = this.datasourceConfigService.configureDataSources();
        this.setTargetDataSources(dataSourceMap);
        this.setDefaultTargetDataSource(mainDataSource);
        this.daoHolders = daoHolders;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<Object, Object> dataSources = datasourceConfigService.configureDataSources();
        updateResolvedDataSources(dataSources);
        updateDaoTemplateHolders(dataSources);
    }

    @Override
    protected Long determineCurrentLookupKey() {
        Long key = DataSourceContextHolder.getCurrentTenantId();
        return key;
    }

    public void updateResolvedDataSources(Map<Object, Object> dataSources) {
        setTargetDataSources(dataSources);
        afterPropertiesSet();
    }

    public void updateDaoTemplateHolders(Map<Object, Object> dataSources) {
        daoHolders.forEach((key, value) -> value.addNewTemplates(dataSources));
    }
}
