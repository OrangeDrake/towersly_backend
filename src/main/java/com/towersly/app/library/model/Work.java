package com.towersly.app.library.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Work {

    private long id;
    private String name;
    private boolean isCompleted;
    private String description;
    private int rank;
    private int expectedTime;
    private int actualTime;
    private long shelfId;

}
