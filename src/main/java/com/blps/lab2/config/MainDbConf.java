package com.blps.lab2.config;

import com.atomikos.spring.AtomikosDataSourceBean;
import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;


import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = {"com.blps.lab2.repo.main"},
        entityManagerFactoryRef = "mainEntityManager"

)
public class MainDbConf {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean(name = "mainDataSource", initMethod = "init", destroyMethod = "close")
    public AtomikosDataSourceBean mainDataSource() {
        PGXADataSource pgxaDataSource = new PGXADataSource();
        pgxaDataSource.setUrl(url);
        pgxaDataSource.setUser(username);
        pgxaDataSource.setPassword(password);

        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setXaDataSource(pgxaDataSource);
        dataSource.setUniqueResourceName("mainDataSource");
        dataSource.setMaxPoolSize(100);
        return dataSource;
    }

    public Map<String, String> mainJpaProperties() {
        Map<String, String> mainJpaProperties = new HashMap<>();
        mainJpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        mainJpaProperties.put("hibernate.show_sql", "true");
        return mainJpaProperties;
    }


    @Bean(name = "mainEntityManagerFactoryBuilder")
    public EntityManagerFactoryBuilder mainEntityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(
                new HibernateJpaVendorAdapter(), mainJpaProperties(), null
        );
    }

    @Primary
    @Bean(name = "mainEntityManager")
    public LocalContainerEntityManagerFactoryBean mainEntityManagerFactory(@Qualifier("mainEntityManagerFactoryBuilder") EntityManagerFactoryBuilder builder, @Qualifier("mainDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.blps.lab2.model.mainDB")
                .persistenceUnit("main")
                .jta(true)
                .build();
    }


}
