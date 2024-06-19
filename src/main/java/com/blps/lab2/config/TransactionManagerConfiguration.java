package com.blps.lab2.config;


import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import jakarta.transaction.SystemException;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;



@Configuration
@EnableTransactionManagement
public class TransactionManagerConfiguration {


    @Bean
    public TransactionManager atomikosTransactionManager() throws SystemException {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(true);
        userTransactionManager.setTransactionTimeout(5);
        return userTransactionManager;
    }

    @Bean(name = "userTransaction")
    public UserTransaction userTransaction() throws SystemException {
        UserTransactionImp userTransaction = new UserTransactionImp();
        userTransaction.setTransactionTimeout(5);
        return userTransaction;
    }

    @Bean(name="transactionManager")
    public JtaTransactionManager transactionManager(UserTransaction userTransaction, TransactionManager atomikosTransactionManager){
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
        jtaTransactionManager.setUserTransaction(userTransaction);
        jtaTransactionManager.setTransactionManager(atomikosTransactionManager);
        jtaTransactionManager.setDefaultTimeout(5);
        return jtaTransactionManager;

    }


}
