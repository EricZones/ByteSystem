// Created by Eric B. 16.02.2022 19:53
package de.ericzones.bytesystem.engine;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.exception.TS3ConnectionFailedException;
import com.github.theholywaffle.teamspeak3.api.reconnect.ConnectionHandler;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.ericzones.bytesystem.ByteSystem;
import de.ericzones.bytesystem.collectives.byteclient.ByteClient;
import de.ericzones.bytesystem.collectives.byteclient.ByteClientManager;
import de.ericzones.bytesystem.collectives.byteclient.event.ClientJoinListener;
import de.ericzones.bytesystem.collectives.byteclient.event.ClientLeaveListener;
import de.ericzones.bytesystem.collectives.bytecore.ByteCoreManager;
import de.ericzones.bytesystem.collectives.bytefeature.channel.ChannelFeatureListener;
import de.ericzones.bytesystem.collectives.bytefeature.moderation.ModerationFeatureListener;
import de.ericzones.bytesystem.collectives.bytefeature.support.SupportFeatureListener;
import de.ericzones.bytesystem.events.ChannelListener;
import de.ericzones.bytesystem.events.ClientMovedListener;
import de.ericzones.bytesystem.events.TextMessageListener;

import java.util.Timer;
import java.util.TimerTask;

public class Query {

    private final int botId;
    private TS3Config config;
    private TS3Query query;

    public Query(int botId) {
        this.botId = botId;
        configure();
    }

    public void configure() {
        ByteCoreManager byteCoreManager = ByteSystem.getByteCoreManager();
        config = new TS3Config();
        config.setProtocol(TS3Query.Protocol.SSH);
        config.setFloodRate(TS3Query.FloodRate.UNLIMITED);
        config.setEnableCommunicationsLogging(false);
        config.setReconnectStrategy(ReconnectStrategy.exponentialBackoff());
        if(byteCoreManager.getByteCorePort(botId) == null)
            config.setHost(byteCoreManager.getByteCoreHost(botId));
        else
            config.setHost(byteCoreManager.getByteCoreHost(botId)+":"+byteCoreManager.getByteCorePort(botId));
        config.setLoginCredentials(byteCoreManager.getByteCoreLoginName(botId), byteCoreManager.getByteCorePassword(botId));

        config.setConnectionHandler(new ConnectionHandler() {
            @Override
            public void onConnect(TS3Api api) {
                api.selectVirtualServerById(byteCoreManager.getByteCoreServerId(botId));
                api.registerAllEvents();
                String nickname = byteCoreManager.getByteCoreNickname(botId);
                if(api.getClientByNameExact(nickname, false) == null)
                    api.setNickname(nickname);
                else if(!api.getClientByNameExact(nickname, false).isServerQueryClient())
                    System.out.println(" [ERROR] Configured nickname already in use");
                int channel = byteCoreManager.getByteCoreChannel(botId);
                if(channel == 0)
                    System.out.println(" [INFO] Channel has not been set");
                else if(api.getChannels().stream().noneMatch(ch -> ch.getId() == channel))
                    System.out.println(" [ERROR] Configured channel does not exist");
                else if(api.getClientByNameExact(nickname, false).getChannelId() == channel)
                    System.out.println(" [ERROR] Configured channel is the standard channel");
                else
                    api.moveQuery(channel);
            }

            @Override
            public void onDisconnect(TS3Query query) {
                if(byteCoreManager.byteCoreExists(botId))
                    byteCoreManager.setByteCoreOffline(botId);
            }
        });
        connect();
    }

    public void disconnect() {
        saveByteClients();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ByteCoreManager byteCoreManager = ByteSystem.getByteCoreManager();
                if (byteCoreManager.byteCoreExists(botId))
                    byteCoreManager.setByteCoreOffline(botId);
                if (query.isConnected())
                    query.exit();
                System.out.println(" [INFO] Query disconnected");
            }
        }, 500);
    }

    private void connect() {
        ByteCoreManager byteCoreManager = ByteSystem.getByteCoreManager();
        if(query != null && query.isConnected())
            disconnect();
        query = new TS3Query(config);
        try {
            query.connect();
            if(byteCoreManager.byteCoreExists(botId))
                byteCoreManager.setByteCoreOnline(botId);
            System.out.println(" [INFO] Query connected");
        } catch (TS3ConnectionFailedException e) {
            if(e.getLocalizedMessage().equalsIgnoreCase("Invalid query username or password"))
                System.out.println(" [ERROR] Wrong login name or password");
            else if(e.getLocalizedMessage().equalsIgnoreCase("Could not connect to the TeamSpeak3 server"))
                System.out.println(" [ERROR] Wrong host, port or server is offline");
            else if(e.getLocalizedMessage().equalsIgnoreCase("ConnectionHandler threw exception in connect handler"))
                System.out.println(" [ERROR] Wrong server id");
            else
                System.out.println(" [ERROR] "+e.getLocalizedMessage());
            if(byteCoreManager.byteCoreExists(botId))
                byteCoreManager.setByteCoreOffline(botId);
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                new ClientJoinListener(query.getApi());
                new ClientLeaveListener(query.getApi());
                new TextMessageListener(query.getApi());
                new ClientMovedListener(query.getApi());
                new ChannelListener(query.getApi());
                new ChannelFeatureListener(query.getApi());
                new SupportFeatureListener(query.getApi());
                new ModerationFeatureListener(query.getApi());
            }
        }, 5);
    }

    private void saveByteClients() {
        ByteClientManager byteClientManager = ByteSystem.getByteClientManager();
        for(Client current : query.getApi().getClients()) {
            if(current.isServerQueryClient()) continue;
            ByteClient byteClient = byteClientManager.getByteClientByUniqueId(current.getUniqueIdentifier());
            if(byteClient == null) continue;
            byteClientManager.setByteClientOffline(byteClient);
        }
    }

    public void stop() {
        disconnect();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(" [INFO] Bot is stopping...");
                System.exit(0);
            }
        }, 1000);
    }

    public boolean isConnected() {
        return query.isConnected();
    }

    public TS3Api getApi() {
        return query.getApi();
    }

}
