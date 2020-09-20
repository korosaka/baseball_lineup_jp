package com.websarva.wings.android.dasenapp;

public class StartingPlayerListItemData extends BasePlayerListItemData {
    private int orderNumber;
    private String position;

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

}
