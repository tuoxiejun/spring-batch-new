package com.example.chatprer05_1;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
//    @Primary
//    @Bean(name = "dataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.datasource1")
//    public DataSource dataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean(name = "dataSource2")
//    @ConfigurationProperties(prefix = "spring.datasource.datasource2")
//    public DataSource dataSource2() {
//        return DataSourceBuilder.create().build();
//    }
    @Primary
    @Bean(name = "dataSource")
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/datasource1");
        config.setUsername("root");
        config.setPassword("root");
        return new HikariDataSource(config);
    }

    @Bean(name = "dataSource2")
    public DataSource dataSource2() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/datasource2");
        config.setUsername("root");
        config.setPassword("root");
        return new HikariDataSource(config);
    }


    @Bean(name = "costomTransactionManager")
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource2());
    }
}
