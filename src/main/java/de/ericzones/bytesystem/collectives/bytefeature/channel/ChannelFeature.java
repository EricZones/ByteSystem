// Created by Eric B. 01.03.2022 19:09
package de.ericzones.bytesystem.collectives.bytefeature.channel;

import de.ericzones.bytesystem.ByteSystem;
import de.ericzones.bytesystem.collectives.bytefeature.Feature;
import de.ericzones.bytesystem.collectives.bytefeature.FeatureType;

public class ChannelFeature extends Feature {

    private int channelId, channelGroupId;
    private String channelName;
    private long delay;

    public ChannelFeature(int botId, boolean enabled, int channelId, String channelName, int channelGroupId, long delay) {
        super(botId, FeatureType.CHANNEL, enabled);
        this.channelId = channelId;
        this.channelName = channelName;
        this.channelGroupId = channelGroupId;
        this.delay = delay;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
        ByteSystem.getByteFeatureManager().updateChannel(ChannelSetting.CHANNEL_ID, channelId);
    }

    public void resetChannelId() {
        this.channelId = -1;
        ByteSystem.getByteFeatureManager().updateChannel(ChannelSetting.CHANNEL_ID, null);
        setEnabled(false);
    }

    public int getChannelGroupId() {
        return channelGroupId;
    }

    public void setChannelGroupId(int channelGroupId) {
        this.channelGroupId = channelGroupId;
        ByteSystem.getByteFeatureManager().updateChannel(ChannelSetting.CHANNEL_GROUP_ID, channelGroupId);
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
        ByteSystem.getByteFeatureManager().updateChannel(ChannelSetting.CHANNEL_NAME, channelName);
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
        ByteSystem.getByteFeatureManager().updateChannel(ChannelSetting.DELAY, delay);
    }

    public boolean isConfigured() {
        if(channelId == -1 || channelName == null || channelGroupId == -1 || delay == -1)
            return false;
        return true;
    }

}
