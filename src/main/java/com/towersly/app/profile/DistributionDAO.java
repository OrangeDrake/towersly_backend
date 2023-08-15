package com.towersly.app.profile;

import com.towersly.app.library.model.Shelf;
import com.towersly.app.library.model.ShelfContainingWorks;
import com.towersly.app.library.model.ShelfWithIdAndNextWorkRankAndUserId;
import com.towersly.app.library.model.Work;
import com.towersly.app.planning.model.Distribution;
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
public class DistributionDAO {

    private JdbcTemplate jdbcTemplate;

    final private GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
    public Distribution create(Distribution distribution) {

        String sql = "insert into public.distribution (name, is_active, rank, user_id) values (?, ?, ?, ?)";

        var decParams = Arrays.asList(new SqlParameter(Types.VARCHAR, "name"), new SqlParameter(Types.BOOLEAN, "is_active"),
                new SqlParameter(Types.INTEGER, "rank"), new SqlParameter(Types.INTEGER, "user_id"));
        var pscf = new PreparedStatementCreatorFactory(sql, decParams) {
            {
                setReturnGeneratedKeys(true);
                setGeneratedKeysColumnNames("id");
            }
        };
        var psc = pscf.newPreparedStatementCreator(Arrays.asList(distribution.getName(), distribution.isActive(), distribution.getRank(), distribution.getUserId()));
        jdbcTemplate.update(psc, generatedKeyHolder);
        var id = Objects.requireNonNull(generatedKeyHolder.getKey()).longValue();
        distribution.setId(id);
        distribution.setUserId(0);
        return distribution;
    }

//    public ShelfWithIdAndNextWorkRankAndUserId readShelfWithIdAndNextWorkRankAndUserId(long id) {
//        String sql = "SELECT next_work_rank, user_id from shelf where id = ?";
//        ShelfWithIdAndNextWorkRankAndUserId shelf = null;
//        try {
//            shelf = jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rownumber) ->
//                    new ShelfWithIdAndNextWorkRankAndUserId(id, rs.getInt("next_work_rank"), rs.getInt("user_id")));
//        } catch (DataAccessException ex) {
//            log.error("Shelf not found: " + id);
//        }
//        return shelf;
//    }
//    public List<ShelfContainingWorks> readAllShelves(int userId) {
//
//        String sql = "SELECT s.id, s.name, s.is_active, s.rank, w.id as w_id, w.name as w_name, w.is_completed as w_is_completed, w.description as w_description, w.rank as w_rank, w.expected_Time as w_expected_Time, w.actual_Time as w_actual_time from shelf s left join work w on w.shelf_id = s.id where s.user_id = ?";
//
//        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[]{userId});
//
//        List<ShelfContainingWorks> shelves = new LinkedList<>();
//        long prevousId = -1;
//        ShelfContainingWorks currentShelf = null;
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
//                currentShelf = new ShelfContainingWorks(id, name, isActive, rank);
//                shelves.add(currentShelf);
//                prevousId = id;
//            }
//            Object workIdObject = row.get("w_id");
//            if (workIdObject != null){
//                long wotkId = (long) workIdObject;
//                String workName = (String) row.get("w_name");
//                boolean workIsComelete = (boolean) row.get("w_is_completed");
//                String workDescription = (String) row.get("w_description");
//                int workRank = (int) row.get("w_rank");
//                int workExpectedTime = (int) row.get("w_expected_Time");
//                int workActualaTime = (int) row.get("w_actual_Time");
//                Work work = new Work(wotkId,workName,workIsComelete,workDescription,workRank,workExpectedTime,workActualaTime, id);
//                currentShelf.getWorks().add(work);
//            }
//        }
//        return shelves;
//    }
//
//    public void updateNextWorkfRank(long id, int nextWorkRank) {
//        String sql = "update public.shelf set next_work_rank = ? WHERE id = ?";
//        jdbcTemplate.update(sql, nextWorkRank, id);
//    }
}

