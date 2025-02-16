// Created by Eric B. 13.11.2021 22:34
package de.ericzones.bytesystem.collectives.byteclient.event;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.ericzones.bytesystem.ByteSystem;
import de.ericzones.bytesystem.collectives.byteclient.ByteClient;
import de.ericzones.bytesystem.collectives.byteclient.ByteClientManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClientJoinListener {

    public ClientJoinListener(TS3Api api) {
        registerEvent(api);
        ByteSystem.getByteClientManager().setAllByteClientsOffline();
        for(Client current : api.getClients()) {
            if(current.isServerQueryClient()) continue;
            if(current.isInServerGroup(17) || current.isInServerGroup(18)) continue;
            if(current.getNickname().contains(" ")) {
                api.kickClientFromServer("Your nickname contains spaces", current.getId());
                continue;
            }
            ByteSystem.getByteClientManager().restoreOnlinetime(current.getUniqueIdentifier());
            updateClientInDatabase(current, api);
        }
    }

    private void registerEvent(TS3Api api) {
        api.addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onClientJoin(ClientJoinEvent e) {
                if(!api.isClientOnline(e.getClientId())) return;
                Client client = api.getClientInfo(e.getClientId());
                if(client.isServerQueryClient()) return;
                ByteSystem.getFileManager().logConnect(client, Objects.requireNonNull(api.getChannels().stream().filter(channel -> channel.getId() == client.getChannelId()).findFirst().orElse(null)));
                if(client.isInServerGroup(17) || client.isInServerGroup(18)) return;
                if(client.getNickname().contains(" ")) {
                    api.kickClientFromServer("Your nickname contains spaces", client.getId());
                    return;
                }
                updateClientInDatabase(client, api);
            }
        });
    }

    private void updateClientInDatabase(Client client, TS3Api api) {
        List<Client> clients = new ArrayList<>();
        for(Client current : api.getClients()) {
            if(current.isServerQueryClient()) continue;
            if(current.getUniqueIdentifier().equals(client.getUniqueIdentifier()))
                clients.add(current);
        }
        if(clients.size() > 1) return;
        ByteClientManager byteClientManager = ByteSystem.getByteClientManager();
        if(api.getClients().stream().noneMatch(c -> c.getUniqueIdentifier().equals(client.getUniqueIdentifier()))) return;
        ByteClient byteClient = byteClientManager.getByteClient(client);
        byteClientManager.setByteClientOnline(byteClient);
    }

}
