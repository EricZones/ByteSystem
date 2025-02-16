// Created by Eric B. 01.03.2022 12:10
package de.ericzones.bytesystem.collectives.bytefeature;

import de.ericzones.bytesystem.ByteSystem;
import de.ericzones.bytesystem.collectives.bytefeature.channel.ChannelFeature;
import de.ericzones.bytesystem.collectives.bytefeature.moderation.ModerationFeature;
import de.ericzones.bytesystem.collectives.bytefeature.moderation.ModerationSetting;
import de.ericzones.bytesystem.collectives.bytefeature.punish.PunishFeature;
import de.ericzones.bytesystem.collectives.bytefeature.support.SupportFeature;
import de.ericzones.bytesystem.collectives.database.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class ByteFeatureManager extends SqlByteFeature {

    List<Feature> features = new ArrayList<>();

    public ByteFeatureManager(DatabaseHandler databaseHandler, int databaseId) {
        super(databaseHandler, databaseId);
        registerFeatures();
    }

    public Feature getFeature(FeatureType featureType) {
        return features.stream().filter(feature -> feature.getFeatureType() == featureType).findFirst().orElse(null);
    }

    private void registerFeatures() {
        String[] featureData = getFeatureData(FeatureType.CHANNEL);
        boolean enabled = Boolean.parseBoolean(featureData[0]);
        int channelId = -1;
        if(!featureData[1].equalsIgnoreCase("null"))
            channelId = Integer.parseInt(featureData[1]);
        String channelName = null;
        if(!featureData[2].equalsIgnoreCase("null"))
            channelName = featureData[2];
        int channelGroupId = -1;
        if(!featureData[3].equalsIgnoreCase("null"))
            channelGroupId = Integer.parseInt(featureData[3]);
        long delay = -1;
        if(!featureData[4].equalsIgnoreCase("null"))
            delay = Long.parseLong(featureData[4]);
        features.add(new ChannelFeature(ByteSystem.getBotId(), enabled, channelId, channelName, channelGroupId, delay));

        featureData = getFeatureData(FeatureType.SUPPORT);
        enabled = Boolean.parseBoolean(featureData[0]);
        channelId = -1;
        if(!featureData[1].equalsIgnoreCase("null"))
            channelId = Integer.parseInt(featureData[1]);
        String channelNameOpen = null;
        if(!featureData[2].equalsIgnoreCase("null"))
            channelNameOpen = featureData[2];
        String channelNameClose = null;
        if(!featureData[3].equalsIgnoreCase("null"))
            channelNameClose = featureData[3];
        int clientsOpen = -1;
        if(!featureData[4].equalsIgnoreCase("null"))
            clientsOpen = Integer.parseInt(featureData[4]);
        int clientsClose = -1;
        if(!featureData[5].equalsIgnoreCase("null"))
            clientsClose = Integer.parseInt(featureData[5]);
        int notifyGroupId = -1;
        if(!featureData[6].equalsIgnoreCase("null"))
            notifyGroupId = Integer.parseInt(featureData[6]);
        boolean automated = Boolean.parseBoolean(featureData[7]);
        boolean open = Boolean.parseBoolean(featureData[8]);
        features.add(new SupportFeature(ByteSystem.getBotId(), enabled, channelId, channelNameOpen, channelNameClose, clientsOpen, clientsClose, notifyGroupId, automated, open));

        featureData = getFeatureData(FeatureType.MODERATION);
        enabled = Boolean.parseBoolean(featureData[0]);
        boolean nameCheck = Boolean.parseBoolean(featureData[1]);
        boolean switchCheck = Boolean.parseBoolean(featureData[2]);
        int switchComplainsLimit = -1;
        if(!featureData[3].equalsIgnoreCase("null"))
            switchComplainsLimit = Integer.parseInt(featureData[3]);
        long switchDelay = -1;
        if(!featureData[4].equalsIgnoreCase("null"))
            switchDelay = Long.parseLong(featureData[4]);
        features.add(new ModerationFeature(ByteSystem.getBotId(), enabled, nameCheck, switchCheck, switchComplainsLimit, switchDelay, getFeatureIgnoreGroups(FeatureType.MODERATION), getModerationFeatureContent(ModerationSetting.FORBIDDEN_NICKNAME)));

        featureData = getFeatureData(FeatureType.PUNISH);
        enabled = Boolean.parseBoolean(featureData[0]);
        int complainsLimit = -1;
        if(!featureData[1].equalsIgnoreCase("null"))
            complainsLimit = Integer.parseInt(featureData[1]);
        long banDuration = -1;
        if(!featureData[2].equalsIgnoreCase("null"))
            banDuration = Long.parseLong(featureData[2]);
        features.add(new PunishFeature(ByteSystem.getBotId(), enabled, complainsLimit, banDuration));
    }

}
