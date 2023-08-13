package com.towersly.app.profile;

import com.towersly.app.profile.model.UserWithIdAndNextShelfRank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
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

    public void updateNextShelfRank(int id,  int nextShelfRank){
        String sql = "update public.user set next_shelf_rank = ? WHERE id = ?";
        jdbcTemplate.update(sql, nextShelfRank, id);
    }

}
