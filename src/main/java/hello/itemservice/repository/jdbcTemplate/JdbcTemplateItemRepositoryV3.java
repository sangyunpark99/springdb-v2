package hello.itemservice.repository.jdbcTemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * SimpleJdbcInsert
 */
@Slf4j
@Repository
public class JdbcTemplateItemRepositoryV3 implements ItemRepository {

    // private final JdbcTemplate template;
    private final NamedParameterJdbcTemplate template;
    private final SimpleJdbcInsert jdbcInsert;

    public JdbcTemplateItemRepositoryV3(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("item")
                .usingGeneratedKeyColumns("id");
                //.usingColumns("item_name","price","quantity"); 생략 가능
    }

    @Override
    public Item save(Item item) { // sql
        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(item);
        Number key = jdbcInsert.executeAndReturnKey(param);
        item.setId(key.longValue());
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        // 작성해준 순서대로 파라미터 바인딩을 해주어야 한다.
        String sql = "update item set item_name=:itemName, price=:price, quantity=:quantity where id=:id";

        // updateParam에는 id가 존재하지 않기때문에, MapSqlParameterSource를 사용해준다.
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("itemName", updateParam.getItemName())
                .addValue("price", updateParam.getPrice())
                .addValue("quantity", updateParam.getQuantity())
                .addValue("id", itemId);

        template.update(sql, param);
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name, price, quantity from item where id =:id";

        // queryForObject는 값이 없으면 EmptyResultDataAccessException 예외가 터진다.
        try {
            Map<String, Object> param = Map.of("id",id);
            Item item = template.queryForObject(sql, param, itemRowMapper());
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            // 데이터 결과가 없는 경우 EmptyResultDataAccessException 예외 발생
            // 결과가 둘 이상이면 IncorrectResultSizeAccessException 예외 발생
            return Optional.empty(); // 예외 발생시 Optional.empty() return
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(cond);

        String sql = "select id, item_name, price, quantity from item";
        // 동적 쿼리
        // 상황에 따라 들어가는 sql문이 다르다.
        // ex) item_name만 있는 경우, price만 있는 경우
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }

        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) { // item이름이 있는 경우
            sql += " item_name like concat('%',:itemName,'%')";
            andFlag = true;
        }

        if (maxPrice != null) { // 가격이 있는 경우
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= :maxPrice";
        }

        log.info("sql={}", sql);
        return template.query(sql, param, itemRowMapper());
        // RowMapper는 데이터베이스의 반환 결과인 ResultSet을 객체로 변환한다.
        // 결과값이 하나일때, 여러개일 때 다 사용된다.
        // 결과가 없는 경우 빈 컬렉션을 반환한다.
        // JdbcTemplate이 mapper를 알아서 반복문 돌려준다.
    }

    // Item 객체를 생성해서 반환해준다.
    private RowMapper<Item> itemRowMapper() { // 데이터 매핑 결과
        return BeanPropertyRowMapper.newInstance(Item.class); // camel 변환 지원(DB는 item_name)
    }
}