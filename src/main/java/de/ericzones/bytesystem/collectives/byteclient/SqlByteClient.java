// Created by Eric B. 07.11.2021 15:23
package de.ericzones.bytesystem.collectives.byteclient;

import de.ericzones.bytesystem.ByteSystem;
import de.ericzones.bytesystem.collectives.database.DatabaseHandler;
import de.ericzones.bytesystem.collectives.database.Pair;
import de.ericzones.bytesystem.collectives.database.SqlDataType;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SqlByteClient {

    private final DatabaseHandler databaseHandler;
    private final int databaseId;

    private final String tableName;
    private final String[] sqlKeys = new String[]{"id", "nickname", "uuid", "databaseId", "ipAddress", "country", "os", "version", "description", "status",
            "firstConnection", "totalConnections", "onlineTime", "admin"};
    private final SqlDataType[] sqlTypes = new SqlDataType[]{SqlDataType.BIGINT, SqlDataType.VARCHAR, SqlDataType.VARCHAR, SqlDataType.BIGINT, SqlDataType.VARCHAR, SqlDataType.VARCHAR,
            SqlDataType.VARCHAR, SqlDataType.VARCHAR, SqlDataType.TEXT, SqlDataType.VARCHAR, SqlDataType.VARCHAR, SqlDataType.BIGINT, SqlDataType.BIGINT, SqlDataType.BOOLEAN};

    public SqlByteClient(DatabaseHandler databaseHandler, int databaseId, int id) {
        this.databaseHandler = databaseHandler;
        this.databaseId = databaseId;
        this.tableName = "Clientlist_"+id;
        this.databaseHandler.createTable(databaseId, tableName, getTableInformation(), sqlKeys[0], true);
    }

    private Pair<String, SqlDataType>[] getTableInformation() {
        Pair<String, SqlDataType>[] content = new Pair[sqlKeys.length];
        for(int i = 0; i < sqlKeys.length; i++) content[i] = new Pair(sqlKeys[i], sqlTypes[i]);
        return content;
    }

    public boolean byteClientExistsByUniqueId(String uniqueId) {
        return this.databaseHandler.existsInTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], uniqueId)});
    }

    public boolean byteClientExistsByNickname(String nickname) {
        return this.databaseHandler.existsInTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[1], nickname)});
    }

    public void setAllByteClientsOffline() {
        List<Object> byteClients = this.databaseHandler.getDataFromTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[9], "Online")}, sqlKeys[2]);
        for(Object current : byteClients)
            this.databaseHandler.updateInTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], current)}, sqlKeys[9], "Offline");
    }

    protected void createByteClient(ByteClient byteClient, boolean online) {
        List<String> list = Arrays.stream(sqlKeys).collect(Collectors.toList());
        list.remove(sqlKeys[0]);

        if(online) {
            this.databaseHandler.addToTable(this.databaseId, tableName, list, Arrays.asList(byteClient.getNickname(), byteClient.getUniqueId(), byteClient.getDatabaseId(), byteClient.getIpAddress(),
                    byteClient.getCountry(), byteClient.getOS(), byteClient.getVersion(), byteClient.getDescription().replaceAll("'", ""), "Online",
                    byteClient.getFirstConnection(), byteClient.getTotalConnections(), byteClient.getOnlinetime().getTotalOnlinetimeMillis(), 0));
        } else {
            String dateTime = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(LocalDateTime.now(ZoneId.of(ByteSystem.getTimeZone())));
            this.databaseHandler.addToTable(this.databaseId, tableName, list, Arrays.asList(byteClient.getNickname(), byteClient.getUniqueId(), byteClient.getDatabaseId(), byteClient.getIpAddress(),
                    byteClient.getCountry(), byteClient.getOS(), byteClient.getVersion(), byteClient.getDescription().replaceAll("'", ""), dateTime,
                    byteClient.getFirstConnection(), byteClient.getTotalConnections(), byteClient.getOnlinetime().getTotalOnlinetimeMillis(), 0));
        }
    }

    protected void updateByteClient(ByteClient byteClient, boolean online) {
        if(!online) {
            String dateTime = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(LocalDateTime.now(ZoneId.of(ByteSystem.getTimeZone())));
            this.databaseHandler.updateInTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], byteClient.getUniqueId())}, sqlKeys[9], dateTime);
            this.databaseHandler.updateInTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], byteClient.getUniqueId())}, sqlKeys[1], byteClient.getNickname());
            this.databaseHandler.updateInTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], byteClient.getUniqueId())}, sqlKeys[4], byteClient.getIpAddress());
            this.databaseHandler.updateInTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], byteClient.getUniqueId())}, sqlKeys[8], byteClient.getDescription().replaceAll("'", ""));
            this.databaseHandler.updateInTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], byteClient.getUniqueId())}, sqlKeys[12], byteClient.getOnlinetime().getTotalOnlinetimeMillis());
        } else {
            this.databaseHandler.updateInTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], byteClient.getUniqueId())}, sqlKeys[1], byteClient.getNickname());
            this.databaseHandler.updateInTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], byteClient.getUniqueId())}, sqlKeys[4], byteClient.getIpAddress());
            this.databaseHandler.updateInTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], byteClient.getUniqueId())}, sqlKeys[5], byteClient.getCountry());
            this.databaseHandler.updateInTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], byteClient.getUniqueId())}, sqlKeys[6], byteClient.getOS());
            this.databaseHandler.updateInTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], byteClient.getUniqueId())}, sqlKeys[7], byteClient.getVersion());
            this.databaseHandler.updateInTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], byteClient.getUniqueId())}, sqlKeys[8], byteClient.getDescription().replaceAll("'", ""));
            this.databaseHandler.updateInTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], byteClient.getUniqueId())}, sqlKeys[11], byteClient.getTotalConnections());
            this.databaseHandler.updateInTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], byteClient.getUniqueId())}, sqlKeys[9], "Online");
        }
    }

    protected List<String> getTopOnlinetimeUniqueIds(int amount) {
        List<String> uniqueIds = new ArrayList<>();
        for(Object current : this.databaseHandler.getHighestObjectFromTable(this.databaseId, tableName, sqlKeys[2], sqlKeys[12], amount))
            uniqueIds.add((String) current);
        return uniqueIds;
    }

    protected boolean isByteClientAdmin(String uniqueId) {
        if(this.databaseHandler.getObjectFromTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], uniqueId)}, sqlKeys[13]) != null)
            return (Boolean)this.databaseHandler.getObjectFromTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], uniqueId)}, sqlKeys[13]);
        return false;
    }

    protected void updateByteClientAdmin(String uniqueId, boolean admin) {
        this.databaseHandler.updateInTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], uniqueId)}, sqlKeys[13], admin);
    }

    protected long getByteClientOnlinetimeMillis(String uniqueId) {
        if(this.databaseHandler.getObjectFromTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], uniqueId)}, sqlKeys[12]) != null)
            return ((BigInteger)this.databaseHandler.getObjectFromTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], uniqueId)}, sqlKeys[12])).longValue();
        return 0;
    }

    protected List<String> getAdminByteClientUniqueIds() {
        return this.databaseHandler.getDataFromTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[13], true)}, sqlKeys[2]);
    }

    protected String[] getByteClientInformationByUniqueId(String uniqueId) {
        String[] byteClientInformation = new String[sqlKeys.length-1];
        for(int i = 1; i < sqlKeys.length; i++) {
            byteClientInformation[i-1] = String.valueOf(this.databaseHandler.getObjectFromTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[2], uniqueId)}, sqlKeys[i]));
        }
        return byteClientInformation;
    }

    protected List<String[]> getByteClientInformationByNickname(String nickname) {
        List<String[]> byteClientInformations = new ArrayList<>();
        List<Object> objects = this.databaseHandler.getDataFromTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[1], nickname)}, sqlKeys[0]);
        for (Object current : objects) {
            String[] byteClient = new String[sqlKeys.length - 1];
            for (int i = 1; i < sqlKeys.length; i++) {
                byteClient[i - 1] = String.valueOf(this.databaseHandler.getObjectFromTable(this.databaseId, tableName, new Pair[]{new Pair(sqlKeys[0], current)}, sqlKeys[i]));
            }
            byteClientInformations.add(byteClient);
        }
        return byteClientInformations;
    }


}
