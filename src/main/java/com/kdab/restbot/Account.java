package com.kdab.restbot;

class Account {
    public Account( String user, String server, int port, String password ) {
        m_user = user;
        m_server = server;
        m_port = port;
        m_password = password;
    }

    public final String getUser() {
        return m_user;
    }

    public final String getServer() {
        return m_server;
    }

    public final int getPort() {
        return m_port;
    }

    public final String getPassword() {
        return m_password;
    }

    private String m_user;
    private String m_server;
    private int m_port;
    private String m_password;
}
