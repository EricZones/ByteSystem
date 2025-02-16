// Created by Eric B. 22.10.2021 12:34
package de.ericzones.bytesystem.collectives.bytecore;

import de.ericzones.bytesystem.collectives.database.DatabaseHandler;
import de.ericzones.bytesystem.collectives.database.Pair;
import de.ericzones.bytesystem.collectives.database.SqlDataType;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SqlByteCore {

    private final DatabaseHandler databaseHandler;
    private final int databaseId;

    private final String tableNameList = "Botlist";
    private final String[] sqlKeysList = new String[]{"id", "status", "created", "millis"};
    private final SqlDataType[] sqlTypesList = new SqlDataType[]{SqlDataType.BIGINT, SqlDataType.VARCHAR, SqlDataType.VARCHAR, SqlDataType.VARCHAR};

    private final String tableNameConfig = "Botconfigs";
    private final String[] sqlKeysConfig = new String[]{"id", "host", "port", "serverid", "loginname", "password", "nickname", "channel"};
    private final SqlDataType[] sqlTypesConfig = new SqlDataType[]{SqlDataType.BIGINT, SqlDataType.VARCHAR, SqlDataType.VARCHAR, SqlDataType.BIGINT, SqlDataType.VARCHAR,
                                                                    SqlDataType.VARCHAR, SqlDataType.VARCHAR, SqlDataType.BIGINT};

    public SqlByteCore(DatabaseHandler databaseHandler, int databaseId) {
        this.databaseHandler = databaseHandler;
        this.databaseId = databaseId;
        this.databaseHandler.createTable(databaseId, tableNameList, getTableInformation(tableNameList), sqlKeysList[0], true);
        this.databaseHandler.createTable(databaseId, tableNameConfig, getTableInformation(tableNameConfig), sqlKeysConfig[0]);
    }

    private Pair<String, SqlDataType>[] getTableInformation(String tableName) {
        Pair<String, SqlDataType>[] content;
        switch (tableName) {
            case tableNameList:
                content = new Pair[sqlKeysList.length];
                for(int i = 0; i < sqlKeysList.length; i++) content[i] = new Pair(sqlKeysList[i], sqlTypesList[i]);
                break;
            case tableNameConfig:
                content = new Pair[sqlKeysConfig.length];
                for(int i = 0; i < sqlKeysConfig.length; i++) content[i] = new Pair(sqlKeysConfig[i], sqlTypesConfig[i]);
                break;
            default:
                content = new Pair[1];
                break;
        }
        return content;
    }

    public boolean byteCoreExists(int id) {
        return this.databaseHandler.existsInTable(this.databaseId, tableNameList, new Pair[]{new Pair(sqlKeysList[0], id)});
    }

    public boolean byteCoreConfigExists(int id) {
        if(!byteCoreExists(id)) return false;
        if(!this.databaseHandler.existsInTable(this.databaseId, tableNameConfig, new Pair[]{new Pair(sqlKeysConfig[0], id)})) return false;
        if(getByteCoreHost(id) != null && getByteCoreLoginName(id) != null && getByteCorePassword(id) != null) return true;
        return false;
    }

    public int createByteCore() {
        List<String> list = Arrays.stream(sqlKeysList).collect(Collectors.toList());
        list.remove(sqlKeysList[0]);
        String dateTime = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(LocalDateTime.now(ZoneId.of("UTC+2")));
        long millis = System.currentTimeMillis();
        this.databaseHandler.addToTable(this.databaseId, tableNameList, list, Arrays.asList("Offline", dateTime, String.valueOf(millis)));
        int id = getIdByMillis(millis);
        this.databaseHandler.addToTable(this.databaseId, tableNameConfig, Arrays.asList(sqlKeysConfig), Arrays.asList(id, null, "null", 1, null, null, "ByteSystem", 0));
        return id;
    }

    public int getIdByMillis(long millis) {
        return ((BigInteger) this.databaseHandler.getObjectFromTable(this.databaseId, tableNameList, new Pair[]{new Pair(sqlKeysList[3], String.valueOf(millis))}, sqlKeysList[0])).intValue();
    }

    public void updateByteCore(int id, boolean online) {
        if(online)
            this.databaseHandler.updateInTable(this.databaseId, tableNameList, new Pair[]{new Pair(sqlKeysList[0], id)}, sqlKeysList[1], "Online");
        else
            this.databaseHandler.updateInTable(this.databaseId, tableNameList, new Pair[]{new Pair(sqlKeysList[0], id)}, sqlKeysList[1], "Offline");
    }

    public void updateByteCoreConfig(int id, int configId, Object setting) {
        this.databaseHandler.updateInTable(this.databaseId, tableNameConfig, new Pair[]{new Pair(sqlKeysConfig[0], id)}, sqlKeysConfig[configId], setting);
    }

    public String getByteCoreHost(int id) {
        if(this.databaseHandler.getObjectFromTable(this.databaseId, tableNameConfig, new Pair[]{new Pair(sqlKeysConfig[0], id)}, sqlKeysConfig[1]) != null && !this.databaseHandler.getObjectFromTable(this.databaseId, tableNameConfig, new Pair[]{new Pair(sqlKeysConfig[0], id)}, sqlKeysConfig[1]).equals("null"))
            return String.valueOf(this.databaseHandler.getObjectFromTable(this.databaseId, tableNameConfig, new Pair[]{new Pair(sqlKeysConfig[0], id)}, sqlKeysConfig[1]));
        return null;
    }

    public String getByteCorePort(int id) {
        if(!this.databaseHandler.getObjectFromTable(this.databaseId, tableNameConfig, new Pair[]{new Pair(sqlKeysConfig[0], id)}, sqlKeysConfig[2]).equals("null"))
            return String.valueOf(this.databaseHandler.getObjectFromTable(this.databaseId, tableNameConfig, new Pair[]{new Pair(sqlKeysConfig[0], id)}, sqlKeysConfig[2]));
        return null; 
    }

    public int getByteCoreServerId(int id) {
        if(this.databaseHandler.getObjectFromTable(this.databaseId, tableNameConfig, new Pair[]{new Pair(sqlKeysConfig[0], id)}, sqlKeysConfig[3]) != null)
            return ((BigInteger) this.databaseHandler.getObjectFromTable(this.databaseId, tableNameConfig, new Pair[]{new Pair(sqlKeysConfig[0], id)}, sqlKeysConfig[3])).intValue();
        return 1;
    }

    public String getByteCoreLoginName(int id) {
        if(!this.databaseHandler.getObjectFromTable(this.databaseId, tableNameConfig, new Pair[]{new Pair(sqlKeysConfig[0], id)}, sqlKeysConfig[4]).equals("null"))
            return String.valueOf(this.databaseHandler.getObjectFromTable(this.databaseId, tableNameConfig, new Pair[]{new Pair(sqlKeysConfig[0], id)}, sqlKeysConfig[4]));
        return null;
    }

    public String getByteCorePassword(int id) {
        if(!this.databaseHandler.getObjectFromTable(this.databaseId, tableNameConfig, new Pair[]{new Pair(sqlKeysConfig[0], id)}, sqlKeysConfig[5]).equals("null"))
            return String.valueOf(this.databaseHandler.getObjectFromTable(this.databaseId, tableNameConfig, new Pair[]{new Pair(sqlKeysConfig[0], id)}, sqlKeysConfig[5]));
        return null;
    }

    public String getByteCoreNickname(int id) {
        if(!this.databaseHandler.getObjectFromTable(this.databaseId, tableNameConfig, new Pair[]{new Pair(sqlKeysConfig[0], id)}, sqlKeysConfig[6]).equals("null"))
            return String.valueOf(this.databaseHandler.getObjectFromTable(this.databaseId, tableNameConfig, new Pair[]{new Pair(sqlKeysConfig[0], id)}, sqlKeysConfig[6]));
        return null;
    }

    public int getByteCoreChannel(int id) {
        if(this.databaseHandler.getObjectFromTable(this.databaseId, tableNameConfig, new Pair[]{new Pair(sqlKeysConfig[0], id)}, sqlKeysConfig[7]) != null)
            return ((BigInteger) this.databaseHandler.getObjectFromTable(this.databaseId, tableNameConfig, new Pair[]{new Pair(sqlKeysConfig[0], id)}, sqlKeysConfig[7])).intValue();
        return 0;
    }

}
