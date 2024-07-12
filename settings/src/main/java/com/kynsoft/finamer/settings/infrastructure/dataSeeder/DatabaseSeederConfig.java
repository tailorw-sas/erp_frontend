package com.kynsoft.finamer.settings.infrastructure.dataSeeder;



import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageModuleReadDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManagePermissionReadDataJPARepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DatabaseSeederConfig {

    private final DataSource dataSource;
    private final ManageModuleReadDataJPARepository moduleQuery;
    private final ManagePermissionReadDataJPARepository permissionReadDataJPARepository;

    public DatabaseSeederConfig(
            DataSource dataSource, ManageModuleReadDataJPARepository moduleQuery, ManagePermissionReadDataJPARepository permissionReadDataJPARepository) {
        this.dataSource = dataSource;
        this.moduleQuery = moduleQuery;
        this.permissionReadDataJPARepository = permissionReadDataJPARepository;
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}