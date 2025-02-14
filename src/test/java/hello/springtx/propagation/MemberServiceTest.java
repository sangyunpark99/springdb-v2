package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LogRepository logRepository;

    /**
     * memberService @Transactional: OFF
     * memberRepository @Transactional: ON
     * logRepository @Transactional : ON
     */
    @Test
    void outerTxOff_success() {
        // given
        String username = "outerTxOff_success";

        // when
        memberService.joinV1(username);

        // then 데이터 정상 실행
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService @Transactional: OFF
     * memberRepository @Transactional: ON
     * logRepository @Transactional : ON Exception
     */
    @Test
    void outerTxOff_fail() { // 회원 저장, 로그는 롤백
        // given
        String username = "로그예외";

        // when, then
        Assertions.assertThatThrownBy(() -> memberService.joinV1(username)).isInstanceOf(RuntimeException.class);
        assertTrue(memberRepository.find(username).isPresent()); // 저장
        assertTrue(logRepository.find(username).isEmpty()); // 롤백
    }

    @Test
    void singleTx() {
        // given
        String username = "outerTxOff_success";

        // when
        memberService.joinV1(username);

        // then 데이터 정상 실행
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }
}