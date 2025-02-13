//package hello.itemservice.config;
//
//import hello.itemservice.repository.ItemRepository;
//import hello.itemservice.repository.jpa.JpaItemRepositoryV3;
//import hello.itemservice.repository.v2.ItemQueryRepositoryV2;
//import hello.itemservice.repository.v2.ItemRepositoryV2;
//import hello.itemservice.service.ItemService;
//import hello.itemservice.service.ItemServiceV2;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.persistence.EntityManager;
//
//@Configuration
//@RequiredArgsConstructor
//public class V2Config {
//
//    private final EntityManager em;
//    private final ItemRepositoryV2 itemRepositoryV2; // springDataJpa 제공
//
//    @Bean
//    public ItemService itemService() {
//        return new ItemServiceV2(itemRepositoryV2, itemQueryRepositoryV2());
//    }
//
//    // 원래는 컴포넌트 스캔으로 한다. 굳이 빈으로 등록하지 않아도 된다.
//
//    @Bean
//    public ItemQueryRepositoryV2 itemQueryRepositoryV2() {
//        return new ItemQueryRepositoryV2(em);
//    }
//
//    // 아래 코드는 테스트 코드에서 사용중이므로 작성해줌
//    @Bean
//    public ItemRepository itemRepository() {
//        return new JpaItemRepositoryV3(em); // jpa
//    }
//
//}
