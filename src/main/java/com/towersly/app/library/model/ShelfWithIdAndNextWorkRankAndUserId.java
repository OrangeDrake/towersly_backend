package com.towersly.app.library.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ShelfWithIdAndNextWorkRankAndUserId {

    private long id;
    private int nextWorkRank;
    private int userId;

}
