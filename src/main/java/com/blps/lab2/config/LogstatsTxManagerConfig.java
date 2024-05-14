package com.blps.lab2.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "logstatsEntityManagerFactory", 
                        transactionManagerRef = "logstatsTxManager", 
                        basePackages = {"com.blps.lab2.model.repository.logstats"})
public class LogstatsTxManagerConfig {

    @Bean(name = "ds2properties")
    @ConfigurationProperties("spring.datasource2")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "ds2")
    @ConfigurationProperties(prefix = "spring.datasource2")
    public DataSource datasource(@Qualifier("ds2properties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "logstatsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(EntityManagerFactoryBuilder builder,
            @Qualifier("ds2") DataSource dataSource) {

        return builder.dataSource(dataSource)
                .packages("com.blps.lab2.model.beans.logstats")
                .persistenceUnit("logstatsPersistenceUnit")
                .build();
    }

    @Bean(name = "logstatsTxManager")
    @ConfigurationProperties("spring.jpa")
    public PlatformTransactionManager transactionManager(
            @Qualifier("logstatsEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
