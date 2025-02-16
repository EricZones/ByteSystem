// Created by Eric B. 13.11.2021 22:48
package de.ericzones.bytesystem.collectives.byteclient.event;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.wrapper.Ban;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.ericzones.bytesystem.ByteSystem;
import de.ericzones.bytesystem.collectives.byteclient.ByteClient;
import de.ericzones.bytesystem.collectives.byteclient.ByteClientManager;
import de.ericzones.bytesystem.collectives.file.FileManager;

public class ClientLeaveListener {

    public ClientLeaveListener(TS3Api api) {
        registerEvent(api);
    }

    private void registerEvent(TS3Api api) {
        api.addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onClientLeave(ClientLeaveEvent e) {
                int clientId = e.getClientId();
                ByteClient byteClient = updateClientInDatabase(clientId, api);
                if(byteClient == null) return;
                FileManager fileManager = ByteSystem.getFileManager();
                if(e.getInvokerId() == -1) {
                    fileManager.logDisconnect(byteClient.getNickname(), e.getReasonMessage());
                    return;
                }
                if(e.getReasonId() == 5)
                    fileManager.logServerKick(api.getClientInfo(e.getInvokerId()), byteClient.getNickname(), e.getReasonMessage());
                else if(e.getReasonId() == 6) {
                    Ban ban = api.getBans().stream().filter(current -> current.getBannedUId().equals(byteClient.getUniqueId()) && current.getInvokerUId().equals(api.getClientInfo(e.getInvokerId()).getUniqueIdentifier())).findFirst().orElse(null);
                    if(ban == null) return;
                    fileManager.logBan(api.getClientInfo(e.getInvokerId()), byteClient.getNickname(), getDuration(ban.getDuration()), e.getReasonMessage());
                }
            }
        });
    }

    private ByteClient updateClientInDatabase(int clientId, TS3Api api) {
        ByteClientManager byteClientManager = ByteSystem.getByteClientManager();
        ByteClient byteClient = byteClientManager.getByteClient(clientId);
        if(byteClient == null) return null;
        for(Client current : api.getClients())
            if(current.getUniqueIdentifier().equals(byteClient.getUniqueId())) return null;
        byteClientManager.setByteClientOffline(byteClient);
        return byteClient;
    }

    private String getDuration(long durationSeconds) {
        long seconds = durationSeconds, minutes = 0, hours = 0, days = 0;
        while(seconds >= 60) {
            seconds-=60;
            minutes++;
        }
        while(minutes >= 60) {
            minutes-=60;
            hours++;
        }
        while(hours >= 24) {
            hours-=24;
            days++;
        }
        return days + "d " + hours + "h " + minutes + "m " + seconds + "s";
    }

}
