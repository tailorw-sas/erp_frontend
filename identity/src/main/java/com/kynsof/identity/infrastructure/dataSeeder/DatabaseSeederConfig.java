//package com.kynsof.identity.infrastructure.dataSeeder;
//
//
//import com.kynsof.identity.infrastructure.repository.query.GeographicLocationReadDataJPARepository;
//import com.kynsof.identity.infrastructure.repository.query.ModuleReadDataJPARepository;
//import com.kynsof.identity.infrastructure.repository.query.PermissionReadDataJPARepository;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.jdbc.datasource.init.ScriptUtils;
//
//import javax.annotation.PostConstruct;
//import javax.sql.DataSource;
//import java.sql.SQLException;
//
//@Configuration
//public class DatabaseSeederConfig {
//
//    private final DataSource dataSource;
//    private final GeographicLocationReadDataJPARepository repositoryQuery;
//    private final ModuleReadDataJPARepository moduleQuery;
//    private final PermissionReadDataJPARepository permissionReadDataJPARepository;
//
//    public DatabaseSeederConfig(GeographicLocationReadDataJPARepository repositoryQuery,
//                                DataSource dataSource, ModuleReadDataJPARepository moduleQuery, PermissionReadDataJPARepository permissionReadDataJPARepository) {
//        this.repositoryQuery = repositoryQuery;
//        this.dataSource = dataSource;
//        this.moduleQuery = moduleQuery;
//        this.permissionReadDataJPARepository = permissionReadDataJPARepository;
//    }
//
//    @PostConstruct
//    public void seedDatabase() {
//        try {
//            if (repositoryQuery.count() == 0) {
//                ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("geographiclocation.sql"));
//            }
//            if (moduleQuery.count() == 0) {
//                ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("module_permission.sql"));
//            }
//            if (permissionReadDataJPARepository.count() == 0) {
//                ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("permissionReadDataJPARepository.sql"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}