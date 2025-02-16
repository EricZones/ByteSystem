// Created by Eric B. 07.11.2021 16:35
package de.ericzones.bytesystem.collectives.byteclient;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.DatabaseClient;
import de.ericzones.bytesystem.ByteSystem;
import de.ericzones.bytesystem.collectives.database.DatabaseHandler;

import java.util.*;

public class ByteClientManager extends SqlByteClient {

    private final Map<String, Onlinetime> byteClientCache = new HashMap<>();
    private final Map<Integer, String> byteClientIds = new HashMap<>();

    public ByteClientManager(DatabaseHandler databaseHandler, int databaseId, int id) {
        super(databaseHandler, databaseId, id);
    }

    public void setByteClientOnline(ByteClient byteClient) {
        if(byteClientExistsByUniqueId(byteClient.getUniqueId()))
            updateByteClient(byteClient, true);
        else
            createByteClient(byteClient, true);
        if(!byteClientCache.containsKey(byteClient.getUniqueId()))
            byteClientCache.put(byteClient.getUniqueId(), new Onlinetime(getByteClientOnlinetimeMillis(byteClient.getUniqueId())));
        byteClientIds.put(byteClient.getClientId(), byteClient.getUniqueId());
    }

    public void setByteClientOffline(ByteClient byteClient) {
        if(byteClientExistsByUniqueId(byteClient.getUniqueId()))
            updateByteClient(byteClient, false);
        else
            createByteClient(byteClient, false);
        byteClientCache.remove(byteClient.getUniqueId());
        byteClientIds.remove(byteClient.getClientId());
    }

    public List<ByteClient> getByteClients() {
        List<ByteClient> byteClients = new ArrayList<>();
        for(Client current : ByteSystem.getQuery().getApi().getClients()) {
            ClientInfo currentInfo = ByteSystem.getQuery().getApi().getClientByUId(current.getUniqueIdentifier());
            if(current.isServerQueryClient()) continue;
            byteClients.add(new ByteClient(current, currentInfo, getOnlinetime(current.getUniqueIdentifier()), isByteClientAdmin(current.getUniqueIdentifier())));
        }
        return byteClients;
    }

    public boolean isByteClientOnline(String nickname) {
        return ByteSystem.getQuery().getApi().getClients().stream().anyMatch(client -> client.getNickname().equalsIgnoreCase(nickname));
    }

    public List<OfflineByteClient> getAdminByteClients() {
        List<OfflineByteClient> adminByteClients = new ArrayList<>();
        List<String> adminUniqueIds = getAdminByteClientUniqueIds();
        for(String current : adminUniqueIds) {
            OfflineByteClient offlineByteClient = getOfflineByteClientByUniqueId(current);
            if(offlineByteClient == null) continue;
            adminByteClients.add(offlineByteClient);
        }
        return adminByteClients;
    }

    public List<OfflineByteClient> getTopOnlinetimeByteClients(int amount) {
        List<OfflineByteClient> byteClients = new ArrayList<>();
        List<String> byteClientUniqueIds = getTopOnlinetimeUniqueIds(amount);
        for(String current : byteClientUniqueIds)
            byteClients.add(getOfflineByteClientByUniqueId(current));
        return byteClients;
    }

    public void restoreOnlinetime(String uniqueId) {
        //if(byteClientCache.containsKey(uniqueId)) return;
        //Client client = ByteSystem.getQuery().getApi().getClientByUId(uniqueId);
        //if(client == null) return;
        //byteClientCache.put(uniqueId, new Onlinetime(getByteClientOnlinetimeMillis(uniqueId)+client.getIdleTime()));
    }

    public void setByteClientAdmin(String uniqueId, boolean admin) {
        updateByteClientAdmin(uniqueId, admin);
    }

    public ByteClient getByteClient(Client client) {
        ClientInfo clientInfo = ByteSystem.getQuery().getApi().getClientByUId(client.getUniqueIdentifier());
        return new ByteClient(client, clientInfo, getOnlinetime(client.getUniqueIdentifier()), isByteClientAdmin(client.getUniqueIdentifier()));
    }

