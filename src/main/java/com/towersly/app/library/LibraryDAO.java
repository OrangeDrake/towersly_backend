package com.towersly.app.library;

import com.towersly.app.library.model.*;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.*;

@Component
@Slf4j
@AllArgsConstructor
public class LibraryDAO {

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

        String sql = "insert into public.shelf (name, is_active, next_work_rank, rank, user_id) values (?, ?, 0, ?, ?) ";

        var decParams = List.of(new SqlParameter(Types.VARCHAR, "name"), new SqlParameter(Types.BOOLEAN, "is_active"),
                new SqlParameter(Types.INTEGER, "rank"), new SqlParameter(Types.INTEGER, "user_id"));
        var pscf = new PreparedStatementCreatorFactory(sql, decParams) {
            {
                setReturnGeneratedKeys(true);
                setGeneratedKeysColumnNames("id");
            }
        };

        var psc = pscf.newPreparedStatementCreator(List.of(shelf.getName(), shelf.is_active(), shelf.getRank(), shelf.getUser_id()));

        jdbcTemplate.update(psc, generatedKeyHolder);

        var id = Objects.requireNonNull(generatedKeyHolder.getKey()).longValue();
        shelf.setId(id);
        shelf.setUser_id(0);
        return shelf;
    }
}
