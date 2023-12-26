package com.towersly.app.library.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Shelf {

    private long id;
    private String name;
    private boolean isActive;
    private int rank;
    private int nextWorkRank;
    private int userId;

}
