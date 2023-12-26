package com.towersly.app.library;

import com.towersly.app.library.model.Work;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
@AllArgsConstructor
public class WorkDAO {

    private JdbcTemplate jdbcTemplate;

    final private GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

    public Work create(Work work) {

        String sql = "insert into public.work (name, is_completed, rank, description, expected_time, actual_time, shelf_id) values (?, ?, ?, ?, ?, ?, ?)";
        var decParams = Arrays.asList(new SqlParameter(Types.VARCHAR, "name"), new SqlParameter(Types.BOOLEAN, "is_completed"), new SqlParameter(Types.INTEGER, "rank"), new SqlParameter(Types.VARCHAR, "description"), new SqlParameter(Types.INTEGER, "expected_time"), new SqlParameter(Types.INTEGER, "actual_time" ), new SqlParameter(Types.BIGINT, "shelf_id" ));

        var pscf = new PreparedStatementCreatorFactory(sql, decParams) {
            {
                setReturnGeneratedKeys(true);
                setGeneratedKeysColumnNames("id");
            }
        };
        //log.info("Debug: " + work.toString());
        var psc = pscf.newPreparedStatementCreator(Arrays.asList(work.getName(), work.isCompleted(), work.getRank(), work.getDescription(), work.getExpectedTime(), work.getActualTime(), work.getShelfId()));
        jdbcTemplate.update(psc, generatedKeyHolder);
        var id = Objects.requireNonNull(generatedKeyHolder.getKey()).longValue();
        work.setId(id);
        return work;
    }

        public void update(Work work, long id) {
            String sql = "update public.work set name = ?, description = ?, expected_time = ?, actual_time = ? WHERE id = ?";
            jdbcTemplate.update(sql, work.getName(), work.getDescription(), work.getExpectedTime(), work.getActualTime(), id);
    }

    public int updateActualTime(long workId, int durationInSeconds, long shelfId) {
        String sql = "update public.work set actual_time = actual_time + ? where (id = ? and shelf_id = ?) returning actual_time";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, durationInSeconds, workId, shelfId);

        for (Map row : rows) {
            return (int) row.get("actual_time");

        }
        return 0;
    }
}
