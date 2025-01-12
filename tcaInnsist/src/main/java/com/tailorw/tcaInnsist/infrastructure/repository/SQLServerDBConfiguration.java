package com.tailorw.tcaInnsist.infrastructure.repository;

import com.tailorw.tcaInnsist.infrastructure.model.ManageConnection;
import com.tailorw.tcaInnsist.infrastructure.repository.config.DBConfigurationProperties;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SQLServerDBConfiguration {
    
    public static String getUrl(String server, String port, String dbName){
        return String.format("jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=true;trustServerCertificate=true;", server, port, dbName);
    }

    public static DBConfigurationProperties getDBProperties(ManageConnection tcaConfigurationProperties){
        DBConfigurationProperties configuration = new DBConfigurationProperties();
        configuration.setUrl(SQLServerDBConfiguration.getUrl(tcaConfigurationProperties.getServer(), tcaConfigurationProperties.getPort(), tcaConfigurationProperties.getDbName()));
        configuration.setUsername(tcaConfigurationProperties.getUserName());
        configuration.setPassword(tcaConfigurationProperties.getPassword());
        return configuration;
    }

    public DataSource createDataSource(DBConfigurationProperties properties){
        System.out.println("URL: " + properties.getUrl() + " UserName: " + properties.getUsername() + " Password: " + properties.getPassword());
        return DataSourceBuilder.create()
                .driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .build();
    }

    public EntityManagerFactory createEntityManagerFactory(DataSource dataSource){
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan(new String[] {"com.tailorw.tcaInnsist.infrastructure.model"});
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        entityManagerFactoryBean.setJpaPropertyMap(jpaProperties);

        entityManagerFactoryBean.afterPropertiesSet();

        return  entityManagerFactoryBean.getObject();
    }

    public static Connection createConnection(String url, String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

}
