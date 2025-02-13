package hello.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LogRepository {

    private final EntityManager em;

    @Transactional
    public void save(Log message) {
        log.info("로그 저장");
        em.persist(message);

        if(message.getMessage().equals("로그예외")) {
            log.info("log 저장시 예외 발생");
            throw new RuntimeException(); // 롤백 발생
        }
    }

    public Optional<Log> find(String message) {
        return em.createQuery("select l from Log l where message = :message", Log.class)
                .setParameter("message", message)
                .getResultList().stream().findAny();
    }
}
