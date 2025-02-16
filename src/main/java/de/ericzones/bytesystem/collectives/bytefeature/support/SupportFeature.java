// Created by Eric B. 06.03.2022 14:01
package de.ericzones.bytesystem.collectives.bytefeature.support;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.ericzones.bytesystem.ByteSystem;
import de.ericzones.bytesystem.collectives.bytefeature.Feature;
import de.ericzones.bytesystem.collectives.bytefeature.FeatureType;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SupportFeature extends Feature {

    private final Timer timer = new Timer();

    private int channelId, channelClientsOpen, channelClientsClose, notifyGroupId;
    private String channelNameOpen, channelNameClose;
    private boolean automated, open;

    public SupportFeature(int botId, boolean enabled, int channelId, String channelNameOpen, String channelNameClose, int channelClientsOpen, int channelClientsClose, int notifyGroupId, boolean automated, boolean open) {
        super(botId, FeatureType.SUPPORT, enabled);
        this.channelId = channelId;
        this.channelNameOpen = channelNameOpen;
        this.channelNameClose = channelNameClose;
        this.channelClientsOpen = channelClientsOpen;
        this.channelClientsClose = channelClientsClose;
        this.notifyGroupId = notifyGroupId;
        this.automated = automated;
        this.open = open;
        initiateSupportTimer();
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
        ByteSystem.getByteFeatureManager().updateSupport(SupportSetting.CHANNEL_ID, channelId);
    }

    public void resetChannelId() {
        this.channelId = -1;
        ByteSystem.getByteFeatureManager().updateSupport(SupportSetting.CHANNEL_ID, null);
        setOpen(false);
        setAutomated(false);
        setEnabled(false);
    }

    public String getChannelNameOpen() {
        return channelNameOpen;
    }

    public void setChannelNameOpen(String channelNameOpen) {
        this.channelNameOpen = channelNameOpen;
        ByteSystem.getByteFeatureManager().updateSupport(SupportSetting.CHANNEL_NAME_OPEN, channelNameOpen);
    }

    public String getChannelNameClose() {
        return channelNameClose;
    }

    public void setChannelNameClose(String channelNameClose) {
        this.channelNameClose = channelNameClose;
        ByteSystem.getByteFeatureManager().updateSupport(SupportSetting.CHANNEL_NAME_CLOSE, channelNameClose);
    }

    public int getChannelClientsOpen() {
        return channelClientsOpen;
    }

    public void setChannelClientsOpen(int channelClientsOpen) {
        this.channelClientsOpen = channelClientsOpen;
        ByteSystem.getByteFeatureManager().updateSupport(SupportSetting.CHANNEL_CLIENTS_OPEN, channelClientsOpen);
    }

    public int getChannelClientsClose() {
        return channelClientsClose;
    }

    public void setChannelClientsClose(int channelClientsClose) {
        this.channelClientsClose = channelClientsClose;
        ByteSystem.getByteFeatureManager().updateSupport(SupportSetting.CHANNEL_CLIENTS_CLOSE, channelClientsClose);
    }

    public int getNotifyGroupId() {
        return notifyGroupId;
    }

    public void setNotifyGroupId(int notifyGroupId) {
        this.notifyGroupId = notifyGroupId;
        ByteSystem.getByteFeatureManager().updateSupport(SupportSetting.GROUP_NOTIFY_ID, notifyGroupId);
    }

    public boolean isAutomated() {
        return automated;
    }

    public void setAutomated(boolean automated) {
        this.automated = automated;
        ByteSystem.getByteFeatureManager().updateSupport(SupportSetting.AUTOMATION, automated);
        if(automated)
            checkSupportStatus(ByteSystem.getQuery().getApi());
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        ByteSystem.getByteFeatureManager().updateSupport(SupportSetting.OPEN, open);
        if(!isConfigured()) return;
        TS3Api api = ByteSystem.getQuery().getApi();
        if(api.getChannels().stream().noneMatch(ch -> ch.getId() == channelId)) {
            resetChannelId();
            return;
        }
        Map<ChannelProperty, String> channelData = new HashMap<>();
        if(open) {
            channelData.put(ChannelProperty.CHANNEL_NAME, channelNameOpen);
            channelData.put(ChannelProperty.CHANNEL_MAXCLIENTS, String.valueOf(channelClientsOpen));
        } else {
            channelData.put(ChannelProperty.CHANNEL_NAME, channelNameClose);
            channelData.put(ChannelProperty.CHANNEL_MAXCLIENTS, String.valueOf(channelClientsClose));
        }
        api.editChannel(channelId, channelData);
    }

    public boolean isConfigured() {
        if(channelId == -1 || channelNameOpen == null || channelNameClose == null || channelClientsOpen == -1 || channelClientsClose == -1 || notifyGroupId == -1)
            return false;
        return true;
    }

    public void checkSupportStatus(TS3Api api) {
        SupportFeature supportFeature = (SupportFeature) ByteSystem.getByteFeatureManager().getFeature(FeatureType.SUPPORT);
        if(!supportFeature.isEnabled()) return;
        if(!supportFeature.isConfigured()) {
            supportFeature.resetChannelId();
            return;
        }
        if(api.getChannels().stream().noneMatch(ch -> ch.getId() == supportFeature.getChannelId())) {
            supportFeature.resetChannelId();
            return;
        }
        if(!supportFeature.isAutomated())
            supportFeature.setOpen(supportFeature.isOpen());
        else {
            int supporter = 0;
            for(Client current : api.getClients()) {
                if(current.isInServerGroup(supportFeature.getNotifyGroupId()))
                    supporter++;
            }
            if(supportFeature.isOpen()) {
                if(supporter > 0) return;
                supportFeature.setOpen(false);
            } else {
                if(supporter == 0) return;
                supportFeature.setOpen(true);
            }
        }
    }

    private void initiateSupportTimer() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(!automated) return;
                checkSupportStatus(ByteSystem.getQuery().getApi());
            }
        };
        timer.schedule(timerTask, 5*1000, 5*1000);
    }

}
