package com.towersly.app.library.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Shelf {

    private long id;
    private String name;
    private boolean is_active;
    private int rank;
    private int next_work_rank;
    private int user_id;

}
