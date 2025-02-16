// Created by Eric B. 25.11.2021 15:28
package de.ericzones.bytesystem.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;
import de.ericzones.bytesystem.ByteSystem;
import de.ericzones.bytesystem.collectives.byteclient.ByteClient;
import de.ericzones.bytesystem.collectives.byteclient.ByteClientManager;
import de.ericzones.bytesystem.collectives.byteclient.OfflineByteClient;
import de.ericzones.bytesystem.collectives.bytefeature.Feature;
import de.ericzones.bytesystem.collectives.bytefeature.FeatureType;
import de.ericzones.bytesystem.collectives.bytefeature.channel.ChannelFeature;
import de.ericzones.bytesystem.collectives.bytefeature.moderation.ModerationFeature;
import de.ericzones.bytesystem.collectives.bytefeature.punish.PunishFeature;
import de.ericzones.bytesystem.collectives.bytefeature.support.SupportFeature;
import de.ericzones.bytesystem.collectives.file.LogCommand;
import de.ericzones.bytesystem.collectives.file.LogType;

import java.util.*;

public class TextMessageListener {

    private final String messageHeader = "\n\n\n»---------------------=====[color=#00aaff][[/color][b]» "+ByteSystem.getName()+" «[/b][color=#00aaff]][/color]=====---------------------«\n\n";
    private final String messageFooter = "\n»---------------------=====[color=#00aaff][[/color][b]» "+ByteSystem.getName()+" «[/b][color=#00aaff]][/color]=====---------------------«";
    private final String permissionMessage = "    » Insufficient [color=red]permissions[/color] for this [color=red]command[/color]";
    private final String syntaxMessage = "    » Wrong [color=red]Syntax[/color]. Use [color=red]%REPLACE%[/color] for help";
    private final String userIdMessage = "    » This [color=red]Unique Id[/color] is not registered";
    private final String userMessage = "    » This [color=red]User[/color] was not found";

    private final Timer timer = new Timer();

    public TextMessageListener(TS3Api api) {
        registerEvent(api);
    }

