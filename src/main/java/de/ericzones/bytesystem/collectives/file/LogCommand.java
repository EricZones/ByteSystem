// Created by Eric B. 19.02.2022 14:11
package de.ericzones.bytesystem.collectives.file;

public enum LogCommand {

    ALLOWED("(+)"),
    DISALLOWED("(-)"),
    UNKNOWN("(?)");

    private LogCommand(String symbol) {
        this.symbol = symbol;
    }

    private final String symbol;

    public String getSymbol() {
        return symbol;
    }
}
