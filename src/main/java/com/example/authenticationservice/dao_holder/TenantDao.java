package com.example.authenticationservice.dao_holder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Repository;
import com.example.authenticationservice.dto.TenantDbInfoDto;
import com.example.authenticationservice.entity.Tenant;
import com.example.authenticationservice.error.UserException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Data
@Repository
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class TenantDao  {

    Logger logger = LoggerFactory.getLogger(TenantDao.class);

    @NonFinal
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @NonFinal
    JdbcTemplate jdbcTemplate;
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    DataSource mainDataSource;

    @Autowired
    public TenantDao(@Qualifier("mainDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.mainDataSource = dataSource;
    }

    public List<TenantDbInfoDto> getTenantDbInfo(DatabaseCreationStatus creationStatus) {
        String query = "select id, db_name, name, db_password " +
                "from tenants " +
                "where creation_status = :creationStatus";
        MapSqlParameterSource params = new MapSqlParameterSource("creationStatus", creationStatus.getValue());

        return namedParameterJdbcTemplate.query(query, params, (rs, rowNum) -> {
            TenantDbInfoDto dto = new TenantDbInfoDto();
            dto.setId(rs.getLong("id"));
            dto.setDbName(rs.getString("db_name"));
            dto.setUserName(rs.getString("name"));
            dto.setDbPassword(rs.getString("db_password"));
            return dto;
        });
    }

    public void createTenantDb(String dbName) {
        String createDbQuery = "CREATE DATABASE IF NOT EXISTS " + dbName;
        jdbcTemplate.execute(createDbQuery);
        log.info("Created database: " + dbName);
    }
    
    public void createTables(Tenant tenant) throws Exception {
         // Read the SQL statements from the schema.sql file and execute them
        String scriptPath = "create.sql";
        Class.forName("com.mysql.cj.jdbc.Driver");
        // Database connection URL (replace with your database URL)
        String url = "jdbc:mysql://localhost:3306/" + tenant.getDbName();
        // Establish the database connection
        Connection connection = DriverManager.getConnection(url,"root","YES");
        try {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(scriptPath));
            // Close the connection when done
            connection.close();
        } catch (ScriptException | SQLException e) {
            throw new UserException(e);
        }
    }
}
