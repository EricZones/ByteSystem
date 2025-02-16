// Created by Eric B. 06.03.2022 16:53
package de.ericzones.bytesystem.collectives.bytefeature.support;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.ericzones.bytesystem.ByteSystem;
import de.ericzones.bytesystem.collectives.bytefeature.FeatureType;

public class SupportFeatureListener {

    public SupportFeatureListener(TS3Api api) {
        ((SupportFeature)ByteSystem.getByteFeatureManager().getFeature(FeatureType.SUPPORT)).checkSupportStatus(api);
        registerEvent(api);
    }

    private void registerEvent(TS3Api api) {
        api.addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onClientMoved(ClientMovedEvent e) {
                Client client = api.getClientInfo(e.getClientId());
                if(client == null || client.isServerQueryClient()) return;
                SupportFeature supportFeature = (SupportFeature) ByteSystem.getByteFeatureManager().getFeature(FeatureType.SUPPORT);
                if(supportFeature == null || !supportFeature.isEnabled()) return;
                if(supportFeature.getChannelId() != e.getTargetChannelId()) return;
                if(client.isInServerGroup(supportFeature.getNotifyGroupId())) {
                    api.sendPrivateMessage(client.getId(), "    » The [color=red]Notify Group[/color] is ignored in the support");
                    return;
                }
                if(!supportFeature.isOpen()) {
                    api.sendPrivateMessage(client.getId(), "    » The [color=red]Support[/color] is currently closed");
                    api.kickClientFromChannel(client.getId());
                    return;
                }
                int supporter = 0;
                for(Client current : api.getClients()) {
                    if(current.isInServerGroup(supportFeature.getNotifyGroupId())) {
                        supporter++;
                        api.sendPrivateMessage(current.getId(), "    » User [URL=client://" + client.getId() + "/" + client.getUniqueIdentifier() + "]" + client.getNickname() + "[/URL] needs support");
                    }
                }
                if(supporter > 1)
                    api.sendPrivateMessage(client.getId(), "    » All [color=#00aaff]"+supporter+"[/color] team members were notified");
                else if(supporter == 1)
                    api.sendPrivateMessage(client.getId(), "    » Just [color=#00aaff]"+supporter+"[/color] team member was notified");
                else
                    api.sendPrivateMessage(client.getId(), "    » Currently no [color=red]team member[/color] is online");
            }
        });
    }

}
