package com.towersly.app.library;

import com.towersly.app.library.model.*;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.*;

@Component
@Slf4j
@AllArgsConstructor
public class ShelfDAO {

    private JdbcTemplate jdbcTemplate;

    final private GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

// for more keys
//    public void create(Shelf shelf) {
//        String sql = "insert into public.shelf (name, is_active, next_work_rank, rank, user_id) values (?, true, 0, ?, ?) ";
//
//        int rowsAffected = jdbcTemplate.update(conn -> {
//
//            PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//
//            preparedStatement.setString(1, shelf.getName());
//            preparedStatement.setInt(2, shelf.getRank());
//            preparedStatement.setInt(3, shelf.getUser_id());
//
//            return preparedStatement;
//
//        }, generatedKeyHolder);
//
//
//        // Get auto-incremented ID
//        long id = (long) generatedKeyHolder.getKeys().get("id");
//
//        log.info("rowsAffected = {}, id={}", rowsAffected, id);
//
//        String name = (String) generatedKeyHolder.getKeys().get("name");
//        log.info("name: " + name);
//    }


    public Shelf create(Shelf shelf) {

        String sql = "insert into public.shelf (name, is_active, next_work_rank, rank, user_id) values (?, ?, 0, ?, ?)";

        var decParams = Arrays.asList(new SqlParameter(Types.VARCHAR, "name"), new SqlParameter(Types.BOOLEAN, "is_active"),
                new SqlParameter(Types.INTEGER, "rank"), new SqlParameter(Types.INTEGER, "user_id"));
        var pscf = new PreparedStatementCreatorFactory(sql, decParams) {
            {
                setReturnGeneratedKeys(true);
                setGeneratedKeysColumnNames("id");
            }
        };
        var psc = pscf.newPreparedStatementCreator(Arrays.asList(shelf.getName(), shelf.is_active(), shelf.getRank(), shelf.getUserId()));
        jdbcTemplate.update(psc, generatedKeyHolder);
        var id = Objects.requireNonNull(generatedKeyHolder.getKey()).longValue();
        shelf.setId(id);
        shelf.setUserId(0);
        return shelf;
    }

    ShelfWithIdAndNextWorkRankAndUserId readShelfWithIdAndNextWorkRankAndUserId(long id) {
        String sql = "SELECT next_work_rank, user_id from shelf where id = ?";
        ShelfWithIdAndNextWorkRankAndUserId shelf = null;
        try {
            shelf = jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rownumber) ->
                    new ShelfWithIdAndNextWorkRankAndUserId(id, rs.getInt("next_work_rank"), rs.getInt("user_id")));
        } catch (DataAccessException ex) {
            log.error("Shelf not found: " + id);
        }
        return shelf;
    }
}

