// Created by Eric B. 31.08.2022 14:17
package de.ericzones.bytesystem.collectives.bytefeature.punish;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.ericzones.bytesystem.ByteSystem;
import de.ericzones.bytesystem.collectives.bytefeature.Feature;
import de.ericzones.bytesystem.collectives.bytefeature.FeatureType;

import java.util.HashMap;
import java.util.Map;

public class PunishFeature extends Feature {

    private int complainsLimit;
    private long banDuration;

    public PunishFeature(int botId, boolean enabled, int complainsLimit, long banDuration) {
        super(botId, FeatureType.PUNISH, enabled);
        this.complainsLimit = complainsLimit;
        this.banDuration = banDuration;
        this.delayCache = new HashMap<>();
        this.warnCache = new HashMap<>();
    }

    public int getComplainsLimit() {
        return complainsLimit;
    }

    public void setComplainsLimit(int complainsLimit) {
        this.complainsLimit = complainsLimit;
        ByteSystem.getByteFeatureManager().updatePunish(PunishSetting.COMPLAINS_LIMIT, complainsLimit);
    }

    public long getBanDuration() {
        return banDuration;
    }

    public void setBanDuration(long banDuration) {
        this.banDuration = banDuration;
        ByteSystem.getByteFeatureManager().updatePunish(PunishSetting.BAN_DURATION, banDuration);
    }

    public boolean isConfigured() {
        if(complainsLimit == -1 || banDuration == -1)
            return false;
        return true;
    }

    private final Map<String, Long> delayCache;
    private final Map<String, Integer> warnCache;

    public boolean isClientPunished(Client client) {
        for(String current : delayCache.keySet()) {
            if (delayCache.get(current) < System.currentTimeMillis()) {
                delayCache.remove(current);
                warnCache.remove(current);
            }
        }
        if(!isConfigured() || !isEnabled()) return false;

        if(!warnCache.containsKey(client.getUniqueIdentifier())) {
            delayCache.put(client.getUniqueIdentifier(), System.currentTimeMillis()+5*60*1000);
            warnCache.put(client.getUniqueIdentifier(), 1);
            return false;
        }
        if(warnCache.get(client.getUniqueIdentifier()) >= this.complainsLimit) {
            delayCache.remove(client.getUniqueIdentifier());
            warnCache.remove(client.getUniqueIdentifier());
            ByteSystem.getQuery().getApi().banClient(client.getId(), this.banDuration, "Maximum amount of complains reached");
            return true;
        }
        delayCache.put(client.getUniqueIdentifier(), System.currentTimeMillis()+5*60*1000);
        warnCache.put(client.getUniqueIdentifier(), warnCache.get(client.getUniqueIdentifier())+1);
        return false;
    }


}
