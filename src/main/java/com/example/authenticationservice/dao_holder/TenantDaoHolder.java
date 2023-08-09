package com.example.authenticationservice.dao_holder;

import javax.sql.DataSource;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import java.util.HashMap;
import java.util.Map;
import static lombok.AccessLevel.PROTECTED;

@Service
@FieldDefaults(level = PROTECTED, makeFinal = true)
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TenantDaoHolder implements SmartInitializingSingleton {

    @NonFinal
    Map<Long, TenantDao> templates;

    @Override
    public void afterSingletonsInstantiated() {
        templates = new HashMap<>();
    }

    public TenantDao getTemplateByTenantKey(Long tenantKey) {
        return templates.get(tenantKey);
    }

    public void addNewTemplates(Map<Object, Object> dataSources) {
        dataSources.forEach((key, value) -> {
            TenantDao tenantDao = new TenantDao((DataSource) value);
            templates.putIfAbsent((Long) key, tenantDao);
        });
    }
}
