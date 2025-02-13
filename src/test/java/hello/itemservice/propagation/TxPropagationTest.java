package hello.itemservice.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

@Slf4j
@SpringBootTest
public class TxPropagationTest {

    @TestConfiguration
    static class Config {
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Autowired
    private PlatformTransactionManager tx;

    @Test
    void outer_transaction() {
        log.info("outer 트랜잭션 시작");
        TransactionStatus outerStatus = tx.getTransaction(new DefaultTransactionAttribute());
        log.info("outer 트랜잭션 is New ? {}", outerStatus.isNewTransaction() ? "✅" : "❌");
        inner_transaction();
        log.info("outer 트랜잭션 커밋");
        tx.commit(outerStatus);

    }

    private void inner_transaction() {
        log.info("inner 트랜잭션 시작");
        TransactionStatus innerStatus = tx.getTransaction(new DefaultTransactionAttribute());
        log.info("inner 트랜잭션 is New ? {}", innerStatus.isNewTransaction() ? "✅" : "❌");
        log.info("inner 트랜잭션 커밋");
        tx.commit(innerStatus);
    }

    @Test
    void outer_transaction_rollback() {
        log.info("outer 트랜잭션 시작");
        TransactionStatus outerStatus = tx.getTransaction(new DefaultTransactionAttribute());

        log.info("inner 트랜잭션 시작");
        TransactionStatus innerStatus = tx.getTransaction(new DefaultTransactionAttribute());
        log.info("inner 트랜잭션 커밋");
        tx.commit(innerStatus);

        log.info("outer 트랜잭션 롤백");
        tx.rollback(outerStatus);
    }

    @Test
    void inner_transaction_rollback() {
        log.info("outer 트랜잭션 시작");
        TransactionStatus outerStatus = tx.getTransaction(new DefaultTransactionAttribute());

        log.info("inner 트랜잭션 시작");
        TransactionStatus innerStatus = tx.getTransaction(new DefaultTransactionAttribute());
        log.info("inner 트랜잭션 롤백");
        tx.rollback(innerStatus);

        log.info("outer 트랜잭션 커밋");
        tx.commit(outerStatus);
    }

    @Test
    void inner_transaction_rollback_option_requires_new() {
        log.info("1️⃣outer 트랜잭션 시작");
        TransactionStatus outerStatus = tx.getTransaction(new DefaultTransactionAttribute());
        log.info("1️⃣outer isNewTransaction = {}", outerStatus.isNewTransaction());

        log.info("2️⃣inner 트랜잭션 시작");
        DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus innerStatus = tx.getTransaction(definition);
        log.info("2️⃣inner isNewTransaction = {}", innerStatus.isNewTransaction());
        log.info("2️⃣inner 트랜잭션 롤백");
        tx.rollback(innerStatus);

        log.info("1️⃣outer 트랜잭션 커밋");
        tx.commit(outerStatus);
    }
}
