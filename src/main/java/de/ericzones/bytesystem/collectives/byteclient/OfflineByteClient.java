// Created by Eric B. 13.11.2021 14:48
package de.ericzones.bytesystem.collectives.byteclient;

import de.ericzones.bytesystem.ByteSystem;

public class OfflineByteClient {

    private final String nickname, uniqueId, ipAddress, country, os, version, description, lastConnection, firstConnection, onlineTime;
    private final int databaseId, totalConnections;
    private final boolean admin;

    public OfflineByteClient(String nickname, String uniqueId, String ipAddress, String country, String os, String version, String description, int databaseId,
                             String lastConnection, String firstConnection, int totalConnections, long onlineTime, boolean admin) {
        this.nickname = nickname;
        this.uniqueId = uniqueId;
        this.ipAddress = ipAddress;
        this.country = country;
        this.os = os;
        this.version = version;
        this.description = description;
        this.databaseId = databaseId;
        this.lastConnection = lastConnection;
        this.firstConnection = firstConnection;
        this.totalConnections = totalConnections;

        long onlineTimeMillis = onlineTime;
        long seconds = 0, minutes = 0, hours = 0, days = 0;
        while(onlineTimeMillis > 1000) {
            onlineTimeMillis-=1000;
            seconds++;
        }
        while(seconds > 60) {
            seconds-=60;
            minutes++;
        }
        while(minutes > 60) {
            minutes-=60;
            hours++;
        }
        while(hours > 24) {
            hours-=24;
            days++;
        }
        this.onlineTime = days + "d " + hours + "h " + minutes + "m";
        this.admin = admin;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getCountry() {
        return country;
    }

    public String getOS() {
        return os;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public String getLastConnection() {
        return lastConnection;
    }

    public String getFirstConnection() {
        return firstConnection;
    }

    public int getTotalConnections() {
        return totalConnections;
    }

    public String getOnlineTime() {
        return onlineTime;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isOnline() {
        if(ByteSystem.getQuery().getApi().getClients().stream().anyMatch(client -> client.getUniqueIdentifier().equals(this.uniqueId)))
            return true;
        return false;
    }

    public ByteClient getByteClient() {
        if(!isOnline()) return null;
        return ByteSystem.getByteClientManager().getByteClientByUniqueId(this.uniqueId);
    }

}
