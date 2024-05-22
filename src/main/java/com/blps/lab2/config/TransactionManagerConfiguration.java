package com.blps.lab2.config;


import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.spring.AtomikosDataSourceBean;
import jakarta.transaction.SystemException;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;
import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration
@EnableTransactionManagement
public class TransactionManagerConfiguration {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean(initMethod = "init", destroyMethod = "close")
    public AtomikosDataSourceBean myDataSource() {
        PGXADataSource pgxaDataSource = new PGXADataSource();
        pgxaDataSource.setUrl(url);
        pgxaDataSource.setUser(username);
        pgxaDataSource.setPassword(password);

        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setXaDataSource(pgxaDataSource);
        dataSource.setUniqueResourceName("myDataSource");
        dataSource.setMaxPoolSize(100);
        return dataSource;
    }


    @Bean
    public TransactionManager atomikosTransactionManager() throws SystemException {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(false);
        userTransactionManager.setTransactionTimeout(20000);
        return userTransactionManager;
    }

    @Bean
    public UserTransaction userTransaction() throws SystemException {
        UserTransactionImp userTransaction = new UserTransactionImp();
        userTransaction.setTransactionTimeout(20000);
        return userTransaction;
    }

    @Bean(name="transactionManager")
    public PlatformTransactionManager transactionManager(UserTransaction userTransaction, TransactionManager atomikosTransactionManager){
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
        jtaTransactionManager.setUserTransaction(userTransaction);
        jtaTransactionManager.setTransactionManager(atomikosTransactionManager);
        jtaTransactionManager.setDefaultTimeout(20000);
        return jtaTransactionManager;

    }




}
