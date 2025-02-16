// Created by Eric B. 19.02.2022 16:55
package de.ericzones.bytesystem.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ChannelEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.ericzones.bytesystem.ByteSystem;

public class ChannelListener {

    public ChannelListener(TS3Api api) {
        registerEvent(api);
    }

    private void registerEvent(TS3Api api) {
        api.addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onChannelEdit(ChannelEditedEvent e) {
                Client client = api.getClientInfo(e.getInvokerId());
                if(client == null || client.isServerQueryClient()) return;
                Channel channel = api.getChannels().stream().filter(current -> current.getId() == e.getChannelId()).findFirst().orElse(null);
                if(channel == null) return;
                ByteSystem.getFileManager().logChannelEdit(client, channel);
            }
        });
    }

}
