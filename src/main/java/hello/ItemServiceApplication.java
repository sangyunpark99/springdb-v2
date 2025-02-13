package hello;

import hello.itemservice.TestDataInit;
import hello.itemservice.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Slf4j
//@Import(MemoryConfig.class)
//@Import(JdbcTemplateV1Config.class)
//@Import(JdbcTemplateV3Config.class)
@SpringBootApplication
//@Import(JpaConfig.class)
//@Import(SpringDataJpaConfig.class)
//@Import(QuerydslConfig.class)
//@Import(V2Config.class)
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

//	@Bean
//	@Profile("local")
//	public TestDataInit testDataInit(ItemRepository itemRepository) {
//		return new TestDataInit(itemRepository);
//	}

	// 아래처럼 빈으로 등록을 하지 않아도 된다.
//	@Bean
//	@Profile("test") // Profile이 test인 경우
//	public DataSource dataSource() {
//		log.info("메모리 데이터베이스 초기화");
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//		dataSource.setDriverClassName("org.h2.Driver"); // h2 database 드라이버 지정
//		dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1"); // database 연결이 닫혀도 데이터베이스가 삭제되지 않도록 제어
//		// db;DB_CLOSE_DELAY=-1 ; 사용하는 부분을 유의해야 한다.
//		// mem:db => 임베디드 모드(메모리 모드)로 동작하는 H2 데이터베이스를 사용할 수 있다.
//		dataSource.setUsername("sa");
//		dataSource.setPassword("");
//		return dataSource;
//	}

}
