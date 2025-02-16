// Created by Eric B. 14.02.2022 18:44
package de.ericzones.bytesystem.collectives.database;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseHandler extends SqlAdapter {

    private final List<SqlConnection> connections;
    private int currentId;

    public DatabaseHandler() {
        this.connections = new ArrayList<>();
        this.currentId = 1;
    }

    public int configureConnection(String host, int port, String database, String username, String password) {
        SqlConnection connection = new SqlConnection(currentId, host, port, database, username, password);
        if(connections.stream().anyMatch(current -> current.equals(connection))) return -1;
        this.currentId++;
        this.connections.add(connection);
        return connection.getId();
    }

    public void connectDatabase(int connectionId) {
        SqlConnection connection = getConnectionById(connectionId);
        if(connection == null) return;
        connection.connect();
    }

    public void disconnectDatabase(int connectionId) {
        SqlConnection connection = getConnectionById(connectionId);
        if(connection == null) return;
        connection.disconnect();
        this.connections.remove(connection);
    }

    public List<Integer> getConnectionsByDatabase(String database) {
        List<Integer> connectionIds = new ArrayList<>();
        List<SqlConnection> connections = this.connections.stream().filter(current -> current.getDatabase().equalsIgnoreCase(database)).collect(Collectors.toList());
        connections.forEach(current -> connectionIds.add(current.getId()));
        return connectionIds;
    }

    public List<Integer> getConnectionsByHost(String host) {
        List<Integer> connectionIds = new ArrayList<>();
        List<SqlConnection> connections = this.connections.stream().filter(current -> current.getHost().equalsIgnoreCase(host)).collect(Collectors.toList());
        connections.forEach(current -> connectionIds.add(current.getId()));
        return connectionIds;
    }

    public List<Integer> getConnections(String host, String database) {
        List<Integer> connectionIds = new ArrayList<>();
        List<SqlConnection> connections = this.connections.stream().filter(current -> current.getHost().equalsIgnoreCase(host) && current.getDatabase().equalsIgnoreCase(database)).collect(Collectors.toList());
        connections.forEach(current -> connectionIds.add(current.getId()));
        return connectionIds;
    }

    public List<Integer> getConnections() {
        List<Integer> connectionIds = new ArrayList<>();
        this.connections.forEach(current -> connectionIds.add(current.getId()));
        return connectionIds;
    }

    public void createTable(int connectionId, String table, Pair<String, SqlDataType>[] columns) {
        SqlConnection connection = getConnectionById(connectionId);
        if(connection == null) return;
        createTable(connection, table, columns);
    }

    public void createTable(int connectionId, String table, Pair<String, SqlDataType>[] columns, String primaryKey) {
        SqlConnection connection = getConnectionById(connectionId);
        if(connection == null) return;
        createTable(connection, table, columns, primaryKey);
    }

    public void createTable(int connectionId, String table, Pair<String, SqlDataType>[] columns, String primaryKey, boolean autoNumbers) {
        SqlConnection connection = getConnectionById(connectionId);
        if(connection == null) return;
        createTable(connection, table, columns, primaryKey, autoNumbers);
    }

    public void updateInTable(int connectionId, String table, Pair<String, Object>[] conditions, String column, Object value) {
        SqlConnection connection = getConnectionById(connectionId);
        if(connection == null) return;
        updateInTable(connection, table, conditions, column, value);
    }

    public void addToTable(int connectionId, String table, Pair<String, Object>[] columnValues) {
        SqlConnection connection = getConnectionById(connectionId);
        if(connection == null) return;
        addToTable(connection, table, columnValues);
    }

    public void addToTable(int connectionId, String table, List<String> columns, List<Object> values) {
        SqlConnection connection = getConnectionById(connectionId);
        if(connection == null) return;
        Pair<String, Object>[] columnValues = new Pair[columns.size()];
        for(int i = 0; i < columns.size(); i++)
            columnValues[i] = new Pair<>(columns.get(i), values.get(i));
        addToTable(connection, table, columnValues);
    }

    public void removeFromTable(int connectionId, String table, Pair<String, Object>[] conditions) {
        SqlConnection connection = getConnectionById(connectionId);
        if(connection == null) return;
        removeFromTable(connection, table, conditions);
    }

    public boolean existsInTable(int connectionId, String table, Pair<String, Object>[] conditions) {
        SqlConnection connection = getConnectionById(connectionId);
        if(connection == null) return false;
        return existsInTable(connection, table, conditions);
    }

    public List<Pair<String, Object>[]> getDataFromTable(int connectionId, String table, String[] columns, Pair<String, Object>[] conditions) {
        SqlConnection connection = getConnectionById(connectionId);
        if(connection == null) return null;
        return getDataFromTable(connection, table, columns, conditions);
    }

    public List<Object> getDataFromTable(int connectionId, String table, Pair<String, Object>[] conditions, String column) {
        SqlConnection connection = getConnectionById(connectionId);
        if(connection == null) return null;
        return getDataFromTable(connection, table, conditions, column);
    }

    public Object getObjectFromTable(int connectionId, String table, Pair<String, Object>[] conditions, String column) {
        SqlConnection connection = getConnectionById(connectionId);
        if(connection == null) return null;
        return getObjectFromTable(connection, table, conditions, column);
    }

    public List<Object> getHighestObjectFromTable(int connectionId, String table, String column, String sortColumn, int amount) {
        SqlConnection connection = getConnectionById(connectionId);
        if(connection == null) return null;
        return getHighestObjectFromTable(connection, table, column, sortColumn, amount);
    }

    private SqlConnection getConnectionById(int id) {
        return this.connections.stream().filter(current -> current.getId() == id).findFirst().orElse(null);
    }

}
