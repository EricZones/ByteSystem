// Created by Eric B. 01.03.2022 12:10
package de.ericzones.bytesystem.collectives.bytefeature;

import de.ericzones.bytesystem.ByteSystem;
import de.ericzones.bytesystem.collectives.bytefeature.channel.ChannelSetting;
import de.ericzones.bytesystem.collectives.bytefeature.moderation.ModerationSetting;
import de.ericzones.bytesystem.collectives.bytefeature.punish.PunishSetting;
import de.ericzones.bytesystem.collectives.bytefeature.support.SupportSetting;
import de.ericzones.bytesystem.collectives.database.DatabaseHandler;
import de.ericzones.bytesystem.collectives.database.Pair;
import de.ericzones.bytesystem.collectives.database.SqlDataType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public abstract class SqlByteFeature {

    private final DatabaseHandler databaseHandler;
    private final int databaseId;

    private final String tableNameChannel = "Feature_Channel";
    private final String[] sqlKeysChannel = new String[]{"id", "status", "channelId", "channelName", "channelGroupId", "delay"};
    private final SqlDataType[] sqlTypesChannel = new SqlDataType[]{SqlDataType.BIGINT, SqlDataType.BOOLEAN, SqlDataType.BIGINT, SqlDataType.VARCHAR, SqlDataType.BIGINT, SqlDataType.BIGINT};

    private final String tableNameSupport = "Feature_Support";
    private final String[] sqlKeysSupport = new String[]{"id", "status", "channelId", "channelNameOpen", "channelNameClose", "clientsOpen", "clientsClose", "notifyGroupId", "automation", "open"};
    private final SqlDataType[] sqlTypesSupport = new SqlDataType[]{SqlDataType.BIGINT, SqlDataType.BOOLEAN, SqlDataType.BIGINT, SqlDataType.VARCHAR, SqlDataType.VARCHAR, SqlDataType.BIGINT, SqlDataType.BIGINT, SqlDataType.BIGINT,
                                                                    SqlDataType.BOOLEAN, SqlDataType.BOOLEAN};

    private final String tableNameModeration = "Feature_Moderation";
    private final String[] sqlKeysModeration = new String[]{"id", "status", "nameCheck", "switchCheck", "switchComplainsLimit", "switchDelay"};
    private final SqlDataType[] sqlTypesModeration = new SqlDataType[]{SqlDataType.BIGINT, SqlDataType.BOOLEAN, SqlDataType.BOOLEAN, SqlDataType.BOOLEAN, SqlDataType.BIGINT, SqlDataType.BIGINT};

    private final String tableNameModerationIgnore = "Feature_Moderation_Ignore";
    private final String[] sqlKeysModerationIgnore = new String[]{"id", "groupId"};
    private final SqlDataType[] sqlTypesModerationIgnore = new SqlDataType[]{SqlDataType.BIGINT, SqlDataType.BIGINT};

    private final String tableNameModerationNickname = "Feature_Moderation_Nickname";
    private final String[] sqlKeysModerationNickname = new String[]{"id", "nickname"};
    private final SqlDataType[] sqlTypesModerationNickname = new SqlDataType[]{SqlDataType.BIGINT, SqlDataType.VARCHAR};

    private final String tableNamePunish = "Feature_Punish";
    private final String[] sqlKeysPunish = new String[]{"id", "status", "complainsLimit", "banDuration"};
    private final SqlDataType[] sqlTypesPunish = new SqlDataType[]{SqlDataType.BIGINT, SqlDataType.BOOLEAN, SqlDataType.BIGINT, SqlDataType.BIGINT};

    public SqlByteFeature(DatabaseHandler databaseHandler, int databaseId) {
        this.databaseHandler = databaseHandler;
        this.databaseId = databaseId;
        this.databaseHandler.createTable(databaseId, tableNameChannel, getTableInformation(tableNameChannel), sqlKeysChannel[0]);
        this.databaseHandler.createTable(databaseId, tableNameSupport, getTableInformation(tableNameSupport), sqlKeysSupport[0]);
        this.databaseHandler.createTable(databaseId, tableNameModeration, getTableInformation(tableNameModeration), sqlKeysModeration[0]);
        this.databaseHandler.createTable(databaseId, tableNameModerationIgnore, getTableInformation(tableNameModerationIgnore));
        this.databaseHandler.createTable(databaseId, tableNameModerationNickname, getTableInformation(tableNameModerationNickname));
        this.databaseHandler.createTable(databaseId, tableNamePunish, getTableInformation(tableNamePunish));
        checkRegistration();
    }

    private Pair<String, SqlDataType>[] getTableInformation(String tableName) {
        Pair<String, SqlDataType>[] content;
        switch (tableName) {
            case tableNameChannel:
                content = new Pair[sqlKeysChannel.length];
                for(int i = 0; i < sqlKeysChannel.length; i++) content[i] = new Pair(sqlKeysChannel[i], sqlTypesChannel[i]);
                break;
            case tableNameSupport:
                content = new Pair[sqlKeysSupport.length];
                for(int i = 0; i < sqlKeysSupport.length; i++) content[i] = new Pair(sqlKeysSupport[i], sqlTypesSupport[i]);
                break;
            case tableNameModeration:
                content = new Pair[sqlKeysModeration.length];
                for(int i = 0; i < sqlKeysModeration.length; i++) content[i] = new Pair(sqlKeysModeration[i], sqlTypesModeration[i]);
                break;
            case tableNameModerationIgnore:
                content = new Pair[sqlKeysModerationIgnore.length];
                for(int i = 0; i < sqlKeysModerationIgnore.length; i++) content[i] = new Pair(sqlKeysModerationIgnore[i], sqlTypesModerationIgnore[i]);
                break;
            case tableNameModerationNickname:
                content = new Pair[sqlKeysModerationNickname.length];
                for(int i = 0; i < sqlKeysModerationNickname.length; i++) content[i] = new Pair(sqlKeysModerationNickname[i], sqlTypesModerationNickname[i]);
                break;
            case tableNamePunish:
                content = new Pair[sqlKeysPunish.length];
                for(int i = 0; i < sqlKeysPunish.length; i++) content[i] = new Pair(sqlKeysPunish[i], sqlTypesPunish[i]);
                break;
            default:
                content = new Pair[1];
                break;
        }
        return content;
    }

    protected String[] getFeatureData(FeatureType featureType) {
        String[] featureData;
        switch (featureType) {
            case CHANNEL:
                featureData = new String[sqlKeysChannel.length-1];
                for(int i = 1; i < sqlKeysChannel.length; i++)
                    featureData[i-1] = String.valueOf(this.databaseHandler.getObjectFromTable(this.databaseId, tableNameChannel, new Pair[]{new Pair(sqlKeysChannel[0], ByteSystem.getBotId())}, sqlKeysChannel[i]));
                return featureData;
            case SUPPORT:
                featureData = new String[sqlKeysSupport.length-1];
                for(int i = 1; i < sqlKeysSupport.length; i++)
                    featureData[i-1] = String.valueOf(this.databaseHandler.getObjectFromTable(this.databaseId, tableNameSupport, new Pair[]{new Pair(sqlKeysSupport[0], ByteSystem.getBotId())}, sqlKeysSupport[i]));
                return featureData;
            case MODERATION:
                featureData = new String[sqlKeysModeration.length-1];
                for(int i = 1; i < sqlKeysModeration.length; i++)
                    featureData[i-1] = String.valueOf(this.databaseHandler.getObjectFromTable(this.databaseId, tableNameModeration, new Pair[]{new Pair(sqlKeysModeration[0], ByteSystem.getBotId())}, sqlKeysModeration[i]));
                return featureData;
            case PUNISH:
                featureData = new String[sqlKeysPunish.length-1];
                for(int i = 1; i < sqlKeysPunish.length; i++)
                    featureData[i-1] = String.valueOf(this.databaseHandler.getObjectFromTable(this.databaseId, tableNamePunish, new Pair[]{new Pair(sqlKeysPunish[0], ByteSystem.getBotId())}, sqlKeysPunish[i]));
                return featureData;
        }
        return null;
    }

    protected List<Integer> getFeatureIgnoreGroups(FeatureType featureType) {
        List<Integer> featureIgnoreGroups = new ArrayList<>();
        switch (featureType) {
            case MODERATION:
                List<Object> objects = this.databaseHandler.getDataFromTable(this.databaseId, tableNameModerationIgnore, new Pair[]{new Pair(sqlKeysModerationIgnore[0], ByteSystem.getBotId())}, sqlKeysModerationIgnore[1]);
                for(Object current : objects)
                    featureIgnoreGroups.add(((BigInteger)current).intValue());
        }
        return featureIgnoreGroups;
    }

    public void addFeatureIgnoreGroup(FeatureType featureType, int groupId) {
        switch (featureType) {
            case MODERATION:
                this.databaseHandler.addToTable(this.databaseId, tableNameModerationIgnore, new Pair[]{new Pair(sqlKeysModerationIgnore[0], ByteSystem.getBotId()), new Pair(sqlKeysModerationIgnore[1], groupId)});
                break;
        }
    }

    public void removeFeatureIgnoreGroup(FeatureType featureType, int groupId) {
        switch (featureType) {
            case MODERATION:
                this.databaseHandler.removeFromTable(this.databaseId, tableNameModerationIgnore, new Pair[]{new Pair(sqlKeysModerationIgnore[0], ByteSystem.getBotId()), new Pair(sqlKeysModerationIgnore[1], groupId)});
                break;
        }
    }

    public void addModerationFeatureContent(ModerationSetting moderationSetting, Object content) {
        switch (moderationSetting) {
            case FORBIDDEN_NICKNAME:
                this.databaseHandler.addToTable(this.databaseId, tableNameModerationNickname, new Pair[]{new Pair(sqlKeysModerationNickname[0], ByteSystem.getBotId()), new Pair(sqlKeysModerationNickname[1], content)});
                break;
        }
    }

    public void removeModerationFeatureContent(ModerationSetting moderationSetting, Object content) {
        switch (moderationSetting) {
            case FORBIDDEN_NICKNAME:
                this.databaseHandler.removeFromTable(this.databaseId, tableNameModerationNickname, new Pair[]{new Pair(sqlKeysModerationNickname[0], ByteSystem.getBotId()), new Pair(sqlKeysModerationNickname[1], content)});
                break;
        }
    }

    protected List<Object> getModerationFeatureContent(ModerationSetting moderationSetting) {
        List<Object> featureContent = new ArrayList<>();
        switch (moderationSetting) {
            case FORBIDDEN_NICKNAME:
                featureContent = this.databaseHandler.getDataFromTable(this.databaseId, tableNameModerationNickname, new Pair[]{new Pair(sqlKeysModerationNickname[0], ByteSystem.getBotId())}, sqlKeysModerationNickname[1]);
        }
        return featureContent;
    }

    public void updateSupport(SupportSetting supportSetting, Object value) {
        switch (supportSetting) {
            case STATUS:
                this.databaseHandler.updateInTable(this.databaseId, tableNameSupport, new Pair[]{new Pair(sqlKeysSupport[0], ByteSystem.getBotId())}, sqlKeysSupport[1], value);
                break;
            case CHANNEL_ID:
                this.databaseHandler.updateInTable(this.databaseId, tableNameSupport, new Pair[]{new Pair(sqlKeysSupport[0], ByteSystem.getBotId())}, sqlKeysSupport[2], value);
                break;
            case CHANNEL_NAME_OPEN:
                this.databaseHandler.updateInTable(this.databaseId, tableNameSupport, new Pair[]{new Pair(sqlKeysSupport[0], ByteSystem.getBotId())}, sqlKeysSupport[3], value);
                break;
            case CHANNEL_NAME_CLOSE:
                this.databaseHandler.updateInTable(this.databaseId, tableNameSupport, new Pair[]{new Pair(sqlKeysSupport[0], ByteSystem.getBotId())}, sqlKeysSupport[4], value);
                break;
            case CHANNEL_CLIENTS_OPEN:
                this.databaseHandler.updateInTable(this.databaseId, tableNameSupport, new Pair[]{new Pair(sqlKeysSupport[0], ByteSystem.getBotId())}, sqlKeysSupport[5], value);
                break;
            case CHANNEL_CLIENTS_CLOSE:
                this.databaseHandler.updateInTable(this.databaseId, tableNameSupport, new Pair[]{new Pair(sqlKeysSupport[0], ByteSystem.getBotId())}, sqlKeysSupport[6], value);
                break;
            case GROUP_NOTIFY_ID:
                this.databaseHandler.updateInTable(this.databaseId, tableNameSupport, new Pair[]{new Pair(sqlKeysSupport[0], ByteSystem.getBotId())}, sqlKeysSupport[7], value);
                break;
            case AUTOMATION:
                this.databaseHandler.updateInTable(this.databaseId, tableNameSupport, new Pair[]{new Pair(sqlKeysSupport[0], ByteSystem.getBotId())}, sqlKeysSupport[8], value);
                break;
            case OPEN:
                this.databaseHandler.updateInTable(this.databaseId, tableNameSupport, new Pair[]{new Pair(sqlKeysSupport[0], ByteSystem.getBotId())}, sqlKeysSupport[9], value);
                break;
        }
    }

    public void updateChannel(ChannelSetting channelSetting, Object value) {
        switch (channelSetting) {
            case STATUS:
                this.databaseHandler.updateInTable(this.databaseId, tableNameChannel, new Pair[]{new Pair(sqlKeysChannel[0], ByteSystem.getBotId())}, sqlKeysChannel[1], value);
                break;
            case CHANNEL_ID:
                this.databaseHandler.updateInTable(this.databaseId, tableNameChannel, new Pair[]{new Pair(sqlKeysChannel[0], ByteSystem.getBotId())}, sqlKeysChannel[2], value);
                break;
            case CHANNEL_NAME:
                this.databaseHandler.updateInTable(this.databaseId, tableNameChannel, new Pair[]{new Pair(sqlKeysChannel[0], ByteSystem.getBotId())}, sqlKeysChannel[3], value);
                break;
            case CHANNEL_GROUP_ID:
                this.databaseHandler.updateInTable(this.databaseId, tableNameChannel, new Pair[]{new Pair(sqlKeysChannel[0], ByteSystem.getBotId())}, sqlKeysChannel[4], value);
                break;
            case DELAY:
                this.databaseHandler.updateInTable(this.databaseId, tableNameChannel, new Pair[]{new Pair(sqlKeysChannel[0], ByteSystem.getBotId())}, sqlKeysChannel[5], value);
                break;
        }
    }

    public void updateModeration(ModerationSetting moderationSetting, Object value) {
        switch (moderationSetting) {
            case STATUS:
                this.databaseHandler.updateInTable(this.databaseId, tableNameModeration, new Pair[]{new Pair(sqlKeysModeration[0], ByteSystem.getBotId())}, sqlKeysModeration[1], value);
                break;
            case NAMECHECK:
                this.databaseHandler.updateInTable(this.databaseId, tableNameModeration, new Pair[]{new Pair(sqlKeysModeration[0], ByteSystem.getBotId())}, sqlKeysModeration[2], value);
                break;
            case SWITCHCHECK:
                this.databaseHandler.updateInTable(this.databaseId, tableNameModeration, new Pair[]{new Pair(sqlKeysModeration[0], ByteSystem.getBotId())}, sqlKeysModeration[3], value);
                break;
            case SWITCH_COMPLAINS_LIMIT:
                this.databaseHandler.updateInTable(this.databaseId, tableNameModeration, new Pair[]{new Pair(sqlKeysModeration[0], ByteSystem.getBotId())}, sqlKeysModeration[4], value);
                break;
            case SWITCH_DELAY:
                this.databaseHandler.updateInTable(this.databaseId, tableNameModeration, new Pair[]{new Pair(sqlKeysModeration[0], ByteSystem.getBotId())}, sqlKeysModeration[5], value);
                break;
        }
    }

    public void updatePunish(PunishSetting punishSetting, Object value) {
        switch (punishSetting) {
            case STATUS:
                this.databaseHandler.updateInTable(this.databaseId, tableNamePunish, new Pair[]{new Pair(sqlKeysPunish[0], ByteSystem.getBotId())}, sqlKeysPunish[1], value);
                break;
            case COMPLAINS_LIMIT:
                this.databaseHandler.updateInTable(this.databaseId, tableNamePunish, new Pair[]{new Pair(sqlKeysPunish[0], ByteSystem.getBotId())}, sqlKeysPunish[2], value);
                break;
            case BAN_DURATION:
                this.databaseHandler.updateInTable(this.databaseId, tableNamePunish, new Pair[]{new Pair(sqlKeysPunish[0], ByteSystem.getBotId())}, sqlKeysPunish[3], value);
                break;
        }
    }

    private void checkRegistration() {
        if(!this.databaseHandler.existsInTable(this.databaseId, tableNameChannel, new Pair[]{new Pair(sqlKeysChannel[0], ByteSystem.getBotId())})) {
            Pair[] data = new Pair[sqlKeysChannel.length];
            data[0] = new Pair(sqlKeysChannel[0], ByteSystem.getBotId());
            data[1] = new Pair(sqlKeysChannel[1], false);
            for(int i = 2; i < sqlKeysChannel.length; i++)
                data[i] = new Pair(sqlKeysChannel[i], null);
            this.databaseHandler.addToTable(this.databaseId, tableNameChannel, data);
        }
        if(!this.databaseHandler.existsInTable(this.databaseId, tableNameSupport, new Pair[]{new Pair(sqlKeysSupport[0], ByteSystem.getBotId())})) {
            Pair[] data = new Pair[sqlKeysSupport.length];
            data[0] = new Pair(sqlKeysSupport[0], ByteSystem.getBotId());
            data[1] = new Pair(sqlKeysSupport[1], false);
            for(int i = 2; i < sqlKeysSupport.length-2; i++)
                data[i] = new Pair(sqlKeysSupport[i], null);
            data[8] = new Pair(sqlKeysSupport[8], false);
            data[9] = new Pair(sqlKeysSupport[9], false);
            this.databaseHandler.addToTable(this.databaseId, tableNameSupport, data);
        }
        if(!this.databaseHandler.existsInTable(this.databaseId, tableNameModeration, new Pair[]{new Pair(sqlKeysModeration[0], ByteSystem.getBotId())})) {
            Pair[] data = new Pair[sqlKeysModeration.length];
            data[0] = new Pair(sqlKeysModeration[0], ByteSystem.getBotId());
            data[1] = new Pair(sqlKeysModeration[1], false);
            data[2] = new Pair(sqlKeysModeration[2], false);
            data[3] = new Pair(sqlKeysModeration[3], false);
            data[4] = new Pair(sqlKeysModeration[4], null);
            data[5] = new Pair(sqlKeysModeration[5], null);
            this.databaseHandler.addToTable(this.databaseId, tableNameModeration, data);
        }
        if(!this.databaseHandler.existsInTable(this.databaseId, tableNamePunish, new Pair[]{new Pair(sqlKeysPunish[0], ByteSystem.getBotId())})) {
            Pair[] data = new Pair[sqlKeysPunish.length];
            data[0] = new Pair(sqlKeysPunish[0], ByteSystem.getBotId());
            data[1] = new Pair(sqlKeysPunish[1], false);
            data[2] = new Pair(sqlKeysPunish[2], null);
            data[3] = new Pair(sqlKeysPunish[3], null);
            this.databaseHandler.addToTable(this.databaseId, tableNamePunish, data);
        }
    }

}
