package com.towersly.app.profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.towersly.app.profile.model.UserWithIdAndNextDistributionRank;
import com.towersly.app.profile.model.UserWithIdAndNextShelfRank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class UserDAO {

    private JdbcTemplate jdbcTemplate;

    final private ObjectMapper mapper = new ObjectMapper();

    public int getUserId(String name) {
        String sql = "select id from public.user where name = ?";
        int userID = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return userID;
    }

    public UserWithIdAndNextShelfRank getUserWithIdAndNextShelfRank(String name) {
        String sql = "select id, next_shelf_rank from public.user where name = ?";
        UserWithIdAndNextShelfRank userWithIdAndNextShelfRank = null;
        try {
            userWithIdAndNextShelfRank = jdbcTemplate.queryForObject(sql, new Object[]{name}, (rs, rownumber) ->
                    new UserWithIdAndNextShelfRank(rs.getInt("id"), rs.getInt("next_shelf_rank")));
        } catch (DataAccessException ex) {
            log.error("User not found: " + name);
        }
        return userWithIdAndNextShelfRank;
    }

    public UserWithIdAndNextDistributionRank getUserWithIdAndNextDistributionRank(String name) {
        String sql = "select id, next_distribution_rank from public.user where name = ?";
        UserWithIdAndNextDistributionRank userWithIdAndNextDistributionRank = null;
        try {
            userWithIdAndNextDistributionRank = jdbcTemplate.queryForObject(sql, new Object[]{name}, (rs, rownumber) ->
                    new UserWithIdAndNextDistributionRank(rs.getInt("id"), rs.getInt("next_distribution_rank")));
        } catch (DataAccessException ex) {
            log.error("User not found: " + name);
        }
        return userWithIdAndNextDistributionRank;
    }

    public void updateNextShelfRank(int id, int nextShelfRank) {
        String sql = "update public.user set next_shelf_rank = ? WHERE id = ?";
        jdbcTemplate.update(sql, nextShelfRank, id);
    }

    public void updateNextDistributionfRank(int id, int nexDistributionRank) {
        String sql = "update public.user set next_distribution_rank = ? WHERE id = ?";
        jdbcTemplate.update(sql, nexDistributionRank, id);
    }

    public void startTracking(int id, String tracking) {
        String sql = "UPDATE public.user SET tracking = cast(? as jsonb) WHERE id = ?";
        jdbcTemplate.update(sql, tracking, id);
    }

    public void stopTracking(int id) {
        String sql = "UPDATE public.user SET tracking = null WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public JsonNode readTracking(int id) {
        String sql = "SELECT tracking from public.user where id = ? ";

        return jdbcTemplate.queryForObject(sql, new Object[]{id}, ((rs, rowNum) -> {
            JsonNode tracking = null;
            String trackingJson = rs.getString("tracking");

            try {
                if (trackingJson != null) {
                    tracking = mapper.readTree(trackingJson);
                }
            } catch (JsonProcessingException e) {
                log.error("User: " + id + "| Tracking json parsing error");
                return null;
            }
            return tracking;
        }));
    }
}
