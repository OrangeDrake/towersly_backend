package com.towersly.app.profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.towersly.app.profile.model.UserWithIdAndNextDistributionRank;
import com.towersly.app.profile.model.UserWithIdAndNextShelfRank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.Arrays;
import java.util.Objects;

@Component
@Slf4j
@AllArgsConstructor
public class UserDAO {

    private JdbcTemplate jdbcTemplate;

    final private ObjectMapper mapper = new ObjectMapper();
    final private GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

    public int getUserId(String name) {
        String sql = "SELECT id from public.user where name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, name);
        }catch (EmptyResultDataAccessException e){
            return 0;
        }
    }

    public UserWithIdAndNextShelfRank getUserWithIdAndNextShelfRank(String name) {
        String sql = "SELECT id, next_shelf_rank from public.user where name = ?";
        UserWithIdAndNextShelfRank userWithIdAndNextShelfRank = null;
        try {
            userWithIdAndNextShelfRank = jdbcTemplate.queryForObject(sql, new Object[] { name },
                    (rs, rownumber) -> new UserWithIdAndNextShelfRank(rs.getInt("id"), rs.getInt("next_shelf_rank")));
        } catch (DataAccessException ex) {
            log.error("User not found: " + name);
        }
        return userWithIdAndNextShelfRank;
    }

    public UserWithIdAndNextDistributionRank getUserWithIdAndNextDistributionRank(String name) {
        String sql = "SELECT id, next_distribution_rank from public.user where name = ?";
        UserWithIdAndNextDistributionRank userWithIdAndNextDistributionRank = null;
        try {
            userWithIdAndNextDistributionRank = jdbcTemplate.queryForObject(sql, new Object[] { name },
                    (rs, rownumber) -> new UserWithIdAndNextDistributionRank(rs.getInt("id"),
                            rs.getInt("next_distribution_rank")));
        } catch (DataAccessException ex) {
            log.error("User not found: " + name);
        }
        return userWithIdAndNextDistributionRank;
    }

    public void updateNextShelfRank(int id, int nextShelfRank) {
        String sql = "UPDATE public.user set next_shelf_rank = ? WHERE id = ?";
        jdbcTemplate.update(sql, nextShelfRank, id);
    }

    public void updateNextDistributionfRank(int id, int nexDistributionRank) {
        String sql = "UPDATE public.user set next_distribution_rank = ? WHERE id = ?";
        jdbcTemplate.update(sql, nexDistributionRank, id);
    }

    public void startTracking(int id, String tracking) {
        String sql = "UPDATE public.user SET tracking = cast(? as jsonb) WHERE id = ?";
        jdbcTemplate.update(sql, tracking, id);
    }

    public void updateTrackingToNUll(int id) {
        String sql = "UPDATE public.user SET tracking = null WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public JsonNode readTracking(int id) {
        String sql = "SELECT tracking from public.user where id = ? ";

        return jdbcTemplate.queryForObject(sql, new Object[] { id }, ((rs, rowNum) -> {
            JsonNode tracking = null;
            String trackingJson = rs.getString("tracking");

            if (trackingJson == null) {
                return mapper.createObjectNode();
            }

            try {
                tracking = mapper.readTree(trackingJson);
            } catch (JsonProcessingException e) {
                log.error("User: " + id + "| Tracking json parsing error");
                return null;
            }
            return tracking;
        }));
    }

    public int readNumberOfVisibleWorks(int id) {
        String sql = "SELECT visible_works from public.user where id = ? ";
        return jdbcTemplate.queryForObject(sql, Integer.class, id);
    }

    public void updateNumberOfVisibleWorks(int id, int numberOfVisibleWorks) {
        String sql = "UPDATE public.user SET visible_works = ? WHERE id = ?";
        jdbcTemplate.update(sql, numberOfVisibleWorks, id);
    }

    public int createNewProfile(String name, int nextDistributionRank, int nextShelfRank, int visibleWorks ) {
        String sql = "insert into public.user (name, next_distribution_rank, next_shelf_rank, visible_works ) values (?, ?, ?, ?)";
        var decParams = Arrays.asList(new SqlParameter(Types.VARCHAR, "name"),
                new SqlParameter(Types.INTEGER, "next_distribution_rank"), new SqlParameter(Types.INTEGER, "next_shelf_rank"),
                new SqlParameter(Types.INTEGER, "visible_works"));

        var pscf = new PreparedStatementCreatorFactory(sql, decParams) {
            {
                setReturnGeneratedKeys(true);
                setGeneratedKeysColumnNames("id");
            }
        };

        var psc = pscf.newPreparedStatementCreator(Arrays.asList(name, nextDistributionRank, nextShelfRank, visibleWorks ));
        jdbcTemplate.update(psc, generatedKeyHolder);
        return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
    }
}
