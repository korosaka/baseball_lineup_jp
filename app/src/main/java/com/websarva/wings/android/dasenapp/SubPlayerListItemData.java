package com.websarva.wings.android.dasenapp;

public class SubPlayerListItemData {
    private int id;
    private Boolean isPitcher;
    private Boolean isBatter;
    private Boolean isRunner;
    private Boolean isFielder;
    private String name;

    public SubPlayerListItemData(
            int id,
            Boolean isPitcher,
            Boolean isBatter,
            Boolean isRunner,
            Boolean isFielder,
            String name) {

        this.id = id;
        this.isPitcher = isPitcher;
        this.isBatter = isBatter;
        this.isRunner = isRunner;
        this.isFielder = isFielder;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getBatter() {
        return isBatter;
    }

    public Boolean getFielder() {
        return isFielder;
    }

    public Boolean getPitcher() {
        return isPitcher;
    }

    public Boolean getRunner() {
        return isRunner;
    }
}
