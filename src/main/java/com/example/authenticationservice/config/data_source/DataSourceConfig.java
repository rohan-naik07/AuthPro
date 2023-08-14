package com.example.authenticationservice.config.data_source;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@DependsOn("dataSourceRouting")
public class DataSourceConfig {

    private DataSourceRoutingService dataSourceRouting;

    public DataSourceConfig(DataSourceRoutingService dataSourceRouting) {
        this.dataSourceRouting = dataSourceRouting;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        return dataSourceRouting;
    }

    @Primary
    @Bean(name="entityManager")
    public LocalContainerEntityManagerFactoryBean entityManagerBean(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(dataSource()).packages("com.example.authenticationservice").build();
    }

    @Bean(name="entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean customEntityManagerFactoryBean(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(dataSource()).packages("com.example.authenticationservice").build();
    }

    @Bean(name = "transactionManager")
    public JpaTransactionManager transactionManager(
        @Autowired 
        @Qualifier("entityManager") 
        LocalContainerEntityManagerFactoryBean customEntityManagerFactoryBean
    ) {
        return new JpaTransactionManager(customEntityManagerFactoryBean.getObject());
    }
}
