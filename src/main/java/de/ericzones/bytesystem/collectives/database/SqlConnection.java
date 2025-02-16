// Created by Eric B. 14.02.2022 18:45
package de.ericzones.bytesystem.collectives.database;

import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class SqlConnection {

    private final int id;

    private final Timer timer;
    private final String host, database, username, password;
    private final int port;

    private Connection connection;
    private TimerTask timerTask;

    public SqlConnection(int id, String host, int port, String database, String username, String password) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.timer = new Timer();
    }

    public void connect() {
        try {
            if(isConnected()) {
                System.out.println(" [INFO] SQL is already connected");
                return;
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database+"?useJDBCCompliantTimezoneShift=true&serverTimezone=Europe/Berlin&useUnicode=true&autoReconnect=true", username, password);
            System.out.println(" [INFO] SQL connected to "+database);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        if(timerTask != null)
            timerTask.cancel();
        timer.schedule(timerTask = new TimerTask() {
            @Override
            public void run() {
                if(!isConnected()) connect();
                getResult("SELECT 1");
            }
        }, 60*1000, 60*1000);
    }

    public boolean isConnected() {
        try {
            if(connection != null && !connection.isClosed())
                return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void disconnect() {
        try {
            if(timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
            if(isConnected()) {
                connection.close();
                System.out.println(" [INFO] SQL disconnected from "+database);
                return;
            }
            System.out.println(" [INFO] SQL is already disconnected");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void update(String sqlUpdate) {
        try {
            if(!isConnected()) connect();
            PreparedStatement statement = connection.prepareStatement(sqlUpdate);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public ResultSet getResult(String sqlQuery) {
        try {
            if(!isConnected()) connect();
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            return statement.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

}