package com.kdab.restbot;

class Account {
    public Account( String user, String server, int port, String password ) {
        m_user = user;
        m_server = server;
        m_port = port;
        m_password = password;
    }

    public final String user() {
        return m_user;
    }

    public final String server() {
        return m_server;
    }

    public final int port() {
        return m_port;
    }

    public final String password() {
        return m_password;
    }

    private String m_user;
    private String m_server;
    private int m_port;
    private String m_password;
}
