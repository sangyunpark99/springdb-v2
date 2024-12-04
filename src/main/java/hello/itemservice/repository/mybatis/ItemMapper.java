package hello.itemservice.repository.mybatis;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

// 마이바티스 매핑 XML을 호출해주는 매퍼 인터페이스이다.
@Mapper
public interface ItemMapper {

    void save(Item item);

    // Parameter가 2개가 넘어가는 경우엔 @Param을 넣어주어야 한다.
    void update(@Param("id") Long id, @Param("update")ItemUpdateDto updateParam);

    Optional<Item> findById(Long id);

    List<Item> findAll(ItemSearchCond itemSearchCond);
}