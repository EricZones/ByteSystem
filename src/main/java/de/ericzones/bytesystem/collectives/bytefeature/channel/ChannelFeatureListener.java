// Created by Eric B. 02.03.2022 16:21
package de.ericzones.bytesystem.collectives.bytefeature.channel;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelGroupClient;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.ericzones.bytesystem.ByteSystem;
import de.ericzones.bytesystem.collectives.byteclient.ByteClientManager;
import de.ericzones.bytesystem.collectives.byteclient.OfflineByteClient;
import de.ericzones.bytesystem.collectives.bytefeature.FeatureType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelFeatureListener {

    private final String errorConfigMessage = "    » The [color=red]System[/color] is currently not configured";
    private final Map<String, Long> channelCreateCache = new HashMap<>();

    public ChannelFeatureListener(TS3Api api) {
        registerEvent(api);
    }

    private void registerEvent(TS3Api api) {
        api.addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onClientMoved(ClientMovedEvent e) {
                Client client = api.getClientInfo(e.getClientId());
                if(client == null || client.isServerQueryClient()) return;
                ChannelFeature channelFeature = (ChannelFeature) ByteSystem.getByteFeatureManager().getFeature(FeatureType.CHANNEL);
                if(channelFeature == null || !channelFeature.isEnabled()) return;
                if(channelFeature.getChannelId() == -1) return;
                if(e.getTargetChannelId() != channelFeature.getChannelId()) return;
                if(channelFeature.getChannelName() == null) {
                    api.sendPrivateMessage(client.getId(), errorConfigMessage);
                    sendAdminMessage(api, "The [color=red]Channelname[/color] has not been configured");
                    return;
                }
                if(channelFeature.getChannelGroupId() == -1) {
                    api.sendPrivateMessage(client.getId(), errorConfigMessage);
                    sendAdminMessage(api, "The [color=red]Channelgroup Id[/color] has not been configured");
                    return;
                }
                if(api.getChannelGroups().stream().noneMatch(channelGroup -> channelGroup.getId() == channelFeature.getChannelGroupId())) {
                    api.sendPrivateMessage(client.getId(), errorConfigMessage);
                    sendAdminMessage(api, "The configured [color=red]Channelgroup Id[/color] does not exist");
                    return;
                }

                if(isAlreadyChannelCreator(api, channelFeature, client)) {
                    api.kickClientFromChannel(client.getId());
                    api.sendPrivateMessage(client.getId(), "    » There is an existing [color=red]Channel[/color] by you");
                    return;
                }

                if(isChannelCreateDelayed(client.getUniqueIdentifier(), channelFeature.getDelay())) {
                    api.kickClientFromChannel(client.getId());
                    api.sendPrivateMessage(client.getId(), "    » You can create a channel in [color=red]"+getRemainingSeconds(client.getUniqueIdentifier())+"[/color] seconds");
                    return;
                }
                Map<ChannelProperty, String> channelData = new HashMap<>();
                channelData.put(ChannelProperty.CHANNEL_FLAG_SEMI_PERMANENT, "1");
                channelData.put(ChannelProperty.CHANNEL_TOPIC, "Channel creator • "+client.getNickname());
                String channelName = channelFeature.getChannelName().replace("%NICKNAME%", client.getNickname());
                int number = 1;
                while(api.getChannelByNameExact(channelName, false) != null) {
                    channelName = channelFeature.getChannelName().replace("%NICKNAME%", client.getNickname()+number);
                    number++;
                }
                ChannelInfo channelInfo = api.getChannelInfo(api.createChannel(channelName, channelData));
                api.setClientChannelGroup(channelFeature.getChannelGroupId(), channelInfo.getId(), client.getDatabaseId());
                api.moveClient(client.getId(), channelInfo.getId());
                channelData.clear();
                channelData.put(ChannelProperty.CHANNEL_FLAG_SEMI_PERMANENT, "0");
                channelData.put(ChannelProperty.CHANNEL_FLAG_TEMPORARY, "1");
                api.editChannel(channelInfo.getId(), channelData);
                api.sendPrivateMessage(client.getId(), "    » Your [color=#00aaff]Channel[/color] has been created");
            }
        });
    }

    private long getRemainingSeconds(String uniqueId) {
        long millis = channelCreateCache.get(uniqueId)-System.currentTimeMillis();
        return (millis/1000)%60;
    }

    private void sendAdminMessage(TS3Api api, String message) {
        ByteClientManager byteClientManager = ByteSystem.getByteClientManager();
        for(OfflineByteClient current : byteClientManager.getAdminByteClients()) {
            if(!current.isOnline()) continue;
            api.sendPrivateMessage(api.getClientByUId(current.getUniqueId()).getId(), "    » "+message);
        }
    }

    private boolean isChannelCreateDelayed(String uniqueId, long delay) {
        Map<String, Long> delayCache = new HashMap<>(channelCreateCache);
        for(String current : delayCache.keySet()) {
            if(channelCreateCache.get(current) < System.currentTimeMillis())
                channelCreateCache.remove(current);
        }
        if(!channelCreateCache.containsKey(uniqueId)) {
            channelCreateCache.put(uniqueId, System.currentTimeMillis()+delay);
            return false;
        }
        if(channelCreateCache.get(uniqueId) > System.currentTimeMillis())
            return true;
        channelCreateCache.put(uniqueId, System.currentTimeMillis()+delay);
        return false;
    }

    private boolean isAlreadyChannelCreator(TS3Api api, ChannelFeature channelFeature, Client client) {
        List<ChannelGroupClient> list = new ArrayList<>(api.getChannelGroupClientsByChannelGroupId(channelFeature.getChannelGroupId()));
        list.removeIf(current -> current.getClientDatabaseId() != client.getDatabaseId());
        int mainChannelOrder = api.getChannelInfo(channelFeature.getChannelId()).getOrder();
        for(ChannelGroupClient current : list) {
            int currentOrder = api.getChannelInfo(current.getChannelId()).getOrder();
            if(currentOrder > mainChannelOrder) return true;
        }
        return false;
    }

}
