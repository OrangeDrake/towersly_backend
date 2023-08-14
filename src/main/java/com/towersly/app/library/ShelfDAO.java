package com.towersly.app.library;

import com.towersly.app.library.model.*;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
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

    public ShelfWithIdAndNextWorkRankAndUserId readShelfWithIdAndNextWorkRankAndUserId(long id) {
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

//    public List<Shelf> readAllShelves(int userId) {
//
//        String sql = "SELECT * from shelf where user_id = ?";
//
//        return jdbcTemplate.query(
//                sql, new Object[]{userId},
//                (rs, rowNum) ->
//                        new Shelf(
//                                rs.getLong("id"),
//                                rs.getString("name"),
//                                rs.getBoolean("is_active"),
//                                rs.getInt("rank"),
//                                rs.getInt("next_work_rank"),
//                                0
//                        )
//        );
//
//    }

//    public List<Shelf> readAllShelves(int userId) {
//
//        String sql = "SELECT * from shelf where user_id = ?";
//
//        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[]{userId});
//
//        List<Shelf> shelves = new LinkedList<>();
//        long prevousId = -1;
//        Shelf currentShelf = null;
//
//        for (Map row : rows) {
//            long id = (long) row.get("id");
//            if (id != prevousId) {
//                if (currentShelf != null) {
//                    shelves.add(currentShelf);
//                }
//                String name = (String) row.get("name");
//                boolean isActive = (boolean) row.get("is_active");
//                int rank = (int) row.get("rank");
//                int nextWorkRank = (int) row.get("next_work_rank");
//                currentShelf = new Shelf(id, name, isActive, rank, nextWorkRank, 0);
//                shelves.add(currentShelf);
//                prevousId = id;
//
//            }
//        }
//        return shelves;

    public List<ShelfContainingWorks> readAllShelves(int userId) {

        String sql = "SELECT s.id, s.name, s.is_active, s.rank, s.next_work_rank, w.id as w_id, w.expected_Time from shelf s left join work w on w.shelf_id = s.id where s.user_id = ?";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[]{userId});

        List<ShelfContainingWorks> shelves = new LinkedList<>();
        long prevousId = -1;
        ShelfContainingWorks currentShelf = null;

        for (Map row : rows) {
            long id = (long) row.get("id");
            if (id != prevousId) {
                if (currentShelf != null) {
                    shelves.add(currentShelf);
                }
                String name = (String) row.get("name");
                boolean isActive = (boolean) row.get("is_active");
                int rank = (int) row.get("rank");
                int nextWorkRank = (int) row.get("next_work_rank");
                currentShelf = new ShelfContainingWorks(id, name, isActive, rank, nextWorkRank, 0);
                shelves.add(currentShelf);
                prevousId = id;
            }
            Object workIdObject = row.get("w_id");
            if (workIdObject != null){
                long wotkId = (long) workIdObject;

                Work work = new Work(wotkId,"",true,"",0,0,0, id);
                currentShelf.getWorks().add(work);
            }

        }
        return shelves;
    }


}

