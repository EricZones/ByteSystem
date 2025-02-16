// Created by Eric B. 16.02.2022 19:51
package de.ericzones.bytesystem.engine;

import de.ericzones.bytesystem.ByteSystem;
import de.ericzones.bytesystem.collectives.byteclient.ByteClient;
import de.ericzones.bytesystem.collectives.byteclient.ByteClientManager;
import de.ericzones.bytesystem.collectives.bytecore.ByteCoreManager;
import de.ericzones.bytesystem.collectives.bytefeature.FeatureType;
import de.ericzones.bytesystem.collectives.bytefeature.channel.ChannelFeature;

import java.util.Scanner;

public class ConsoleListener {

    private final int botId;
    private boolean inputEnabled;

    public ConsoleListener(int botId) {
        this.botId = botId;
        this.inputEnabled = true;
        registerConsole();
    }

    private void registerConsole() {
        ByteCoreManager byteCoreManager = ByteSystem.getByteCoreManager();
        Query query = ByteSystem.getQuery();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if(!inputEnabled) continue;
            System.out.println(" ");
            System.out.println(" ########### ByteSystem ###########");
            System.out.println(" - Msg (Notify user)");
            System.out.println(" - Admin (Add admin)");
            System.out.println(" - Features (Configure features)");
            System.out.println(" - Settings (Configure query)");
            System.out.println(" - Connect (Connect query)");
            System.out.println(" - Disconnect (Disconnect query)");
            System.out.println(" - Stop (Stop bot)");
            String s = scanner.next();
            if(s.equalsIgnoreCase("disconnect")) {
                query.disconnect();
                continue;
            } else if(s.equalsIgnoreCase("clear") || s.equalsIgnoreCase("cl")) {
                for(int i = 0; i < 50; i++)
                    System.out.println(" ");
                continue;
            } else if(s.equalsIgnoreCase("stop")) {
                query.stop();
                return;
            } else if(s.equalsIgnoreCase("msg")) {
                if(!ByteSystem.getQuery().isConnected()) {
                    System.out.println(" ");
                    System.out.println(" [ERROR] Query not connected");
                    continue;
                }
                System.out.println(" ");
                System.out.println(" ################ Msg ################");
                System.out.println(" » Enter the user nickname");
                String argument = scanner.next();
                if(argument.equalsIgnoreCase("back"))
                    continue;
                ByteClientManager byteClientManager = ByteSystem.getByteClientManager();
                if(!byteClientManager.isByteClientOnline(argument)) {
                    System.out.println(" ");
                    System.out.println(" [ERROR] User was not found");
                    continue;
                }
                ByteClient byteClient = byteClientManager.getByteClientByNickname(argument);
                System.out.println(" ");
                System.out.println(" ################ Msg ################");
                System.out.println(" » Enter the message");
                argument = scanner.nextLine();
                if(argument.equalsIgnoreCase("back"))
                    continue;
                ByteSystem.getQuery().getApi().sendPrivateMessage(byteClient.getClientId(), "    » "+argument);
                System.out.println(" ");
                System.out.println(" [INFO] User "+byteClient.getNickname()+" has been notified");
                continue;
            } else if(s.equalsIgnoreCase("admin")) {
                System.out.println(" ");
                System.out.println(" ############## Admin ##############");
                System.out.println(" » Enter a unique id");
                String argument = scanner.next();
                if(argument.equalsIgnoreCase("back"))
                    continue;
                ByteClientManager byteClientManager = ByteSystem.getByteClientManager();
                if(!byteClientManager.byteClientExistsByUniqueId(argument)) {
                    System.out.println(" ");
                    System.out.println(" [ERROR] Unique id is not registered");
                } else {
                    byteClientManager.setByteClientAdmin(argument, true);
                    System.out.println(" ");
                    System.out.println(" [INFO] New unique id registered");
                }
                continue;
            } else if(s.equalsIgnoreCase("connect")) {
                query.configure();
                continue;
            } else if(s.equalsIgnoreCase("features")) {
                System.out.println(" ");
                System.out.println(" ############ Features ############");
                System.out.println(" - Channel ("+(ByteSystem.getByteFeatureManager().getFeature(FeatureType.CHANNEL).isEnabled() ? "Enabled" : "Disabled")+")");
                String setting = scanner.next();
                if(setting.equalsIgnoreCase("channel")) {
                    ChannelFeature channelFeature = (ChannelFeature) ByteSystem.getByteFeatureManager().getFeature(FeatureType.CHANNEL);
                    System.out.println(" ");
                    System.out.println(" ############## Channel ##############");
                    System.out.println(" - Status ("+(channelFeature.isEnabled() ? "Enabled" : "Disabled")+")");
                    System.out.println(" - ChannelId ("+channelFeature.getChannelId()+")");
                    System.out.println(" - ChannelName ("+channelFeature.getChannelName()+")");
                    System.out.println(" - ChannelGroupId ("+channelFeature.getChannelGroupId()+")");
                    System.out.println(" - Delay ("+(channelFeature.getDelay()/1000)+"s)");
                    setting = scanner.next();
                    if(setting.equalsIgnoreCase("status")) {
                        System.out.println(" ");
                        System.out.println(" ############## Status ##############");
                        System.out.println(" » Enable/disable feature (0 & 1)");
                        String argument = scanner.next();
                        if(argument.equalsIgnoreCase("back"))
                            continue;
                        if(argument.equalsIgnoreCase("0")) {
                            ByteSystem.getByteFeatureManager().getFeature(FeatureType.CHANNEL).setEnabled(false);
                            System.out.println(" ");
                            System.out.println(" [INFO] Feature Channel disabled");
                            continue;
                        } else if(argument.equalsIgnoreCase("1")) {
                            ByteSystem.getByteFeatureManager().getFeature(FeatureType.CHANNEL).setEnabled(true);
                            System.out.println(" ");
                            System.out.println(" [INFO] Feature Channel enabled");
                            continue;
                        }
                        System.out.println(" ");
                        System.out.println(" [ERROR] Invalid input (0 or 1)");
                        continue;
                    } else if(setting.equalsIgnoreCase("channelid")) {
                        System.out.println(" ");
                        System.out.println(" ############ Channel ID ############");
                        System.out.println(" » Enter channel id");
                        String argument = scanner.next();
                        if(argument.equalsIgnoreCase("back"))
                            continue;
                        int channelId;
                        try {
                            channelId = Integer.parseInt(argument);
                        } catch (NumberFormatException ex) {
                            System.out.println(" ");
                            System.out.println(" [ERROR] Invalid input (Number expected)");
                            continue;
                        }
                        if(query.getApi().getChannels().stream().noneMatch(ch -> ch.getId() == channelId)) {
                            System.out.println(" ");
                            System.out.println(" [ERROR] Channel id does not exist");
                            continue;
                        }
                        ((ChannelFeature) ByteSystem.getByteFeatureManager().getFeature(FeatureType.CHANNEL)).setChannelId(channelId);
                        System.out.println(" ");
                        System.out.println(" [INFO] New channel id set ("+channelId+")");
                        continue;
                    } else if(setting.equalsIgnoreCase("channelname")) {
                        System.out.println(" ");
                        System.out.println(" ########### Channel Name ###########");
                        System.out.println(" » Enter channel name");
                        String argument = scanner.next();
                        if(argument.equalsIgnoreCase("back"))
                            continue;
                        if(argument.length() == 0) {
                            System.out.println(" ");
                            System.out.println(" [ERROR] Invalid input");
                            continue;
                        }
                        argument = argument.replace("_", " ");
                        ((ChannelFeature) ByteSystem.getByteFeatureManager().getFeature(FeatureType.CHANNEL)).setChannelName(argument.trim());
                        System.out.println(" ");
                        System.out.println(" [INFO] New channel name set ("+argument.trim()+")");
                        continue;
                    } else if(setting.equalsIgnoreCase("channelgroupid")) {
                        System.out.println(" ");
                        System.out.println(" ########### Channel Gruppen ID ###########");
                        System.out.println(" » Enter channel group id");
                        String argument = scanner.next();
                        if(argument.equalsIgnoreCase("back"))
                            continue;
                        int groupId;
                        try {
                            groupId = Integer.parseInt(argument);
                        } catch (NumberFormatException ex) {
                            System.out.println(" ");
                            System.out.println(" [ERROR] Invalid input (Number expected)");
                            continue;
                        }
                        if(query.getApi().getChannelGroups().stream().noneMatch(ch -> ch.getId() == groupId)) {
                            System.out.println(" ");
                            System.out.println(" [ERROR] Channel group id does not exist");
                            continue;
                        }
                        ((ChannelFeature) ByteSystem.getByteFeatureManager().getFeature(FeatureType.CHANNEL)).setChannelGroupId(groupId);
                        System.out.println(" ");
                        System.out.println(" [INFO] New channel group id set ("+groupId+")");
                        continue;
                    } else if(setting.equalsIgnoreCase("delay")) {
                        System.out.println(" ");
                        System.out.println(" ############## Delay ##############");
                        System.out.println(" » Enter channel delay (seconds)");
                        String argument = scanner.next();
                        if(argument.equalsIgnoreCase("back"))
                            continue;
                        long delay;
                        try {
                            delay = Long.parseLong(argument);
                        } catch (NumberFormatException ex) {
                            System.out.println(" ");
                            System.out.println(" [ERROR] Invalid input (Number expected)");
                            continue;
                        }
                        if(delay < 0) delay = 0;
                        delay = delay*1000;
                        ((ChannelFeature) ByteSystem.getByteFeatureManager().getFeature(FeatureType.CHANNEL)).setDelay(delay);
                        System.out.println(" ");
                        System.out.println(" [INFO] New channel delay set ("+(delay/1000)+"s)");
                        continue;
                    }
                }
                System.out.println(" ");
                System.out.println(" [ERROR] Invalid input");
            } else if(s.equalsIgnoreCase("settings") || s.equalsIgnoreCase("options")) {
                System.out.println(" ");
                System.out.println(" ############# Settings #############");
                System.out.println(" - Host ("+byteCoreManager.getByteCoreHost(botId)+")");
                System.out.println(" - Port ("+byteCoreManager.getByteCorePort(botId)+")");
                System.out.println(" - ServerId ("+byteCoreManager.getByteCoreServerId(botId)+")");
                System.out.println(" - LoginName ("+byteCoreManager.getByteCoreLoginName(botId)+")");
                System.out.println(" - Password ("+byteCoreManager.getByteCorePassword(botId)+")");
                System.out.println(" - Nickname ("+byteCoreManager.getByteCoreNickname(botId)+")");
                System.out.println(" - Channel ("+byteCoreManager.getByteCoreChannel(botId)+")");
                String setting = scanner.next();
                if(setting.equalsIgnoreCase("host")) {
                    System.out.println(" ");
                    System.out.println(" ############## Host ##############");
                    System.out.println(" » Enter a host");
                    String argument = scanner.next();
                    if(argument.equalsIgnoreCase("back"))
                        continue;
                    byteCoreManager.setByteCoreHost(botId, argument);
                    System.out.println(" ");
                    System.out.println(" [INFO] New host set: "+argument);
                    query.disconnect();
                    query.configure();
                    continue;
                } else if(setting.equalsIgnoreCase("port")) {
                    System.out.println(" ");
                    System.out.println(" ############## Port ##############");
                    System.out.println(" » Enter a port");
                    String argument = scanner.next();
                    if(argument.equalsIgnoreCase("back"))
                        continue;
                    byteCoreManager.setByteCorePort(botId, argument);
                    System.out.println(" ");
                    System.out.println(" [INFO] New port set: "+argument);
                    query.disconnect();
                    query.configure();
                    continue;
                } else if(setting.equalsIgnoreCase("serverid")) {
                    System.out.println(" ");
                    System.out.println(" ############ ServerId ############");
                    System.out.println(" » Enter a server id");
                    String argument = scanner.next();
                    if(argument.equalsIgnoreCase("back"))
                        continue;
                    byteCoreManager.setByteCoreServerId(botId, Integer.parseInt(argument));
                    System.out.println(" ");
                    System.out.println(" [INFO] New server id set: "+argument);
                    query.disconnect();
                    query.configure();
                    continue;
                } else if(setting.equalsIgnoreCase("loginname")) {
                    System.out.println(" ");
                    System.out.println(" ############ LoginName ############");
                    System.out.println(" » Enter a login name");
                    String argument = scanner.next();
                    if(argument.equalsIgnoreCase("back"))
                        continue;
                    byteCoreManager.setByteCoreLoginName(botId, argument);
                    System.out.println(" ");
                    System.out.println(" [INFO] New login name set: "+argument);
                    query.disconnect();
                    query.configure();
                    continue;
                } else if(setting.equalsIgnoreCase("password")) {
                    System.out.println(" ");
                    System.out.println(" ############ Password ############");
                    System.out.println(" » Enter a password");
                    String argument = scanner.next();
                    if(argument.equalsIgnoreCase("back"))
                        continue;
                    byteCoreManager.setByteCorePassword(botId, argument);
                    System.out.println(" ");
                    System.out.println(" [INFO] New password set: "+argument);
                    query.disconnect();
                    query.configure();
                    continue;
                } else if(setting.equalsIgnoreCase("nickname")) {
                    System.out.println(" ");
                    System.out.println(" ############ Nickname ############");
                    System.out.println(" » Enter a nickname");
                    String argument = scanner.next();
                    if(argument.equalsIgnoreCase("back"))
                        continue;
                    if(query.getApi().getClientByNameExact(argument, false) == null) {
                        byteCoreManager.setByteCoreNickname(botId, argument);
                        System.out.println(" ");
                        System.out.println(" [INFO] New nickname set: " + argument);
                        query.getApi().setNickname(argument);
                    } else if(byteCoreManager.getByteCoreNickname(botId).equals(argument)) {
                        System.out.println(" ");
                        System.out.println(" [ERROR] Entered nickname is already used by the query");
                    } else {
                        System.out.println(" ");
                        System.out.println(" [ERROR] Entered nickname is already in use");
                    }
                    continue;
                } else if(setting.equalsIgnoreCase("channel")) {
                    System.out.println(" ");
                    System.out.println(" ############# Channel #############");
                    System.out.println(" » Enter a channel");
                    String argument = scanner.next();
                    if(argument.equalsIgnoreCase("back"))
                        continue;
                    if(query.getApi().getChannels().stream().noneMatch(ch -> ch.getId() == Integer.parseInt(argument))) {
                        System.out.println(" ");
                        System.out.println(" [ERROR] Entered channel does not exist");
                    } else if(query.getApi().getClientByNameExact(byteCoreManager.getByteCoreNickname(botId), false).getChannelId() == Integer.parseInt(argument)) {
                        System.out.println(" ");
                        System.out.println(" [ERROR] Entered channel is already the query channel");
                    } else {
                        byteCoreManager.setByteCoreChannel(botId, Integer.parseInt(argument));
                        System.out.println(" ");
                        System.out.println(" [INFO] New channel set: "+argument);
                        query.getApi().moveQuery(Integer.parseInt(argument));
                    }
                    continue;
                }
                System.out.println(" ");
                System.out.println(" [ERROR] Invalid input");
            }
        }
    }

}
