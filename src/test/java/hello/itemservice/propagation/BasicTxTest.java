package hello.itemservice.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager txManger;

//    @TestConfiguration
//    static class Config {
//        @Bean
//        public PlatformTransactionManager transactionManager(DataSource dataSource) {
//            return new DataSourceTransactionManager(dataSource);
//        }
//    }

    @Test
    void commit() {
        log.info("✅트랜잭션 시작");
        TransactionStatus status = txManger.getTransaction(new DefaultTransactionAttribute());

        log.info("✅트랜잭션 커밋 시작");
        txManger.commit(status);
        log.info("✅트랜잭션 커밋 완료");
    }

    @Test
    void rollback() {
        log.info("✅트랜잭션 시작");
        TransactionStatus status = txManger.getTransaction(new DefaultTransactionAttribute());

        log.info("⚠️트랜잭션 롤백 시작");
        txManger.rollback(status);
        log.info("⚠️트랜잭션 롤백 완료");
    }

    @Test
    void two_transaction_double_commit() {
        log.info("✅트랜잭션-A 시작");
        TransactionStatus statusA = txManger.getTransaction(new DefaultTransactionAttribute());
        log.info("✅트랜잭션-A 커밋");
        txManger.commit(statusA);

        System.out.println();
        System.out.println();

        log.info("✅트랜잭션-B 시작");
        TransactionStatus statusB = txManger.getTransaction(new DefaultTransactionAttribute());
        log.info("✅트랜잭션-B 커밋");
        txManger.commit(statusB);
    }

    @Test
    void two_transaction_double_rollback() {
        log.info("✅트랜잭션-A 시작");
        TransactionStatus statusA = txManger.getTransaction(new DefaultTransactionAttribute());
        log.info("✅트랜잭션-A 롤백");
        txManger.rollback(statusA);

        System.out.println();
        System.out.println();

        log.info("✅트랜잭션-B 시작");
        TransactionStatus statusB = txManger.getTransaction(new DefaultTransactionAttribute());
        log.info("✅트랜잭션-B 롤백");
        txManger.rollback(statusB);
    }


    @Test
    void multi_thread() throws InterruptedException {

        Runnable transactionACommit = () -> {
            log.info("✅트랜잭션-A 시작");
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            TransactionStatus statusA = txManger.getTransaction(new DefaultTransactionAttribute());
            log.info("✅트랜잭션-A 커밋");
            txManger.commit(statusA);
        };

        Runnable transactionBCommit = () -> {
            log.info("✅트랜잭션-B 시작");
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            TransactionStatus statusB = txManger.getTransaction(new DefaultTransactionAttribute());
            log.info("✅트랜잭션-B 커밋");
            txManger.commit(statusB);
        };

        Runnable transactionCCommit = () -> {
            log.info("✅트랜잭션-C 시작");
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            TransactionStatus statusC = txManger.getTransaction(new DefaultTransactionAttribute());
            log.info("✅트랜잭션-C 커밋");
            txManger.commit(statusC);
        };

        Runnable transactionDCommit = () -> {
            log.info("✅트랜잭션-D 시작");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            TransactionStatus statusD = txManger.getTransaction(new DefaultTransactionAttribute());
            log.info("✅트랜잭션-D 커밋");
            txManger.commit(statusD);
        };

        Thread threadA = new Thread(transactionACommit);
        Thread threadB = new Thread(transactionBCommit);
        Thread threadC = new Thread(transactionCCommit);
        Thread threadD = new Thread(transactionDCommit);

        threadA.start();
        threadB.start();
        threadC.start();
        threadD.start();

        threadA.join();
        threadB.join();
        threadC.join();
        threadD.join();
    }
}
