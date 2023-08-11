package com.towersly.app.library;

import com.towersly.app.library.model.*;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class LibraryDAO {

    private JdbcTemplate jdbcTemplate;

//    RowMapper<Course> rowMapper = (rs, rowNum) -> {
//        Course course = new Course();
//        course.setCourseId(rs.getInt("course_id"));
//        course.setTitle(rs.getString("title"));
//        course.setDescription(rs.getString("description"));
//        course.setLink(rs.getString("link"));
//        return course;
//    };
    public void create(Shelf shelf) {
        String sql = "insert into shelf (name, is_active, next_work_rank, rank, user_id) values (?, true, 0, ?, ?) ";
        int insert = jdbcTemplate.update(sql, shelf.getName(), shelf.getRank(),shelf.getUser_id());
        if(insert == 1) {
            log.info("User: " + shelf.getUser_id() + "| New shelf created: "+ shelf.getName());
        }
    }

}
