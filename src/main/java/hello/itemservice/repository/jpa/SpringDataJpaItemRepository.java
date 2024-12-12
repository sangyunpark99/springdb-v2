package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataJpaItemRepository extends JpaRepository<Item, Long> {

    // 조건 없이 검색하는건 Spring Data Jpa가 기본적으로 CRUD 기능이 제공된다.

    List<Item> findByItemNameLike(@Param(("itemName")) String itemName);

    List<Item> findByPriceLessThanEqual(Integer price);

    // 이름이 너무 길다
    List<Item> findByItemNameLikeAndPriceLessThanEqual(String itemName, Integer price);

    // 이름이 너무 길어진 경우 아래와 같이 @Query를 사용한다.
    // 실무에서 쿼리가 너무 복잡한 경우 Querydsl을 사용하면 된다.
    @Query("select i from Item i where i.itemName like :itemName and i.price <= :price")
    List<Item> findItems(@Param("itemName") String itemName, @Param("price") Integer price);
}
