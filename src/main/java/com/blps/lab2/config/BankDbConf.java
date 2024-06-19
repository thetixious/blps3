package com.blps.lab2.config;


import com.atomikos.spring.AtomikosDataSourceBean;
import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = {"com.blps.lab2.repo.bank"},
        entityManagerFactoryRef = "bankEntityManager",
        transactionManagerRef = "transactionManager"
)
public class BankDbConf {

    @Value("${spring.bank-datasource.url}")
    private String url;

    @Value("${spring.bank-datasource.username}")
    private String username;

    @Value("${spring.bank-datasource.password}")
    private String password;
    @Bean(name="bankDataSource",initMethod = "init", destroyMethod = "close")
    public AtomikosDataSourceBean bankDataSource() {
        PGXADataSource pgxaDataSource = new PGXADataSource();
        pgxaDataSource.setUrl(url);
        pgxaDataSource.setUser(username);
        pgxaDataSource.setPassword(password);

        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setXaDataSource(pgxaDataSource);
        dataSource.setUniqueResourceName("bankDataSource");
        dataSource.setMaxPoolSize(100);
        dataSource.setBorrowConnectionTimeout(5);
        return dataSource;
    }
    public Map<String, String> bankJpaProperties() {
        Map<String, String> bankJpaProperties = new HashMap<>();
        bankJpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        bankJpaProperties.put("hibernate.show_sql", "true");
        bankJpaProperties.put("javax.persistence.transactionType", "JTA");

        return bankJpaProperties;
    }


    @Bean(name = "bankEntityManagerFactoryBuilder")
    public EntityManagerFactoryBuilder bankEntityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(
                new HibernateJpaVendorAdapter(), bankJpaProperties(), null
        );
    }
    @Bean(name = "bankEntityManager")
    public LocalContainerEntityManagerFactoryBean bankEntityManagerFactory(@Qualifier("bankEntityManagerFactoryBuilder") EntityManagerFactoryBuilder builder, @Qualifier("bankDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.blps.lab2.model.bankDB")
                .persistenceUnit("bank")
                .jta(true)
                .build();
    }


}