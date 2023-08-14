package com.towersly.app.library.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ShelfContainingWorks {

    private long id;
    private String name;
    private boolean is_active;
    private int rank;
    private int next_work_rank;
    private int userId;
    private final List<Work> works = new LinkedList<Work>();

}
