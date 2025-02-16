// Created by Eric B. 19.02.2022 14:41
package de.ericzones.bytesystem.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.ericzones.bytesystem.ByteSystem;
import de.ericzones.bytesystem.collectives.file.FileManager;

public class ClientMovedListener {

    public ClientMovedListener(TS3Api api) {
        registerEvent(api);
    }

    private void registerEvent(TS3Api api) {
        api.addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onClientMoved(ClientMovedEvent e) {
                Client client = api.getClientInfo(e.getClientId());
                if(client == null) return;
                if(client.isServerQueryClient()) return;
                Channel channel = api.getChannels().stream().filter(current -> current.getId() == e.getTargetChannelId()).findFirst().orElse(null);
                if(channel == null) return;
                FileManager fileManager = ByteSystem.getFileManager();
                if(e.getInvokerId() != -1) {
                    if(e.getReasonId() == 4) {
                        fileManager.logChannelKick(api.getClientInfo(e.getInvokerId()), client, channel, e.getReasonMessage());
                        return;
                    }
                    fileManager.logMove(api.getClientInfo(e.getInvokerId()), client, channel);
                } else
                    fileManager.logSwitch(client, channel);
            }
        });
    }

}
