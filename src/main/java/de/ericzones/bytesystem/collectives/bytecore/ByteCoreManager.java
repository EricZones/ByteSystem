// Created by Eric B. 23.10.2021 13:59
package de.ericzones.bytesystem.collectives.bytecore;

import de.ericzones.bytesystem.collectives.database.DatabaseHandler;

public class ByteCoreManager extends SqlByteCore {

    public ByteCoreManager(DatabaseHandler databaseHandler, int databaseId) {
        super(databaseHandler, databaseId);
    }

    public int initialByteCore() {
        return createByteCore();
    }

    public void setByteCoreOnline(int id) {
        updateByteCore(id, true);
    }

    public void setByteCoreOffline(int id) {
        updateByteCore(id, false);
    }

    public void setByteCoreHost(int id, String host) {
        updateByteCoreConfig(id, 1, host);
    }

    public void setByteCorePort(int id, String port) {
        updateByteCoreConfig(id, 2, port);
    }

    public void setByteCoreServerId(int id, int serverId) {
        updateByteCoreConfig(id, 3, serverId);
    }

    public void setByteCoreLoginName(int id, String loginName) {
        updateByteCoreConfig(id, 4, loginName);
    }

    public void setByteCorePassword(int id, String password) {
        updateByteCoreConfig(id, 5, password);
    }

    public void setByteCoreNickname(int id, String nickname) {
        updateByteCoreConfig(id, 6, nickname);
    }

    public void setByteCoreChannel(int id, int channel) {
        updateByteCoreConfig(id, 7, channel);
    }

}
