package com.example.authenticationservice.config.data_source;

import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import static lombok.AccessLevel.PRIVATE;


@Slf4j
@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DataSourceContextHolder {
    Logger logger = LoggerFactory.getLogger(DataSourceContextHolder.class);
    @NonFinal
    static ThreadLocal<Long> currentTenantId = new ThreadLocal<>();
    static Long DEFAULT_TENANT_ID = null;

    public static void setCurrentTenantId(Long tenantId) {
        currentTenantId.set(tenantId);
    }

    public static Long getCurrentTenantId() {
        return currentTenantId.get();
    }

    public void updateTenantContext(HttpServletRequest request) {

        Long tenantId;
        if(request.getHeader("tenantId") == null){
            setCurrentTenantId(DEFAULT_TENANT_ID);
            return;
        }
        try {
            tenantId = Long.parseLong(request.getHeader("tenantId"));
            logger.info("Current tenant id: " + tenantId);
            setCurrentTenantId(tenantId);
        } catch (Exception e) {
            log.error("Exception occurred while 'updateTenantContext' execution: " + e.getMessage());
           tenantId = DEFAULT_TENANT_ID;
        }
        setCurrentTenantId(tenantId);
    }
}