    private void registerEvent(TS3Api api) {
        api.addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onTextMessage(TextMessageEvent e) {
                Client client = api.getClientInfo(e.getInvokerId());
                if(client.isServerQueryClient()) return;
                if(e.getTargetMode() != TextMessageTargetMode.CLIENT) {
                    logMessage(e.getTargetMode(), client, e.getMessage().trim());
                    return;
                }
                ByteClient byteClient = ByteSystem.getByteClientManager().getByteClient(client);
                String message = e.getMessage().trim();

                if(message.equalsIgnoreCase("!info")) {
                    logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                    api.sendPrivateMessage(client.getId(),
                            messageHeader+
                            "    [color=#00aaff]"+ByteSystem.getName()+"[/color]\n" +
                            "    BotId [color=#00aaff]"+ByteSystem.getBotId()+"[/color]\n" +
                            "    Version [color=#00aaff]"+ByteSystem.getVersion()+"[/color]\n" +
                            "    Status "+ByteSystem.getChannel()+"\n" +
                            "    Developer [color=#00aaff]EricZones[/color]\n" +
                            messageFooter);
                    return;
                }

                if(message.equalsIgnoreCase("!help")) {
                    logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                    StringBuilder builder = new StringBuilder(messageHeader+
                            "    » Use [color=#00aaff]!info[/color] to get information about the bot\n" +
                            "    » Use [color=#00aaff]!help[/color] to see all commands\n" +
                            "    » Use [color=#00aaff]!onlinetime[/color] to see top-onlinetime user\n");
                    if(byteClient.isAdmin()) {
                        builder.append(
                                "    » Use [color=#00aaff]!channel[/color] to manage channel\n" +
                                "    » Use [color=#00aaff]!msg <Name> <Message>[/color] to notify an user\n" +
                                "    » Use [color=#00aaff]!check <Name>[/color] to see information about an user\n" +
                                "    » Use [color=#00aaff]!feature[/color] to manage features\n" +
                                "    » Use [color=#00aaff]!admin[/color] to manage admins\n" +
                                "    » Use [color=#00aaff]!disconnect[/color] to disconnect the query\n" +
                                "    » Use [color=#00aaff]!stop[/color] to stop the bot\n");
                    }
                    builder.append(messageFooter);
                    api.sendPrivateMessage(client.getId(), builder.toString());
                    return;
                }

                if(message.equalsIgnoreCase("!disconnect")) {
                    if(!byteClient.isAdmin()) {
                        logCommand(client, e.getMessage().trim(), LogCommand.DISALLOWED);
                        api.sendPrivateMessage(client.getId(), permissionMessage);
                        return;
                    }
                    logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                    api.sendPrivateMessage(client.getId(), "    » Query is disconnecting...");
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            ByteSystem.getQuery().disconnect();
                        }
                    }, 5);
                    return;
                }

                if(message.equalsIgnoreCase("!stop")) {
                    if(!byteClient.isAdmin()) {
                        logCommand(client, e.getMessage().trim(), LogCommand.DISALLOWED);
                        api.sendPrivateMessage(client.getId(), permissionMessage);
                        return;
                    }
                    logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                    api.sendPrivateMessage(client.getId(), "    » Bot is stopping...");
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            ByteSystem.getQuery().stop();
                        }
                    }, 5);
                    return;
                }

                if(message.equalsIgnoreCase("!feature")) {
                    if(!byteClient.isAdmin()) {
                        logCommand(client, e.getMessage().trim(), LogCommand.DISALLOWED);
                        api.sendPrivateMessage(client.getId(), permissionMessage);
                        return;
                    }
                    logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                    api.sendPrivateMessage(client.getId(),
                            "\n\n\n»--------------------=====[color=#00aaff][[/color][b]» Features «[/b][color=#00aaff]][/color]=====--------------------«\n\n"+
                                    "    » Syntax: [i]!feature <Feature> [<Option>] [<Value>][/i]\n\n" +
                                    "    [color=#00aaff]Channel[/color] • ("+(ByteSystem.getByteFeatureManager().getFeature(FeatureType.CHANNEL).isEnabled() ? "[color=green]✔" : "[color=red]✖")+"[/color])\n" +
                                    "    [color=#00aaff]Support[/color] • ("+(ByteSystem.getByteFeatureManager().getFeature(FeatureType.SUPPORT).isEnabled() ? "[color=green]✔" : "[color=red]✖")+"[/color])\n" +
                                    "    [color=#00aaff]Moderation[/color] • ("+(ByteSystem.getByteFeatureManager().getFeature(FeatureType.MODERATION).isEnabled() ? "[color=green]✔" : "[color=red]✖")+"[/color])\n" +
                                    "    [color=#00aaff]Punish[/color] • ("+(ByteSystem.getByteFeatureManager().getFeature(FeatureType.PUNISH).isEnabled() ? "[color=green]✔" : "[color=red]✖")+"[/color])\n"
                                    +"\n»--------------------=====[color=#00aaff][[/color][b]» Features «[/b][color=#00aaff]][/color]=====--------------------«");
                    return;
                }

                if(message.startsWith("!feature ")) {
                    if(!byteClient.isAdmin()) {
                        logCommand(client, e.getMessage().trim(), LogCommand.DISALLOWED);
                        api.sendPrivateMessage(client.getId(), permissionMessage);
                        return;
                    }
                    message = message.replace("!feature ", "").trim();
                    String[] args = getArgs(message, 3);
                    if(args == null) {
                        args = getArgs(message, 1);
                        if(args == null) {
                            logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                            api.sendPrivateMessage(client.getId(), syntaxMessage.replace("%REPLACE%", "!feature"));
                            return;
                        }
                        FeatureType featureType = FeatureType.getFeature(args[0]);
                        if(featureType == null) {
                            logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                            api.sendPrivateMessage(client.getId(), syntaxMessage.replace("%REPLACE%", "!feature"));
                            return;
                        }
                        logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                        Feature feature = ByteSystem.getByteFeatureManager().getFeature(featureType);
                        StringBuilder stringBuilder = new StringBuilder("\n\n\n»--------------------=====[color=#00aaff][[/color][b]» "+featureType.getName()+" «[/b][color=#00aaff]][/color]=====--------------------«\n\n");
                        stringBuilder.append("    » Syntax: [i]!feature "+featureType.getName()+" <Option> <Value>[/i]\n\n");
                        stringBuilder.append("    [color=#00aaff]Status[/color] • "+(feature.isEnabled() ? "[color=green]✔" : "[color=red]✖")+"[/color]\n");
                        switch (featureType) {
                            case CHANNEL:
                                stringBuilder.append(
                                        "    [color=#00aaff]ChannelID[/color] • "+(((ChannelFeature)feature).getChannelId() == -1 ? "/" : ((ChannelFeature)feature).getChannelId())+"\n"+
                                        "    [color=#00aaff]ChannelName[/color] • "+(((ChannelFeature)feature).getChannelName() == null ? "/" : ((ChannelFeature)feature).getChannelName())+"\n"+
                                        "    [color=#00aaff]ChannelGroupID[/color] • "+(((ChannelFeature)feature).getChannelGroupId() == -1 ? "/" : ((ChannelFeature)feature).getChannelGroupId())+"\n"+
                                        "    [color=#00aaff]Delay[/color] • "+(((ChannelFeature)feature).getDelay() == -1 ? "/" : ((ChannelFeature)feature).getDelay()/1000+"s")+"\n");
                                break;
                            case SUPPORT:
                                stringBuilder.append(
                                        "    [color=#00aaff]ChannelID[/color] • "+(((SupportFeature)feature).getChannelId() == -1 ? "/" : ((SupportFeature)feature).getChannelId())+"\n"+
                                        "    [color=#00aaff]ChannelNameOpen[/color] • "+(((SupportFeature)feature).getChannelNameOpen() == null ? "/" : ((SupportFeature)feature).getChannelNameOpen())+"\n"+
                                        "    [color=#00aaff]ChannelNameClose[/color] • "+(((SupportFeature)feature).getChannelNameClose() == null ? "/" : ((SupportFeature)feature).getChannelNameClose())+"\n"+
                                        "    [color=#00aaff]ChannelClientsOpen[/color] • "+(((SupportFeature)feature).getChannelClientsOpen() == -1 ? "/" : ((SupportFeature)feature).getChannelClientsOpen())+"\n"+
                                        "    [color=#00aaff]ChannelClientsClose[/color] • "+(((SupportFeature)feature).getChannelClientsClose() == -1 ? "/" : ((SupportFeature)feature).getChannelClientsClose())+"\n"+
                                        "    [color=#00aaff]NotifyGroupID[/color] • "+(((SupportFeature)feature).getNotifyGroupId() == -1 ? "/" : ((SupportFeature)feature).getNotifyGroupId())+"\n"+
                                        "    [color=#00aaff]Automation[/color] • "+(((SupportFeature)feature).isAutomated() ? "[color=green]✔" : "[color=red]✖")+"[/color]\n"+
                                        "    [color=#00aaff]Open[/color] • "+(((SupportFeature)feature).isOpen() ? "[color=green]✔" : "[color=red]✖")+"[/color]\n");
                                break;
                            case MODERATION:
                                stringBuilder.append(
                                        "    [color=#00aaff]NameCheck[/color] • "+(((ModerationFeature)feature).isNameCheck() ? "[color=green]✔" : "[color=red]✖")+"[/color]\n"+
                                        "    [color=#00aaff]ForbiddenNicknames[/color] • "+((ModerationFeature)feature).getForbiddenNicknames().size()+" • [i]<list; add; remove> [<nickname>][/i]\n"+
                                        "    [color=#00aaff]SwitchCheck[/color] • "+(((ModerationFeature)feature).isSwitchCheck() ? "[color=green]✔" : "[color=red]✖")+"[/color]\n"+
                                        "    [color=#00aaff]SwitchComplainsLimit[/color] • "+(((ModerationFeature)feature).getSwitchComplainsLimit() == -1 ? "/" : ((ModerationFeature)feature).getSwitchComplainsLimit())+"\n"+
                                        "    [color=#00aaff]SwitchDelay[/color] • "+(((ModerationFeature)feature).getSwitchDelay() == -1 ? "/" : ((ModerationFeature)feature).getSwitchDelay()/1000+"s")+"\n" +
                                        "    [color=#00aaff]IgnoredGroups[/color] • "+((ModerationFeature)feature).getIgnoredGroups().size()+" • [i]<list; add; remove> [<groupId>][/i]\n");
                                break;
                            case PUNISH:
                                stringBuilder.append(
                                        "    [color=#00aaff]ComplainsLimit[/color] • "+(((PunishFeature)feature).getComplainsLimit() == -1 ? "/" : ((PunishFeature)feature).getComplainsLimit())+"\n"+
                                        "    [color=#00aaff]BanDuration[/color] • "+(((PunishFeature)feature).getBanDuration() == -1 ? "/" : ((PunishFeature)feature).getBanDuration()+"s")+"\n");
                                break;
                        }
                        stringBuilder.append("\n»--------------------=====[color=#00aaff][[/color][b]» "+featureType.getName()+" «[/b][color=#00aaff]][/color]=====--------------------«");
                        api.sendPrivateMessage(client.getId(), stringBuilder.toString());
                        return;
                    }
                    FeatureType featureType = FeatureType.getFeature(args[0]);
                    if(featureType == null) {
                        logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                        api.sendPrivateMessage(client.getId(), syntaxMessage.replace("%REPLACE%", "!feature"));
                        return;
                    }
                    Feature feature = ByteSystem.getByteFeatureManager().getFeature(featureType);
                    if(featureType == FeatureType.CHANNEL) {
                        if(args[1].equalsIgnoreCase("status")) {
                            if(!((ChannelFeature)feature).isConfigured()) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Channel-Feature[/color] is not configured");
                                return;
                            }
                            if(args[2].equals("0")) {
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                feature.setEnabled(false);
                                api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Channel-Feature[/color] has been disabled");
                                return;
                            } else if(args[2].equals("1")) {
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                feature.setEnabled(true);
                                api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Channel-Feature[/color] has been enabled");
                                return;
                            }
                            logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                            api.sendPrivateMessage(client.getId(), "    » The [color=red]Status[/color] can be set using 1 or 0");
                            return;
                        } else if(args[1].equalsIgnoreCase("channelid")) {
                            int channelId;
                            try {
                                channelId = Integer.parseInt(args[2]);
                            } catch (NumberFormatException ex) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Channel ID[/color] expects numbers");
                                return;
                            }
                            if(api.getChannels().stream().noneMatch(ch -> ch.getId() == channelId)) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » This [color=red]Channel ID[/color] was not found");
                                return;
                            }
                            if(((SupportFeature)ByteSystem.getByteFeatureManager().getFeature(FeatureType.SUPPORT)).getChannelId() == channelId) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » This [color=red]Channel ID[/color] in the [color=red]Support-Feature[/color] is already registered");
                                return;
                            }
                            logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                            ((ChannelFeature)feature).setChannelId(channelId);
                            api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Channel ID[/color] has been set to [color=#00aaff]"+channelId+"[/color]");
                            return;
                        } else if(args[1].equalsIgnoreCase("channelname")) {
                            message = message.replace(args[0]+" "+args[1], "").trim();
                            logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                            ((ChannelFeature)feature).setChannelName(message);
                            api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Channel Name[/color] has been set to '[color=#00aaff]"+message+"[/color]'");
                            return;
                        } else if(args[1].equalsIgnoreCase("channelgroupid")) {
                            int groupId;
                            try {
                                groupId = Integer.parseInt(args[2]);
                            } catch (NumberFormatException ex) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Channel Group ID[/color] expects numbers");
                                return;
                            }
                            if(api.getChannelGroups().stream().noneMatch(ch -> ch.getId() == groupId)) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Channel Group ID[/color] was not found");
                                return;
                            }
                            logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                            ((ChannelFeature)feature).setChannelGroupId(groupId);
                            api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Channel Group ID[/color] has been set to [color=#00aaff]"+groupId+"[/color]");
                            return;
                        } else if(args[1].equalsIgnoreCase("delay")) {
                            long delay;
                            try {
                                delay = Long.parseLong(args[2]);
                            } catch (NumberFormatException ex) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Delay[/color] expects numbers");
                                return;
                            }
                            if(delay < 0) delay = 0;
                            delay = delay*1000;
                            logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                            ((ChannelFeature)feature).setDelay(delay);
                            api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Delay[/color] has been set to [color=#00aaff]"+(delay/1000)+"s[/color]");
                            return;
                        }
                    } else if(featureType == FeatureType.SUPPORT) {
                        if(args[1].equalsIgnoreCase("status")) {
                            if(!((SupportFeature)feature).isConfigured()) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Support-Feature[/color] ist not configured");
                                return;
                            }
                            if(args[2].equals("0")) {
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                feature.setEnabled(false);
                                api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Support-Feature[/color] has been disabled");
                                return;
                            } else if(args[2].equals("1")) {
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                feature.setEnabled(true);
                                api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Support-Feature[/color] has been enabled");
                                return;
                            }
                            logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                            api.sendPrivateMessage(client.getId(), "    » The [color=red]Status[/color] can be set to 0 or 1");
                            return;
                        } else if(args[1].equalsIgnoreCase("channelid")) {
                            int channelId;
                            try {
                                channelId = Integer.parseInt(args[2]);
                            } catch (NumberFormatException ex) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Channel ID[/color] expects numbers");
                                return;
                            }
                            if(api.getChannels().stream().noneMatch(ch -> ch.getId() == channelId)) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » This [color=red]Channel ID[/color] was not found");
                                return;
                            }
                            if(((ChannelFeature)ByteSystem.getByteFeatureManager().getFeature(FeatureType.CHANNEL)).getChannelId() == channelId) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » This [color=red]Channel ID[/color] in the [color=red]Channel-Feature[/color] is already registered");
                                return;
                            }
                            logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                            ((SupportFeature)feature).setChannelId(channelId);
                            api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Channel ID[/color] has been set to [color=#00aaff]"+channelId+"[/color]");
                            return;
                        } else if(args[1].equalsIgnoreCase("channelnameopen")) {
                            message = message.replace(args[0]+" "+args[1], "").trim();
                            logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                            ((SupportFeature)feature).setChannelNameOpen(message);
                            api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Channel Name Open[/color] has been set to '[color=#00aaff]"+message+"[/color]'");
                            return;
                        } else if(args[1].equalsIgnoreCase("channelnameclose")) {
                            message = message.replace(args[0]+" "+args[1], "").trim();
                            logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                            ((SupportFeature)feature).setChannelNameClose(message);
                            api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Channel Name Closed[/color] has been set to '[color=#00aaff]"+message+"[/color]'");
                            return;
                        } else if(args[1].equalsIgnoreCase("channelclientsopen")) {
                            int clients;
                            try {
                                clients = Integer.parseInt(args[2]);
                            } catch (NumberFormatException ex) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Channel Clients[/color] expect numbers");
                                return;
                            }
                            if(clients < 0) clients = 0;
                            logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                            ((SupportFeature)feature).setChannelClientsOpen(clients);
                            api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Channel Clients Open[/color] have been set to [color=#00aaff]"+clients+"[/color]");
                            return;
                        } else if(args[1].equalsIgnoreCase("channelclientsclose")) {
                            int clients;
                            try {
                                clients = Integer.parseInt(args[2]);
                            } catch (NumberFormatException ex) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Channel Clients[/color] expect numbers");
                                return;
                            }
                            if(clients < 0) clients = 0;
                            logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                            ((SupportFeature)feature).setChannelClientsClose(clients);
                            api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Channel Clients Closed[/color] have been set to [color=#00aaff]"+clients+"[/color]");
                            return;
                        } else if(args[1].equalsIgnoreCase("notifygroupid")) {
                            int groupId;
                            try {
                                groupId = Integer.parseInt(args[2]);
                            } catch (NumberFormatException ex) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Notify Group ID[/color] expects numbers");
                                return;
                            }
                            if(api.getServerGroups().stream().noneMatch(ch -> ch.getId() == groupId)) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » This [color=red]Server Group ID[/color] was not found");
                                return;
                            }
                            logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                            ((SupportFeature)feature).setNotifyGroupId(groupId);
                            api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Notify Group ID[/color] has been set to [color=#00aaff]"+groupId+"[/color]");
                            return;
                        } else if(args[1].equalsIgnoreCase("automation")) {
                            if(!((SupportFeature)feature).isConfigured()) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Support-Feature[/color] is not configured");
                                return;
                            }
                            if(args[2].equals("0")) {
                                if(!((SupportFeature)feature).isAutomated()) {
                                    logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                    api.sendPrivateMessage(client.getId(), "    » The [color=red]Support-Automation[/color] is already disabled");
                                    return;
                                }
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                ((SupportFeature)feature).setAutomated(false);
                                api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Support[/color] is not automated anymore");
                                return;
                            } else if(args[2].equals("1")) {
                                if(((SupportFeature)feature).isAutomated()) {
                                    logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                    api.sendPrivateMessage(client.getId(), "    » The [color=red]Support-Automation[/color] is already enabled");
                                    return;
                                }
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                ((SupportFeature)feature).setAutomated(true);
                                api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Support[/color] is now automated");
                                return;
                            }
                            logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                            api.sendPrivateMessage(client.getId(), "    » The [color=red]Support-Automation[/color] can be configured with 0 or 1");
                            return;
                        } else if(args[1].equalsIgnoreCase("open")) {
                            if(!((SupportFeature)feature).isConfigured()) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Support-Feature[/color] is not configured");
                                return;
                            }
                            if(((SupportFeature)feature).isAutomated()) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Support-Feature[/color] is currently automated");
                                return;
                            }
                            if(args[2].equals("0")) {
                                if(!((SupportFeature)feature).isOpen()) {
                                    logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                    api.sendPrivateMessage(client.getId(), "    » The [color=red]Support[/color] is already closed");
                                    return;
                                }
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                ((SupportFeature)feature).setOpen(false);
                                api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Support[/color] has been closed");
                                return;
                            } else if(args[2].equals("1")) {
                                if(((SupportFeature)feature).isOpen()) {
                                    logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                    api.sendPrivateMessage(client.getId(), "    » The [color=red]Support[/color] is already opened");
                                    return;
                                }
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                ((SupportFeature)feature).setOpen(true);
                                api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Support[/color] has been opened");
                                return;
                            }
                            logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                            api.sendPrivateMessage(client.getId(), "    » The [color=red]Support[/color] can be opened or closed with 0 or 1");
                            return;
                        }
                    } else if(featureType == FeatureType.MODERATION) {
                        if(args[1].equalsIgnoreCase("status")) {
                            if(!((ModerationFeature)feature).isConfigured()) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Moderation-Feature[/color] is not configured");
                                return;
                            }
                            if(args[2].equals("0")) {
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                feature.setEnabled(false);
                                api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Moderation-Feature[/color] has been disabled");
                                return;
                            } else if(args[2].equals("1")) {
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                feature.setEnabled(true);
                                api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Moderation-Feature[/color] has been enabled");
                                return;
                            }
                            logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                            api.sendPrivateMessage(client.getId(), "    » The [color=red]Status[/color] can be set to 0 or 1");
                            return;
                        } else if(args[1].equalsIgnoreCase("namecheck")) {
                            if(args[2].equals("0")) {
                                if(!((ModerationFeature)feature).isNameCheck()) {
                                    logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                    api.sendPrivateMessage(client.getId(), "    » The [color=red]NameCheck[/color] is already disabled");
                                    return;
                                }
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                ((ModerationFeature)feature).setNameCheck(false);
                                api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]NameCheck[/color] has been disabled");
                                return;
                            } else if(args[2].equals("1")) {
                                if(((ModerationFeature)feature).isNameCheck()) {
                                    logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                    api.sendPrivateMessage(client.getId(), "    » The [color=red]NameCheck[/color] is already enabled");
                                    return;
                                }
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                ((ModerationFeature)feature).setNameCheck(true);
                                api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]NameCheck[/color] has been enabled");
                                return;
                            }
                            logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                            api.sendPrivateMessage(client.getId(), "    » The [color=red]NameCheck[/color] can be set to 0 or 1");
                            return;
                        } else if(args[1].equalsIgnoreCase("switchcheck")) {
                            if(args[2].equals("0")) {
                                if(!((ModerationFeature)feature).isSwitchCheck()) {
                                    logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                    api.sendPrivateMessage(client.getId(), "    » The [color=red]SwitchCheck[/color] is already disabled");
                                    return;
                                }
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                ((ModerationFeature)feature).setSwitchCheck(false);
                                api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]SwitchCheck[/color] has been disabled");
                                return;
                            } else if(args[2].equals("1")) {
                                if(((ModerationFeature)feature).isSwitchCheck()) {
                                    logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                    api.sendPrivateMessage(client.getId(), "    » The [color=red]SwitchCheck[/color] is already enabled");
                                    return;
                                }
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                ((ModerationFeature)feature).setSwitchCheck(true);
                                api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]SwitchCheck[/color] has been enabled");
                                return;
                            }
                            logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                            api.sendPrivateMessage(client.getId(), "    » The [color=red]SwitchCheck[/color] can be set to 0 or 1");
                            return;
                        } else if(args[1].equalsIgnoreCase("switchcomplainslimit")) {
                            int complainsLimit;
                            try {
                                complainsLimit = Integer.parseInt(args[2]);
                            } catch (NumberFormatException ex) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Switch Complains Limit[/color] expects numbers");
                                return;
                            }
                            if(complainsLimit <= 0) complainsLimit = 1;
                            logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                            ((ModerationFeature)feature).setSwitchComplainsLimit(complainsLimit);
                            api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Switch Complains Limit[/color] has been set to [color=#00aaff]"+complainsLimit+"[/color]");
                            return;
                        } else if(args[1].equalsIgnoreCase("switchdelay")) {
                            long delay;
                            try {
                                delay = Long.parseLong(args[2]);
                            } catch (NumberFormatException ex) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Switch Delay[/color] expects numbers");
                                return;
                            }
                            if(delay < 0) delay = 0;
                            delay = delay*1000;
                            logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                            ((ModerationFeature)feature).setSwitchDelay(delay);
                            api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Switch Delay[/color] has been set to [color=#00aaff]"+(delay/1000)+"s[/color]");
                            return;
                        } else if(args[1].equalsIgnoreCase("ignoredgroups")) {
                            args = getArgs(message, 4);
                            if(args == null) {
                                args = getArgs(message, 3);
                                if(args[2].equalsIgnoreCase("list")) {
                                    logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                    if (((ModerationFeature) feature).getIgnoredGroups().size() == 0) {
                                        api.sendPrivateMessage(client.getId(), "    » There are no ignored [color=red]Server Groups[/color] registered");
                                        return;
                                    }
                                    StringBuilder builder = new StringBuilder("\n\n\n»-----------------=====[color=#00aaff][[/color][b]» IgnoredGroups «[/b][color=#00aaff]][/color]=====-----------------«\n\n");
                                    for(Integer current : ((ModerationFeature)feature).getIgnoredGroups()) {
                                        if(api.getServerGroups().stream().noneMatch(group -> group.getId() == current)) {
                                            ((ModerationFeature)feature).removeIgnoredGroup(current);
                                            continue;
                                        }
                                        ServerGroup serverGroup = api.getServerGroups().stream().filter(group -> group.getId() == current).findFirst().orElse(null);
                                        builder.append("    [color=#00aaff]" + serverGroup.getName() + "[/color] • [i]" + current + "[/i]\n");
                                    }
                                    builder.append("\n»-----------------=====[color=#00aaff][[/color][b]» IgnoredGroups «[/b][color=#00aaff]][/color]=====-----------------«");
                                    api.sendPrivateMessage(client.getId(), builder.toString());
                                    return;
                                }
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), syntaxMessage.replace("%REPLACE%", "!feature"));
                                return;
                            }
                            if(args[2].equalsIgnoreCase("add")) {
                                int groupId;
                                try {
                                    groupId = Integer.parseInt(args[3]);
                                } catch (NumberFormatException ex) {
                                    logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                    api.sendPrivateMessage(client.getId(), "    » The [color=red]Group ID[/color] expects numbers");
                                    return;
                                }
                                if(api.getServerGroups().stream().noneMatch(group -> group.getId() == groupId)) {
                                    logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                    api.sendPrivateMessage(client.getId(), "    » This [color=red]Group ID[/color] was not found");
                                    return;
                                }
                                if(((ModerationFeature)feature).getIgnoredGroups().contains(groupId)) {
                                    logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                    api.sendPrivateMessage(client.getId(), "    » This [color=red]Group ID[/color] is already ignored");
                                    return;
                                }
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                ((ModerationFeature)feature).addIgnoredGroup(groupId);
                                api.sendPrivateMessage(client.getId(), "    » The Servergroup [color=#00aaff]"+api.getServerGroups().stream().filter(group -> group.getId() == groupId).findFirst().orElse(null).getName()+"[/color]" +
                                        " is now ignored");
                                return;
                            } else if(args[2].equalsIgnoreCase("remove")) {
                                int groupId;
                                try {
                                    groupId = Integer.parseInt(args[3]);
                                } catch (NumberFormatException ex) {
                                    logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                    api.sendPrivateMessage(client.getId(), "    » The [color=red]Group ID[/color] expects numbers");
                                    return;
                                }
                                if(!((ModerationFeature)feature).getIgnoredGroups().contains(groupId)) {
                                    logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                    api.sendPrivateMessage(client.getId(), "    » This [color=red]Group ID[/color] is not ignored");
                                    return;
                                }
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                ((ModerationFeature)feature).removeIgnoredGroup(groupId);
                                ServerGroup serverGroup = api.getServerGroups().stream().filter(group -> group.getId() == groupId).findFirst().orElse(null);
                                if(serverGroup != null)
                                    api.sendPrivateMessage(client.getId(), "    » The Servergroup [color=#00aaff]"+serverGroup.getName()+"[/color] is not ignored anymore");
                                else
                                    api.sendPrivateMessage(client.getId(), "    » The Group ID [color=#00aaff]"+groupId+"[/color] is not ignored anymore");
                                return;
                            }
                        } else if(args[1].equalsIgnoreCase("forbiddennicknames")) {
                            args = getArgs(message, 4);
                            if(args == null) {
                                args = getArgs(message, 3);
                                if(args[2].equalsIgnoreCase("list")) {
                                    logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                    if (((ModerationFeature) feature).getForbiddenNicknames().size() == 0) {
                                        api.sendPrivateMessage(client.getId(), "    » There are no forbidden [color=red]Nicknames[/color] registered");
                                        return;
                                    }
                                    StringBuilder builder = new StringBuilder("\n\n\n»-----------------=====[color=#00aaff][[/color][b]» ForbiddenNicknames «[/b][color=#00aaff]][/color]=====-----------------«\n\n");
                                    for(String current : ((ModerationFeature)feature).getForbiddenNicknames())
                                        builder.append("    [color=#00aaff]" + current + "[/color]\n");
                                    builder.append("\n»-----------------=====[color=#00aaff][[/color][b]» ForbiddenNicknames «[/b][color=#00aaff]][/color]=====-----------------«");
                                    api.sendPrivateMessage(client.getId(), builder.toString());
                                    return;
                                }
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), syntaxMessage.replace("%REPLACE%", "!feature"));
                                return;
                            }
                            if(args[2].equalsIgnoreCase("add")) {
                                String nickname = message;
                                for(int i = 0; i < args.length-1; i++)
                                    nickname = nickname.replace(args[i]+" ", "").trim();
                                nickname = nickname.toLowerCase();
                                if(((ModerationFeature)feature).getForbiddenNicknames().contains(nickname)) {
                                    logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                    api.sendPrivateMessage(client.getId(), "    » This [color=red]Nickname[/color] is already forbidden");
                                    return;
                                }
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                ((ModerationFeature)feature).addForbiddenNickname(nickname);
                                api.sendPrivateMessage(client.getId(), "    » The Nickname [color=#00aaff]"+nickname+"[/color] is forbidden now");
                                return;
                            } else if(args[2].equalsIgnoreCase("remove")) {
                                String nickname = message;
                                for(int i = 0; i < args.length-1; i++)
                                    nickname = nickname.replace(args[i]+" ", "").trim();
                                nickname = nickname.toLowerCase();
                                if(!((ModerationFeature)feature).getForbiddenNicknames().contains(nickname)) {
                                    logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                    api.sendPrivateMessage(client.getId(), "    » This [color=red]Nickname[/color] is not forbidden");
                                    return;
                                }
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                ((ModerationFeature)feature).removeForbiddenNickname(nickname);
                                api.sendPrivateMessage(client.getId(), "    » The Nickname [color=#00aaff]"+nickname+"[/color] is not forbidden anymore");
                                return;
                            }

                        }
                    } else if(featureType == FeatureType.PUNISH) {
                        if(args[1].equalsIgnoreCase("status")) {
                            if(!((PunishFeature)feature).isConfigured()) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Punish-Feature[/color] is not configured");
                                return;
                            }
                            if(args[2].equals("0")) {
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                feature.setEnabled(false);
                                api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Punish-Feature[/color] has been disabled");
                                return;
                            } else if(args[2].equals("1")) {
                                logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                                feature.setEnabled(true);
                                api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Punish-Feature[/color] has been enabled");
                                return;
                            }
                            logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                            api.sendPrivateMessage(client.getId(), "    » The [color=red]Status[/color] can be set to 0 or 1");
                            return;
                        } else if(args[1].equalsIgnoreCase("complainslimit")) {
                            int complainsLimit;
                            try {
                                complainsLimit = Integer.parseInt(args[2]);
                            } catch (NumberFormatException ex) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Complains Limit[/color] expects numbers");
                                return;
                            }
                            if(complainsLimit <= 0) complainsLimit = 1;
                            logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                            ((PunishFeature)feature).setComplainsLimit(complainsLimit);
                            api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Complains Limit[/color] has been set to [color=#00aaff]"+complainsLimit+"[/color]");
                            return;
                        } else if(args[1].equalsIgnoreCase("banduration")) {
                            long duration;
                            try {
                                duration = Long.parseLong(args[2]);
                            } catch (NumberFormatException ex) {
                                logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                                api.sendPrivateMessage(client.getId(), "    » The [color=red]Ban Duration[/color] expects numbers");
                                return;
                            }
                            if(duration <= 0) duration = 1;
                            logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                            ((PunishFeature)feature).setBanDuration(duration);
                            api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Ban Duration[/color] has been set to [color=#00aaff]"+duration+"s[/color]");
                            return;
                        }
                    }
                    logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                    api.sendPrivateMessage(client.getId(), syntaxMessage.replace("%REPLACE%", "!feature"));
                    return;
                }

                if(message.equalsIgnoreCase("!channel")) {
                    if(!byteClient.isAdmin()) {
                        logCommand(client, e.getMessage().trim(), LogCommand.DISALLOWED);
                        api.sendPrivateMessage(client.getId(), permissionMessage);
                        return;
                    }
                    logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                    api.sendPrivateMessage(client.getId(),
                            "\n\n\n»--------------------=====[color=#00aaff][[/color][b]» Channel «[/b][color=#00aaff]][/color]=====--------------------«\n\n"+
                                    "    [color=#00aaff]!channel me[/color] • Show your current channel\n" +
                                    "    [color=#00aaff]!channel list[/color] • Show all channels\n" +
                                    "    [color=#00aaff]!channel <ID>[/color] • Configure the query channel\n"
                                    +"\n»--------------------=====[color=#00aaff][[/color][b]» Channel «[/b][color=#00aaff]][/color]=====--------------------«");
                    return;
                }

                if(message.startsWith("!channel ")) {
                    if(!byteClient.isAdmin()) {
                        logCommand(client, e.getMessage().trim(), LogCommand.DISALLOWED);
                        api.sendPrivateMessage(client.getId(), permissionMessage);
                        return;
                    }
                    message = message.replace("!channel ", "").trim();
                    if(message.length() == 0) {
                        logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                        api.sendPrivateMessage(client.getId(), syntaxMessage.replace("%REPLACE%", "!channel"));
                        return;
                    }
                    if(message.equalsIgnoreCase("list")) {
                        List<Channel> channels = api.getChannels();
                        //channels.sort(Comparator.comparing(Channel::getId));
                        StringBuilder builder = new StringBuilder("\n\n\n»-------------------=====[color=#00aaff][[/color][b]» Channel «[/b][color=#00aaff]][/color]=====-------------------«\n\n");
                        for(Channel channel : channels)
                            builder.append("    "+(String.valueOf(channel.getId()).length() == 1 ? " " : "")+"[color=#00aaff]"+channel.getId()+"[/color] • "+channel.getName()+"\n");
                        builder.append("\n»-------------------=====[color=#00aaff][[/color][b]» Channel «[/b][color=#00aaff]][/color]=====-------------------«");
                        logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                        api.sendPrivateMessage(client.getId(), builder.toString());
                        return;
                    } else if(message.equalsIgnoreCase("me")) {
                        logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                        api.sendPrivateMessage(client.getId(), "    » Your current Channel ID • [color=#00aaff]"+client.getChannelId()+"[/color]");
                        return;
                    } else {
                        int channelId;
                        try {
                            channelId = Integer.parseInt(message);
                        } catch (NumberFormatException ex) {
                            logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                            api.sendPrivateMessage(client.getId(), "    » The [color=red]Channel ID[/color] expects numbers");
                            return;
                        }
                        if (api.getChannels().stream().noneMatch(ch -> ch.getId() == channelId)) {
                            logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                            api.sendPrivateMessage(client.getId(), "    » This [color=red]Channel ID[/color] was not found");
                            return;
                        }
                        if (api.getClientByNameExact(ByteSystem.getByteCoreManager().getByteCoreNickname(ByteSystem.getBotId()), false).getChannelId() == channelId) {
                            logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                            api.sendPrivateMessage(client.getId(), "    » The [color=red]Query[/color] is already in this channel");
                            return;
                        }
                        logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                        ByteSystem.getByteCoreManager().setByteCoreChannel(ByteSystem.getBotId(), channelId);
                        api.sendPrivateMessage(client.getId(), "    » The [color=#00aaff]Query-Channel[/color] has been set to [color=#00aaff]" + channelId + "[/color]");
                        api.moveQuery(channelId);
                        return;
                    }
                }

                if(message.startsWith("!check ")) {
                    if(!byteClient.isAdmin()) {
                        logCommand(client, e.getMessage().trim(), LogCommand.DISALLOWED);
                        api.sendPrivateMessage(client.getId(), permissionMessage);
                        return;
                    }
                    ByteClientManager byteClientManager = ByteSystem.getByteClientManager();
                    message = message.replace("!check ", "").trim();
                    String[] args = getArgs(message, 1);
                    if(args == null) {
                        logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                        api.sendPrivateMessage(client.getId(), syntaxMessage.replace("%REPLACE%", "!help"));
                        return;
                    }
                    List<OfflineByteClient> offlineByteClients = null;
                    OfflineByteClient offlineByteClient = null;
                    if(byteClientManager.byteClientExistsByUniqueId(args[0])) offlineByteClient = byteClientManager.getOfflineByteClientByUniqueId(args[0]);
                    else if(byteClientManager.byteClientExistsByNickname(args[0])) offlineByteClients = byteClientManager.getOfflineByteClientByNickname(args[0]);
                    if(offlineByteClient == null && (offlineByteClients == null || offlineByteClients.size() == 0)) {
                        logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                        api.sendPrivateMessage(client.getId(), userMessage);
                        return;
                    }
                    if(offlineByteClient == null && offlineByteClients.size() == 1)
                        offlineByteClient = offlineByteClients.get(0);
                    else if(offlineByteClients != null) {
                        logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                        StringBuilder builder = new StringBuilder("\n\n\n»-------------------=====[color=#00aaff][[/color][b]» User «[/b][color=#00aaff]][/color]=====-------------------«\n\n");
                        builder.append("    » Nickname registered multiple times. Use the Unique ID\n\n");
                        for(OfflineByteClient current : offlineByteClients)
                            builder.append("    [color=#00aaff]"+current.getNickname()+"[/color] • [i]"+current.getUniqueId()+"[/i]\n");
                        builder.append("\n»-------------------=====[color=#00aaff][[/color][b]» User «[/b][color=#00aaff]][/color]=====-------------------«");
                        api.sendPrivateMessage(client.getId(), builder.toString());
                        return;
                    }
                    logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                    if(offlineByteClient.isOnline()) {
                        ByteClient targetByteClient = offlineByteClient.getByteClient();
                        api.sendPrivateMessage(client.getId(),
                                "\n\n\n»-------------------=====[color=#00aaff][[/color][b]» User «[/b][color=#00aaff]][/color]=====-------------------«\n\n" +
                                        "    [color=#00aaff]Nickname[/color] • "+targetByteClient.getNickname()+"\n" +
                                        "    [color=#00aaff]Unique ID[/color] • "+targetByteClient.getUniqueId()+"\n" +
                                        "    [color=#00aaff]IP Address[/color] • "+targetByteClient.getIpAddress()+"\n" +
                                        "    [color=#00aaff]Teamspeak ID[/color] • "+targetByteClient.getDatabaseId()+"\n" +
                                        "    [color=#00aaff]Location[/color] • "+targetByteClient.getCountry()+"\n" +
                                        "    [color=#00aaff]OS[/color] • "+targetByteClient.getOS()+"\n" +
                                        "    [color=#00aaff]Version[/color] • "+targetByteClient.getVersion()+"\n" +
                                        "    [color=#00aaff]Description[/color] • "+targetByteClient.getDescription()+"\n" +
                                        "    [color=#00aaff]Status[/color] • [color=green]Online[/color]\n" +
                                        "    [color=#00aaff]First Connection[/color] • "+targetByteClient.getFirstConnection()+"\n" +
                                        "    [color=#00aaff]Total Connections[/color] • "+targetByteClient.getTotalConnections()+"\n" +
                                        "    [color=#00aaff]Onlinetime[/color] • "+targetByteClient.getOnlinetime().getTotalOnlinetime()+"\n" +
                                        "    [color=#00aaff]Admin[/color] • "+(targetByteClient.isAdmin() ? "[color=green]✔" : "[color=red]✖")+"[/color]\n" +
                                        "\n»-------------------=====[color=#00aaff][[/color][b]» User «[/b][color=#00aaff]][/color]=====-------------------«");
                        return;
                    }
                    api.sendPrivateMessage(client.getId(),
                            "\n\n\n»-------------------=====[color=#00aaff][[/color][b]» User «[/b][color=#00aaff]][/color]=====-------------------«\n\n" +
                                    "    [color=#00aaff]Nickname[/color] • "+offlineByteClient.getNickname()+"\n" +
                                    "    [color=#00aaff]Unique ID[/color] • "+offlineByteClient.getUniqueId()+"\n" +
                                    "    [color=#00aaff]IP Address[/color] • "+offlineByteClient.getIpAddress()+"\n" +
                                    "    [color=#00aaff]Teamspeak ID[/color] • "+offlineByteClient.getDatabaseId()+"\n" +
                                    "    [color=#00aaff]Location[/color] • "+offlineByteClient.getCountry()+"\n" +
                                    "    [color=#00aaff]OS[/color] • "+offlineByteClient.getOS()+"\n" +
                                    "    [color=#00aaff]Version[/color] • "+offlineByteClient.getVersion()+"\n" +
                                    "    [color=#00aaff]Description[/color] • "+offlineByteClient.getDescription()+"\n" +
                                    "    [color=#00aaff]Last Connection[/color] • "+offlineByteClient.getLastConnection()+"\n" +
                                    "    [color=#00aaff]First Connection[/color] • "+offlineByteClient.getFirstConnection()+"\n" +
                                    "    [color=#00aaff]Total Connections[/color] • "+offlineByteClient.getTotalConnections()+"\n" +
                                    "    [color=#00aaff]Onlinetime[/color] • "+offlineByteClient.getOnlineTime()+"\n" +
                                    "    [color=#00aaff]Admin[/color] • "+(offlineByteClient.isAdmin() ? "[color=green]✔" : "[color=red]✖")+"[/color]\n" +
                                    "\n»-------------------=====[color=#00aaff][[/color][b]» User «[/b][color=#00aaff]][/color]=====-------------------«");
                    return;
                }

                if(message.equalsIgnoreCase("!onlinetime")) {
                    logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                    ByteClientManager byteClientManager = ByteSystem.getByteClientManager();
                    List<OfflineByteClient> topPlayers = byteClientManager.getTopOnlinetimeByteClients(5);
                    StringBuilder builder = new StringBuilder("\n\n\n»-----------------=====[color=#00aaff][[/color][b]» Onlinetime «[/b][color=#00aaff]][/color]=====-----------------«\n\n");
                    for(OfflineByteClient current : topPlayers) {
                        if(current.isOnline()) {
                            builder.append("    [color=#00aaff]" + current.getByteClient().getNickname() + "[/color] • " + current.getByteClient().getOnlinetime().getTotalOnlinetime() + "\n");
                            continue;
                        }
                        builder.append("    [color=#00aaff]" + current.getNickname() + "[/color] • " + current.getOnlineTime() + "\n");
                    }
                    builder.append("\n»-----------------=====[color=#00aaff][[/color][b]» Onlinetime «[/b][color=#00aaff]][/color]=====-----------------«");
                    api.sendPrivateMessage(client.getId(), builder.toString());
                    return;
                }
                
                if(message.equalsIgnoreCase("!admin")) {
                    if(!byteClient.isAdmin()) {
                        logCommand(client, e.getMessage().trim(), LogCommand.DISALLOWED);
                        api.sendPrivateMessage(client.getId(), permissionMessage);
                        return;
                    }
                    logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                    api.sendPrivateMessage(client.getId(),
                            messageHeader+
                            "    » Use [color=#00aaff]!admin add <Unique ID>[/color] to add an admin\n" +
                            "    » Use [color=#00aaff]!admin remove <Unique ID>[/color] to remove an admin\n" +
                            "    » Use [color=#00aaff]!admin list[/color] to show all admins\n"
                            +messageFooter);
                    return;
                }

                if(message.startsWith("!admin ")) {
                    if(!byteClient.isAdmin()) {
                        logCommand(client, e.getMessage().trim(), LogCommand.DISALLOWED);
                        api.sendPrivateMessage(client.getId(), permissionMessage);
                        return;
                    }
                    ByteClientManager byteClientManager = ByteSystem.getByteClientManager();
                    message = message.replace("!admin ", "").trim();
                    String[] args = getArgs(message, 2);
                    if(args == null) {
                        args = getArgs(message, 1);
                        if(args == null) {
                            logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                            api.sendPrivateMessage(client.getId(), syntaxMessage.replace("%REPLACE%", "!admin"));
                            return;
                        }
                        if(args[0].equalsIgnoreCase("list")) {
                            logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                            StringBuilder builder = new StringBuilder("\n\n\n»-----------------=====[color=#00aaff][[/color][b]» Admins «[/b][color=#00aaff]][/color]=====-----------------«\n\n");
                            for(OfflineByteClient current : byteClientManager.getAdminByteClients())
                                builder.append("    [color=#00aaff]"+current.getNickname()+"[/color] • [i]"+current.getUniqueId()+"[/i]\n");
                            builder.append("\n»-----------------=====[color=#00aaff][[/color][b]» Admins «[/b][color=#00aaff]][/color]=====-----------------«");
                            api.sendPrivateMessage(client.getId(), builder.toString());
                            return;
                        }
                        logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                        api.sendPrivateMessage(client.getId(), syntaxMessage.replace("%REPLACE%", "!admin"));
                        return;
                    }
                    if(args[0].equalsIgnoreCase("add")) {
                        if(!byteClientManager.byteClientExistsByUniqueId(args[1])) {
                            logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                            api.sendPrivateMessage(client.getId(), userIdMessage);
                            return;
                        }
                        OfflineByteClient offlineByteClient = byteClientManager.getOfflineByteClientByUniqueId(args[1]);
                        if(offlineByteClient.isAdmin()) {
                            logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                            api.sendPrivateMessage(client.getId(), "    » The user [color=red]"+offlineByteClient.getNickname()+"[/color] is already an admin");
                            return;
                        }
                        logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                        byteClientManager.setByteClientAdmin(offlineByteClient.getUniqueId(), true);
                        api.sendPrivateMessage(client.getId(), "    » The user [color=#00aaff]"+offlineByteClient.getNickname()+"[/color] is now an admin");
                        return;
                    } else if(args[0].equalsIgnoreCase("remove")) {
                        if(!byteClientManager.byteClientExistsByUniqueId(args[1])) {
                            logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                            api.sendPrivateMessage(client.getId(), userIdMessage);
                            return;
                        }
                        OfflineByteClient offlineByteClient = byteClientManager.getOfflineByteClientByUniqueId(args[1]);
                        if(!offlineByteClient.isAdmin()) {
                            logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                            api.sendPrivateMessage(client.getId(), "    » The user [color=red]"+offlineByteClient.getNickname()+"[/color] is not an admin");
                            return;
                        }
                        logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                        byteClientManager.setByteClientAdmin(offlineByteClient.getUniqueId(), false);
                        api.sendPrivateMessage(client.getId(), "    » The user [color=#00aaff]"+offlineByteClient.getNickname()+"[/color] is not an admin anymore");
                        return;
                    }
                    api.sendPrivateMessage(client.getId(), syntaxMessage.replace("%REPLACE%", "!admin"));
                    return;
                }

                if(message.startsWith("!msg ")) {
                    if(!byteClient.isAdmin()) {
                        logCommand(client, e.getMessage().trim(), LogCommand.DISALLOWED);
                        api.sendPrivateMessage(client.getId(), permissionMessage);
                        return;
                    }
                    message = message.replace("!msg ", "").trim();
                    String[] args = getArgs(message, 2);
                    if(args == null) {
                        logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                        api.sendPrivateMessage(client.getId(), syntaxMessage.replace("%REPLACE%", "!help"));
                        return;
                    }
                    ByteClientManager byteClientManager = ByteSystem.getByteClientManager();
                    if(!byteClientManager.isByteClientOnline(args[0])) {
                        logCommand(client, e.getMessage().trim(), LogCommand.UNKNOWN);
                        api.sendPrivateMessage(client.getId(), userMessage);
                        return;
                    }
                    logCommand(client, e.getMessage().trim(), LogCommand.ALLOWED);
                    ByteClient targetByteClient = byteClientManager.getByteClientByNickname(args[0]);
                    message = message.substring(message.indexOf(args[0])+args[0].length());
                    api.sendPrivateMessage(targetByteClient.getClientInfo().getId(), "    » "+message);
                    api.sendPrivateMessage(client.getId(), "    » User [color=#00aaff]"+targetByteClient.getNickname()+"[/color] has been notified");
                    return;
                }

                logMessage(e.getTargetMode(), client, e.getMessage().trim());
            }
        });
    }

    private void logMessage(TextMessageTargetMode targetMode, Client client, String message) {
        LogType logType = null;
        switch (targetMode) {
            case SERVER:
                logType = LogType.MESSAGE_SERVER;
                break;
            case CHANNEL:
                logType = LogType.MESSAGE_CHANNEL;
                break;
            case CLIENT:
                logType = LogType.MESSAGE_PRIVATE;
                break;
        }
        ByteSystem.getFileManager().logMessage(logType, client, message);
    }

    private void logCommand(Client client, String command, LogCommand logCommand) {
        ByteSystem.getFileManager().logCommand(client, command, logCommand);
    }

    private String[] getArgs(String message, int amount) {
        String[] args = new String[amount];
        if(message.length() == 0) return null;
        int count = 1;
        for(char current : message.toCharArray()) {
            if(current == ' ') count++;
        }
        if(count < amount) return null;

        for(int i = 0; i < amount; i++) {
            if(!message.contains(" "))
                args[i] = message;
            else
                args[i] = message.substring(0, message.indexOf(" "));
            message = message.replace(args[i]+" ", "");
        }
        return args;
    }

}
