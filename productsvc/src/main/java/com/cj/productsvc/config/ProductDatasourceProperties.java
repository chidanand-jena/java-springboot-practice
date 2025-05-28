package com.cj.productsvc.config;

import com.zaxxer.hikari.HikariConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@ConfigurationProperties(prefix = "productsvc")
@Getter
@Setter
public class ProductDatasourceProperties {
    private DataSourceProperties  datasource;
    private HikariProperties hikari;

    @Getter
    @Setter
    public static class DataSourceProperties{
        private String url;// jdbc:mysql://localhost:3306/product_db
        private String username;//: root
        private String password;//: cjdev@25

        //@ConfigurationProperties supports relaxed binding.
        // Converts kebab-case (driver-class-name) to camelCase
        private String driverClassName;//: com.mysql.cj.jdbc.Driver
    }
    @Getter
    @Setter
    public static class HikariProperties{
        private int maximumPoolSize;//: 10
        private int minimumIdle;//: 5
        private long idleTimeout;//idle-timeout: 30000
        private long maxLifetime ;//max-lifetime: 1800000
        private long connectionTimeout  ;//connection-timeout: 30000
        private String poolName ;//pool-name: ProductHikariCP
        private boolean autoCommit ;//auto-commit: true
        private long validationTimeout ;//validation-timeout: 5000
        private long leakDetectionThreshold;//leak-detection-threshold: 2000

    }


}
