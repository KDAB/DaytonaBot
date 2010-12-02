/*
    This file is part of Daytona.

    Copyright (c) 2010 Frank Osterfeld <frank.osterfeld@kdab.com>

    Daytona is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    Daytona is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/

package com.kdab.daytona;

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
