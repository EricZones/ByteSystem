// Created by Eric B. 21.10.2021 13:18
package de.ericzones.bytesystem;

import de.ericzones.bytesystem.collectives.byteclient.ByteClientManager;
import de.ericzones.bytesystem.collectives.bytecore.ByteCoreManager;
import de.ericzones.bytesystem.collectives.bytefeature.ByteFeatureManager;
import de.ericzones.bytesystem.collectives.database.DatabaseHandler;
import de.ericzones.bytesystem.collectives.file.ConfigProperty;
import de.ericzones.bytesystem.collectives.file.FileManager;
import de.ericzones.bytesystem.engine.ConsoleListener;
import de.ericzones.bytesystem.engine.Query;

public class ByteSystem {

    private static FileManager fileManager;
    private static DatabaseHandler databaseHandler;
    private static int databaseId;
    private static ByteCoreManager byteCoreManager;
    private static ByteClientManager byteClientManager;
    private static ByteFeatureManager byteFeatureManager;
    private static Query query;

    private static int botId;
    private static final String name = "ByteSystem";
    private static final String version = "1.0";
    private static final String channel = "[color=green]Release[/color]";
    private static final String timeZone = "+2";

    public static void main(String[] args) {
        registerObjects();
    }

    private static void registerObjects() {
        fileManager = new FileManager();
        databaseHandler = new DatabaseHandler();

        databaseId = databaseHandler.configureConnection(fileManager.getProperty(ConfigProperty.HOST),
                Integer.parseInt(fileManager.getProperty(ConfigProperty.PORT)),
                fileManager.getProperty(ConfigProperty.DATABASE),
                fileManager.getProperty(ConfigProperty.USERNAME),
                fileManager.getProperty(ConfigProperty.PASSWORD));
        databaseHandler.connectDatabase(databaseId);
        byteCoreManager = new ByteCoreManager(databaseHandler, databaseId);

        if(fileManager.getProperty(ConfigProperty.ID).equals("0")) {
            botId = byteCoreManager.initialByteCore();
            fileManager.setProperty(ConfigProperty.ID, String.valueOf(botId));
        } else
            botId = Integer.parseInt(fileManager.getProperty(ConfigProperty.ID));

        if(!byteCoreManager.byteCoreConfigExists(botId)) {
            System.out.println(" [ERROR] Query not configured. Update Host, Query-Loginname and Query-Password in the database. Keep port null if not needed.");
            System.out.println(" [INFO] Bot is stopping...");
            System.exit(0);
        }
        byteClientManager = new ByteClientManager(databaseHandler, databaseId, botId);
        byteFeatureManager = new ByteFeatureManager(databaseHandler, databaseId);
        query = new Query(botId);
        ConsoleListener consoleListener = new ConsoleListener(botId);
    }

    public static int getBotId() {
        return botId;
    }

    public static String getName() {
        return name;
    }

    public static String getTimeZone() {
        return timeZone;
    }

    public static String getVersion() {
        return version;
    }

    public static String getChannel() {
        return channel;
    }

    public static FileManager getFileManager() {
        return fileManager;
    }

    public static ByteClientManager getByteClientManager() {
        return byteClientManager;
    }

    public static ByteCoreManager getByteCoreManager() {
        return byteCoreManager;
    }

    public static ByteFeatureManager getByteFeatureManager() {
        return byteFeatureManager;
    }

    public static Query getQuery() {
        return query;
    }

}
