package com.cj.productsvc.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class MySqlDbConfig {

   private final ProductDatasourceProperties dbProps;
    @Bean
    public DataSource dataSource(){
        ProductDatasourceProperties.DataSourceProperties ds = dbProps.getDatasource();
        ProductDatasourceProperties.HikariProperties hk = dbProps.getHikari();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ds.getUrl());
        config.setUsername(ds.getUsername());
        config.setPassword(ds.getPassword());
        config.setDriverClassName(ds.getDriverClassName());

        config.setMaximumPoolSize(hk.getMaximumPoolSize());
        config.setMinimumIdle(hk.getMinimumIdle());
        config.setIdleTimeout(hk.getIdleTimeout());
        config.setMaxLifetime(hk.getMaxLifetime());
        config.setConnectionTimeout(hk.getConnectionTimeout());
        config.setPoolName(hk.getPoolName());
        config.setAutoCommit(hk.isAutoCommit());
        config.setValidationTimeout(hk.getValidationTimeout());
        config.setLeakDetectionThreshold(hk.getLeakDetectionThreshold());

        return new HikariDataSource(config);

    }

    @Bean
    public JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(dataSource());
    }
}
