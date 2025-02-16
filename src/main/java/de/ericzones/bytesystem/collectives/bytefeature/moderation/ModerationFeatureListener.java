// Created by Eric B. 10.03.2022 17:06
package de.ericzones.bytesystem.collectives.bytefeature.moderation;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.ericzones.bytesystem.ByteSystem;
import de.ericzones.bytesystem.collectives.bytefeature.FeatureType;
import de.ericzones.bytesystem.collectives.bytefeature.channel.ChannelFeature;
import de.ericzones.bytesystem.collectives.bytefeature.punish.PunishFeature;
import de.ericzones.bytesystem.collectives.database.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ModerationFeatureListener {

    private final Timer scheduler = new Timer();
    private final Map<String, Pair<Long, Integer>> channelSwitchCache = new HashMap<>();

    public ModerationFeatureListener(TS3Api api) {
        registerEvent(api);
        configureNameChecker();
    }

    private void registerEvent(TS3Api api) {
        api.addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onClientMoved(ClientMovedEvent e) {
                Client client = api.getClientInfo(e.getClientId());
                if (client == null || client.isServerQueryClient()) return;
                ModerationFeature moderationFeature = (ModerationFeature) ByteSystem.getByteFeatureManager().getFeature(FeatureType.MODERATION);
                if (!moderationFeature.isEnabled()) return;
                if (!moderationFeature.isConfigured() || !moderationFeature.isSwitchCheck()) return;
                if (e.getInvokerId() != -1) return;

                for (int groupId : client.getServerGroups())
                    if (moderationFeature.getIgnoredGroups().contains(groupId)) return;

                ChannelFeature channelFeature = (ChannelFeature) ByteSystem.getByteFeatureManager().getFeature(FeatureType.CHANNEL);
                if (channelFeature.isEnabled() && channelFeature.isConfigured() && channelFeature.getChannelId() == e.getTargetChannelId()) return;
                if (!switchedTooFast(moderationFeature, client)) return;
                checkComplains(api, moderationFeature, client);
            }
        }, new TS3EventAdapter() {
            @Override
            public void onClientJoin(ClientJoinEvent e) {
                Client client = api.getClientInfo(e.getClientId());
                if (client == null || client.isServerQueryClient()) return;
                ModerationFeature moderationFeature = (ModerationFeature) ByteSystem.getByteFeatureManager().getFeature(FeatureType.MODERATION);
                if (!moderationFeature.isEnabled()) return;
                if (!moderationFeature.isConfigured() || !moderationFeature.isNameCheck()) return;

                for (int groupId : client.getServerGroups())
                    if (moderationFeature.getIgnoredGroups().contains(groupId)) return;

                for(String current : moderationFeature.getForbiddenNicknames()) {
                    if(client.getNickname().toLowerCase().contains(current)) {
                        scheduler.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                PunishFeature punishFeature = (PunishFeature) ByteSystem.getByteFeatureManager().getFeature(FeatureType.PUNISH);
                                if(punishFeature.isClientPunished(client)) return;
                                api.kickClientFromServer("Your nickname contains forbidden words", client);
                            }
                        }, 100);
                        return;
                    }
                }
            }
        });
    }

    private Timer timer;
    private TimerTask timerTask;

    private void configureNameChecker() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                ModerationFeature moderationFeature = (ModerationFeature) ByteSystem.getByteFeatureManager().getFeature(FeatureType.MODERATION);
                if (!moderationFeature.isEnabled() || !moderationFeature.isConfigured() || !moderationFeature.isNameCheck()) return;
                outerloop:
                for(Client current : ByteSystem.getQuery().getApi().getClients()) {
                    if (current == null || current.isServerQueryClient()) continue;
                    for (int groupId : current.getServerGroups())
                        if (moderationFeature.getIgnoredGroups().contains(groupId)) continue outerloop;
                    for(String nickname : moderationFeature.getForbiddenNicknames()) {
                        if (current.getNickname().toLowerCase().contains(nickname)) {
                            PunishFeature punishFeature = (PunishFeature) ByteSystem.getByteFeatureManager().getFeature(FeatureType.PUNISH);
                            if(punishFeature.isClientPunished(current)) continue outerloop;
                            ByteSystem.getQuery().getApi().kickClientFromServer("Your nickname contains forbidden words", current);
                            continue outerloop;
                        }
                    }
                }
            }
        };
        ModerationFeature moderationFeature = (ModerationFeature) ByteSystem.getByteFeatureManager().getFeature(FeatureType.MODERATION);
        if (moderationFeature.isEnabled() && moderationFeature.isConfigured() && moderationFeature.isNameCheck())
            toggleNameChecker();
    }

    private void toggleNameChecker() {
        if(timer != null) {
            timerTask.cancel();
            timer = null;
            return;
        }
        timer = new Timer();
        timer.schedule(timerTask, 0, 5*1000);
    }

    private void checkComplains(TS3Api api, ModerationFeature moderationFeature, Client client) {
        if(!channelSwitchCache.containsKey(client.getUniqueIdentifier())) return;
        int complains = channelSwitchCache.get(client.getUniqueIdentifier()).getSecondObject();
        if(complains >= moderationFeature.getSwitchComplainsLimit()) {
            channelSwitchCache.remove(client.getUniqueIdentifier());
            PunishFeature punishFeature = (PunishFeature)ByteSystem.getByteFeatureManager().getFeature(FeatureType.PUNISH);
            if(punishFeature.isClientPunished(client)) return;
            api.kickClientFromServer("Channelhopping is forbidden", client);
        } else if(complains == moderationFeature.getSwitchComplainsLimit()/2)
            api.sendPrivateMessage(client.getId(), "    » On this server [color=red]Channelhopping[/color] is forbidden");
    }

    private boolean switchedTooFast(ModerationFeature moderationFeature, Client client) {
        for(String current : channelSwitchCache.keySet()) {
            if (channelSwitchCache.get(current).getFirstObject() < System.currentTimeMillis()) channelSwitchCache.remove(current);
        }
        if(channelSwitchCache.containsKey(client.getUniqueIdentifier())) {
            int complains = channelSwitchCache.get(client.getUniqueIdentifier()).getSecondObject()+1;
            channelSwitchCache.put(client.getUniqueIdentifier(), new Pair(System.currentTimeMillis()+moderationFeature.getSwitchDelay(), complains));
            return true;
        }
        channelSwitchCache.put(client.getUniqueIdentifier(), new Pair(System.currentTimeMillis()+moderationFeature.getSwitchDelay(), 1));
        return false;
    }

}
