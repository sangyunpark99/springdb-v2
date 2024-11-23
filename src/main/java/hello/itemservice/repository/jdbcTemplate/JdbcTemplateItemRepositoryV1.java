package hello.itemservice.repository.jdbcTemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class JdbcTemplateItemRepositoryV1 implements ItemRepository {

    private final JdbcTemplate template;

    public JdbcTemplateItemRepositoryV1(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {

        String sql = "insert into item (item_name, price, quantity) values (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> { // update를 사용해서 데이터를 변경해준다.
            // 반환값은 영향 받은 row 수를 반환한다.
            // 자동 증가 키를 사용할 경우 keyHolder를 사용해야 한다.
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, item.getItemName());
            ps.setInt(2, item.getPrice());
            ps.setInt(3, item.getQuantity());

            return ps;
        }, keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);

        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        // 작성해준 순서대로 파라미터 바인딩을 해주어야 한다.
        String sql = "update item set item_name=?, price=?, quantity=? where id=?";
        template.update(sql, updateParam.getItemName(), updateParam.getPrice(), updateParam.getQuantity(), itemId);
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, itemName, price, quantity from item where id = ?";

        // queryForObject는 값이 없으면 EmptyResultDataAccessException 예외가 터진다.
        try {
            Item item = template.queryForObject(sql, itemRowMapper(), id);
            return Optional.of(item);
        }catch (EmptyResultDataAccessException e) {
            // 데이터 결과가 없는 경우 EmptyResultDataAccessException 예외 발생
            // 결과가 둘 이상이면 IncorrectResultSizeAccessException 예외 발생
            return Optional.empty(); // 예외 발생시 Optional.empty() return
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        String sql = "select id, item_name, price, quantity from item";
        // 동적 쿼리
        // 상황에 따라 들어가는 sql문이 다르다.
        // ex) item_name만 있는 경우, price만 있는 경우
        if(StringUtils.hasText(itemName) || maxPrice != null) {
            sql += "where";
        }

        boolean andFlag = false;
        List<Object> param = new ArrayList<>();
        if(StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',?,'%')";
            param.add(itemName);
            andFlag = true;
        }

        if(maxPrice != null) {
            if(andFlag) {
                sql += " and";
            }
            sql += " price <= ?";
            param.add(maxPrice);
        }

        log.info("sql={}",sql);
        return template.query(sql, itemRowMapper(), param.toArray());
        // RowMapper는 데이터베이스의 반환 결과인 ResultSet을 객체로 변환한다.
        // 결과값이 하나일때, 여러개일 때 다 사용된다.
        // 결과가 없는 경우 빈 컬렉션을 반환한다.
        // JdbcTemplate이 mapper를 알아서 반복문 돌려준다.
    }

    // Item 객체를 생성해서 반환해준다.
    private RowMapper<Item> itemRowMapper() { // 데이터 매핑 결과
        return ((rs, rowNum) -> {
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setItemName(rs.getString("item_name"));
            item.setPrice(rs.getInt("price"));
            item.setQuantity(rs.getInt("quantity"));
            return item;
        });
    }
}