// Created by Eric B. 21.10.2021 20:00
package de.ericzones.bytesystem.collectives.file;

import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.*;

public class FileManager {

    private final String configPath = "config.properties";
    private final Properties properties = new Properties();

    private final String messageLogPath = "logs"+File.separator+"message"+File.separator;
    private final String punishLogPath = "logs"+File.separator+"punish"+File.separator;
    private final String moveLogPath = "logs"+File.separator+"move"+File.separator;
    private final String channelLogPath = "logs"+File.separator+"channel"+File.separator;
    private final String logPath = "logs"+File.separator;
    private Logger messageLogger, punishLogger, moveLogger, channelLogger;
    private Logger logger;

    public FileManager() {
        readConfig();
        registerLogging();
    }

    public String getProperty(ConfigProperty configProperty) {
        return properties.getProperty(configProperty.toString());
    }

    private void registerLogging() {
        File file = new File(messageLogPath);
        file.mkdirs();
        file = new File(punishLogPath);
        file.mkdirs();
        file = new File(moveLogPath);
        file.mkdirs();
        file = new File(channelLogPath);
        file.mkdirs();

        FileHandler fileHandler;
        try {
            messageLogger = Logger.getLogger("MessageLogger");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd__HH-mm");
            Date date = new Date();
            fileHandler = new FileHandler(messageLogPath+"Message_"+dateFormat.format(date)+".log");
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    return timeFormat.format(record.getMillis())+" || "+record.getMessage()+"\n";
                }
            });
            messageLogger.addHandler(fileHandler);
            messageLogger.setUseParentHandlers(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            punishLogger = Logger.getLogger("PunishLogger");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd__HH-mm");
            Date date = new Date();
            fileHandler = new FileHandler(punishLogPath+"Punish_"+dateFormat.format(date)+".log");
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    return timeFormat.format(record.getMillis())+" || "+record.getMessage()+"\n";
                }
            });
            punishLogger.addHandler(fileHandler);
            punishLogger.setUseParentHandlers(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            moveLogger = Logger.getLogger("MoveLogger");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd__HH-mm");
            Date date = new Date();
            fileHandler = new FileHandler(moveLogPath+"Move_"+dateFormat.format(date)+".log");
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    return timeFormat.format(record.getMillis())+" || "+record.getMessage()+"\n";
                }
            });
            moveLogger.addHandler(fileHandler);
            moveLogger.setUseParentHandlers(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            channelLogger = Logger.getLogger("ChannelLogger");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd__HH-mm");
            Date date = new Date();
            fileHandler = new FileHandler(channelLogPath+"Channel_"+dateFormat.format(date)+".log");
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    return timeFormat.format(record.getMillis())+" || "+record.getMessage()+"\n";
                }
            });
            channelLogger.addHandler(fileHandler);
            channelLogger.setUseParentHandlers(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            logger = Logger.getLogger("Logger");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd__HH-mm");
            Date date = new Date();
            fileHandler = new FileHandler(logPath+dateFormat.format(date)+".log");
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    return timeFormat.format(record.getMillis())+" || "+record.getMessage()+"\n";
                }
            });
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void logCommand(Client client, String command, LogCommand logCommand) {
        messageLogger.log(Level.OFF, LogType.COMMAND.getText()+" || "+client.getNickname()+" || "+command+" || "+logCommand.getSymbol());
        logger.log(Level.OFF, LogType.COMMAND.getText()+" || "+client.getNickname()+" || "+command+" || "+logCommand.getSymbol());
    }

    public void logMessage(LogType logType, Client client, String message) {
        messageLogger.log(Level.OFF, logType.getText()+" || "+client.getNickname()+" || "+message);
        logger.log(Level.OFF, logType.getText()+" || "+client.getNickname()+" || "+message);
    }

    public void logChannelKick(Client client, Client targetClient, Channel channel, String reason) {
        punishLogger.log(Level.OFF, LogType.KICK_CHANNEL.getText()+" || "+client.getNickname()+" || "+targetClient.getNickname()+" > "+channel.getId()+(!reason.equals("") ? " - "+reason : ""));
        logger.log(Level.OFF, LogType.KICK_CHANNEL.getText()+" || "+client.getNickname()+" || "+targetClient.getNickname()+" > "+channel.getId()+(!reason.equals("") ? " - "+reason : ""));
    }

    public void logServerKick(Client client, String targetNickname, String reason) {
        punishLogger.log(Level.OFF, LogType.KICK_SERVER.getText()+" || "+client.getNickname()+" || "+targetNickname+(!reason.equals("") ? " > "+reason : ""));
        logger.log(Level.OFF, LogType.KICK_SERVER.getText()+" || "+client.getNickname()+" || "+targetNickname+(!reason.equals("") ? " > "+reason : ""));
    }

    public void logBan(Client client, String targetNickname, String duration, String reason) {
        punishLogger.log(Level.OFF, LogType.BAN.getText()+" || "+client.getNickname()+" || "+targetNickname+" > "+duration+(!reason.equals("") ? " - "+reason : ""));
        logger.log(Level.OFF, LogType.BAN.getText()+" || "+client.getNickname()+" || "+targetNickname+" > "+duration+(!reason.equals("") ? " - "+reason : ""));
    }

    public void logSwitch(Client client, Channel channel) {
        moveLogger.log(Level.OFF, LogType.SWITCH.getText()+" || "+client.getNickname()+" > "+channel.getId());
        logger.log(Level.OFF, LogType.SWITCH.getText()+" || "+client.getNickname()+" > "+channel.getId());
    }

    public void logMove(Client client, Client targetClient, Channel channel) {
        moveLogger.log(Level.OFF, LogType.MOVE.getText()+" || "+client.getNickname()+" || "+targetClient.getNickname()+" > "+channel.getId());
        logger.log(Level.OFF, LogType.MOVE.getText()+" || "+client.getNickname()+" || "+targetClient.getNickname()+" > "+channel.getId());
    }

    public void logConnect(Client client, Channel channel) {
        moveLogger.log(Level.OFF, LogType.CONNECT.getText()+" || "+client.getNickname()+" > "+channel.getId());
        logger.log(Level.OFF, LogType.CONNECT.getText()+" || "+client.getNickname()+" > "+channel.getId());
    }

    public void logDisconnect(String nickname, String message) {
        moveLogger.log(Level.OFF, LogType.DISCONNECT.getText()+" || "+nickname+(!message.equals("") ? " > "+message : ""));
        logger.log(Level.OFF, LogType.DISCONNECT.getText()+" || "+nickname+(!message.equals("") ? " > "+message : ""));
    }

    public void logChannelEdit(Client client, Channel channel) {
        channelLogger.log(Level.OFF, LogType.CHANNEL_EDIT.getText()+" || "+client.getNickname()+" || "+channel.getId());
        logger.log(Level.OFF, LogType.CHANNEL_EDIT.getText()+" || "+client.getNickname()+" || "+channel.getId());
    }

    private void readConfig() {
        try {
            FileInputStream inputStream = new FileInputStream(configPath);
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            createConfig();
            System.out.println(" [ERROR] Bot not configured. Edit the config.properties with database information (DO NOT CHANGE ID)");
            System.out.println(" [INFO] Bot is stopping...");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setProperty(ConfigProperty configProperty, String value) {
        try {
            FileWriter fileWriter = new FileWriter(configPath);
            properties.setProperty(configProperty.toString(), value);
            properties.store(fileWriter, null);
        } catch (FileNotFoundException e) {
            createConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createConfig() {
        try {
            FileWriter fileWriter = new FileWriter(configPath);
            properties.setProperty(ConfigProperty.ID.toString(), "0");
            properties.setProperty(ConfigProperty.HOST.toString(), "localhost");
            properties.setProperty(ConfigProperty.PORT.toString(), "3306");
            properties.setProperty(ConfigProperty.DATABASE.toString(), "database");
            properties.setProperty(ConfigProperty.USERNAME.toString(), "username");
            properties.setProperty(ConfigProperty.PASSWORD.toString(), "password");
            properties.store(fileWriter, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        readConfig();
    }

}
