// Created by Eric B. 01.03.2022 19:31
package de.ericzones.bytesystem.collectives.bytefeature;

import de.ericzones.bytesystem.ByteSystem;
import de.ericzones.bytesystem.collectives.bytefeature.channel.ChannelSetting;
import de.ericzones.bytesystem.collectives.bytefeature.moderation.ModerationSetting;
import de.ericzones.bytesystem.collectives.bytefeature.punish.PunishSetting;
import de.ericzones.bytesystem.collectives.bytefeature.support.SupportSetting;

public abstract class Feature {

    private final int botId;
    private final FeatureType featureType;
    private boolean enabled;

    public Feature(int botId, FeatureType featureType, boolean enabled) {
        this.botId = botId;
        this.featureType = featureType;
        this.enabled = enabled;
    }

    public int getBotId() {
        return botId;
    }

    public FeatureType getFeatureType() {
        return featureType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(featureType == FeatureType.CHANNEL)
            ByteSystem.getByteFeatureManager().updateChannel(ChannelSetting.STATUS, enabled);
        else if(featureType == FeatureType.SUPPORT)
            ByteSystem.getByteFeatureManager().updateSupport(SupportSetting.STATUS, enabled);
        else if(featureType == FeatureType.MODERATION)
            ByteSystem.getByteFeatureManager().updateModeration(ModerationSetting.STATUS, enabled);
        else if(featureType == FeatureType.PUNISH)
            ByteSystem.getByteFeatureManager().updatePunish(PunishSetting.STATUS, enabled);
    }

}