    public ByteClient getByteClient(ClientInfo clientInfo) {
        Client client = ByteSystem.getQuery().getApi().getClientByNameExact(clientInfo.getNickname(), false);
        return new ByteClient(client, clientInfo, getOnlinetime(client.getUniqueIdentifier()), isByteClientAdmin(client.getUniqueIdentifier()));
    }

    public ByteClient getByteClientByUniqueId(String uniqueId) {
        ClientInfo clientInfo = ByteSystem.getQuery().getApi().getClientByUId(uniqueId);
        Client client = ByteSystem.getQuery().getApi().getClientByNameExact(clientInfo.getNickname(), false);
        return new ByteClient(client, clientInfo, getOnlinetime(uniqueId), isByteClientAdmin(uniqueId));
    }

    public ByteClient getByteClientByNickname(String nickname) {
        Client client = ByteSystem.getQuery().getApi().getClientByNameExact(nickname, true);
        ClientInfo clientInfo = ByteSystem.getQuery().getApi().getClientByUId(client.getUniqueIdentifier());
        return new ByteClient(client, clientInfo, getOnlinetime(client.getUniqueIdentifier()), isByteClientAdmin(client.getUniqueIdentifier()));
    }

    public ByteClient getByteClient(int clientId) {
        String uniqueId = byteClientIds.get(clientId);
        if(uniqueId == null) return null;
        DatabaseClient databaseClient = ByteSystem.getQuery().getApi().getDatabaseClientByUId(uniqueId);
        return new ByteClient(databaseClient, getOnlinetime(uniqueId), isByteClientAdmin(uniqueId));
    }

    public List<OfflineByteClient> getOfflineByteClientByNickname(String nickname) {
        List<OfflineByteClient> offlineByteClients = new ArrayList<>();
        List<String[]> byteClientInformation = getByteClientInformationByNickname(nickname);
        for(String[] current : byteClientInformation) {
            String uniqueId = current[1];
            nickname = current[0];
            String ipAddress = current[3];
            String country = current[4];
            String os = current[5];
            String version = current[6];
            String description = current[7];
            int databaseId = Integer.parseInt(current[2]);
            String lastConnection = current[8];
            String firstConnection = current[9];
            int totalConnections = Integer.parseInt(current[10]);
            long onlineTime = Long.parseLong(current[11]);
            boolean admin = Boolean.parseBoolean(current[12]);
            offlineByteClients.add(new OfflineByteClient(nickname, uniqueId, ipAddress, country, os, version, description, databaseId, lastConnection, firstConnection, totalConnections, onlineTime, admin));
        }
        return offlineByteClients;
    }

    public OfflineByteClient getOfflineByteClientByUniqueId(String uniqueId) {
        String[] byteClientInformation = getByteClientInformationByUniqueId(uniqueId);
        String nickname = byteClientInformation[0];
        String ipAddress = byteClientInformation[3];
        String country = byteClientInformation[4];
        String os = byteClientInformation[5];
        String version = byteClientInformation[6];
        String description = byteClientInformation[7];
        int databaseId = Integer.parseInt(byteClientInformation[2]);
        String lastConnection = byteClientInformation[8];
        String firstConnection = byteClientInformation[9];
        int totalConnections = Integer.parseInt(byteClientInformation[10]);
        long onlineTime = Long.parseLong(byteClientInformation[11]);
        boolean admin = Boolean.parseBoolean(byteClientInformation[12]);
        return new OfflineByteClient(nickname, uniqueId, ipAddress, country, os, version, description, databaseId, lastConnection, firstConnection, totalConnections, onlineTime, admin);
    }

    private Onlinetime getOnlinetime(String uniqueId) {
        if(!byteClientCache.containsKey(uniqueId))
            byteClientCache.put(uniqueId, new Onlinetime(getByteClientOnlinetimeMillis(uniqueId)));
        return byteClientCache.get(uniqueId);
    }

}
