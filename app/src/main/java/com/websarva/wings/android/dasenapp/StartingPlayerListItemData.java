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

    public int getItemOrderNumber() {
        return orderNumber;
    }

    public String getItemPosition() {
        return position;
    }

    public String getItemName() {
        return name;
    }
}
