package com.bda.db;

public class ConnectionData {
    private String host;
    private int port;
    private String database;
    private String user;
    private String password;

    public String getHost() {
        return this.host == null ? "" : this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return this.database == null ? "" : this.database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUser() {
        return this.user == null ? "" : this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password == null ? "" : this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format(
            "%s:%d %s:%s /%s", 
            this.host,
            this.port,
            this.user,
            this.password,
            this.database
        );
    }
}