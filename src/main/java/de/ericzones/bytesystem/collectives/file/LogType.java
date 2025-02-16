// Created by Eric B. 19.02.2022 11:10
package de.ericzones.bytesystem.collectives.file;

public enum LogType {

    MESSAGE_PRIVATE("PRIVATE MSG"),
    MESSAGE_CHANNEL("CHANNEL MSG"),
    MESSAGE_SERVER("SERVER MSG"),
    COMMAND("COMMAND"),
    KICK_CHANNEL("CHANNEL KICK"),
    KICK_SERVER("SERVER KICK"),
    BAN("BAN"),
    MOVE("MOVE"),
    SWITCH("SWITCH"),
    CONNECT("CONNECT"),
    DISCONNECT("DISCONNECT"),
    CHANNEL_EDIT("EDIT CHANNEL");

    private LogType(String text) {
        this.text = text;
    }

    private final String text;

    public String getText() {
        return text;
    }

}
