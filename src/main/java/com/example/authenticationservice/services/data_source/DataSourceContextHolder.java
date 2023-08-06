package com.example.authenticationservice.services.data_source;

import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
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
        try {
            tenantId = (long) 0;
            setCurrentTenantId(tenantId);
        } catch (Exception e) {
            log.error("Exception occurred while 'updateTenantContext' execution: " + e.getMessage());
           tenantId = DEFAULT_TENANT_ID;
        }
        setCurrentTenantId(tenantId);
    }
}
