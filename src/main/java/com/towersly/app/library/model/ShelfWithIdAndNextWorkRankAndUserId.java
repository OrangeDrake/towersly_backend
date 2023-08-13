package com.towersly.app.library.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ShelfWithIdAndNextWorkRankAndUserId {

    private long id;
    private int next_work_rank;
    private int user_id;

}
