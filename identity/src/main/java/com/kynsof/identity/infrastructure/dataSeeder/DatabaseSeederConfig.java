package com.kynsof.identity.infrastructure.dataSeeder;


import com.kynsof.identity.infrastructure.repository.query.BusinessModuleReadDataJPARepository;
import com.kynsof.identity.infrastructure.repository.query.ModuleReadDataJPARepository;
import com.kynsof.identity.infrastructure.repository.query.PermissionReadDataJPARepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DatabaseSeederConfig {

    private final DataSource dataSource;
    private final ModuleReadDataJPARepository moduleQuery;
    private final PermissionReadDataJPARepository permissionReadDataJPARepository;
    private final BusinessModuleReadDataJPARepository businessModuleReadDataJPARepository;

    public DatabaseSeederConfig(
            DataSource dataSource, ModuleReadDataJPARepository moduleQuery, PermissionReadDataJPARepository permissionReadDataJPARepository, BusinessModuleReadDataJPARepository businessModuleReadDataJPARepository) {
        this.dataSource = dataSource;
        this.moduleQuery = moduleQuery;
        this.permissionReadDataJPARepository = permissionReadDataJPARepository;
        this.businessModuleReadDataJPARepository = businessModuleReadDataJPARepository;
    }

    @PostConstruct
    public void seedDatabase() {
        try {
            if (moduleQuery.count() == 0) {
                ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("module_system.sql"));
            }
            if (permissionReadDataJPARepository.count() == 0) {
                ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("permission.sql"));
            }
            if (businessModuleReadDataJPARepository.count() == 0) {
                ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("business_module.sql"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}