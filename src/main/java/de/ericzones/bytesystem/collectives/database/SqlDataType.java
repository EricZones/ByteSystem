// Created by Eric B. 14.02.2022 18:45
package de.ericzones.bytesystem.collectives.database;

public enum SqlDataType {

    VARCHAR("VARCHAR(255)"),
    BOOLEAN("BIT"),
    BIGINT("BIGINT UNSIGNED"),
    DOUBLE("DOUBLE(1500,500)"),
    TEXT("MEDIUMTEXT");

    private SqlDataType(String tag) {
        this.tag = tag;
    }

    private final String tag;

    public String getTag() {
        return tag;
    }

}