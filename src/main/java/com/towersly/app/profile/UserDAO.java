package com.towersly.app.profile;

import com.towersly.app.profile.model.UserWithIdAndNextShelfRank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class UserDAO {

    private JdbcTemplate jdbcTemplate;

    public int getUserId(String name) {
        String sql = "select id from public.user where name = ?";
        int userID = jdbcTemplate.queryForObject(sql, Integer.class, name);
        log.info("User: " + userID);
        return userID;
    }

    public UserWithIdAndNextShelfRank getUserWithIdAndNextShelfRank(String name) {
        String sql = "select id, next_shelf_rank from public.user where name = ?";
        UserWithIdAndNextShelfRank userWithIdAndNextShelfRank = jdbcTemplate.queryForObject(sql, new Object[]{name}, (rs, rownumber) ->
                new UserWithIdAndNextShelfRank(rs.getInt("id"), rs.getInt("next_shelf_rank"))
        );
        return userWithIdAndNextShelfRank;
    }

}
