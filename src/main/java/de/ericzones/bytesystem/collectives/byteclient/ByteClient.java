// Created by Eric B. 08.11.2021 20:02
package de.ericzones.bytesystem.collectives.byteclient;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.DatabaseClient;

import java.text.SimpleDateFormat;

public class ByteClient {

    private ClientInfo clientInfo;
    private DatabaseClient databaseClient;

    private final String nickname, uniqueId, ipAddress, description;
    private final Onlinetime onlinetime;
    private String os, version, country;
    private final int databaseId;
    private int clientId;
    private boolean admin;

    public ByteClient(Client client, ClientInfo clientInfo, Onlinetime onlinetime, boolean admin) {
        this.clientInfo = clientInfo;
        this.nickname = client.getNickname();
        this.uniqueId = client.getUniqueIdentifier();
        this.ipAddress = client.getIp();
        this.description = clientInfo.getDescription();
        this.os = client.getPlatform();
        String version = client.getVersion();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < version.length(); i++) {
            if(version.charAt(i) != '[')
                stringBuilder.append(version.charAt(i));
            else
                break;
        }
        this.version = stringBuilder.toString().trim();
        this.country = client.getCountry();
        this.databaseId = client.getDatabaseId();
        this.clientId = client.getId();
        this.onlinetime = onlinetime;
        this.admin = admin;
    }

    public ByteClient(DatabaseClient databaseClient, Onlinetime onlinetime, boolean admin) {
        this.databaseClient = databaseClient;
        this.nickname = databaseClient.getNickname();
        this.uniqueId = databaseClient.getUniqueIdentifier();
        this.ipAddress = databaseClient.getLastIp();
        this.description = databaseClient.getDescription();
        this.databaseId = databaseClient.getDatabaseId();
        this.onlinetime = onlinetime;
        this.admin = admin;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getUniqueId() {
        return this.uniqueId;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public String getDescription() {
        return this.description;
    }

    public String getOS() {
        return this.os;
    }

    public String getVersion() {
        return this.version;
    }

    public String getCountry() {
        return this.country;
    }

    public int getDatabaseId() {
        return this.databaseId;
    }

    public int getClientId() {
        return clientId;
    }

    public String getFirstConnection() {
        if(clientInfo != null)
            return new SimpleDateFormat("dd.MM.yyyy HH:mm").format(clientInfo.getCreatedDate());
        else
            return new SimpleDateFormat("dd.MM.yyyy HH:mm").format(databaseClient.getCreatedDate());
    }

    public int getTotalConnections() {
        if(clientInfo != null)
            return clientInfo.getTotalConnections();
        else
            return databaseClient.getTotalConnections();
    }

    public Onlinetime getOnlinetime() {
        return onlinetime;
    }

    public boolean isAdmin() {
        return admin;
    }

}
