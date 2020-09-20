package com.websarva.wings.android.dasenapp;

public class StartingPlayerListItemData {
    private int orderNumber;
    private String position;
    private String name;

    public StartingPlayerListItemData(int orderNum, String position, String name) {
        this.orderNumber = orderNum;
        this.position = position;
        this.name = name;
    }

    public int getOrderNum() {
        return orderNumber;
    }

    public String getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }
}
