package com.ajulay;

import java.sql.*;

/**
 * Created by ajulay on 21.03.2018.
 */
public class SQLHandler {
private static Connection conn;
private static Statement stmt;

public static void connect() throws ClassNotFoundException, SQLException {
    Class.forName("org.sqlite.JDBC");
    conn = DriverManager.getConnection("jdbc:sqlite:server/data.db");
    stmt = conn.createStatement();
}

    public static String getNickByLoginPass(String login, String pass) {
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery("SELECT nick FROM users WHERE login ='" + login + "' AND password = '" + pass + "';");
            String nick = "";

            if (rs.next()) {

                nick = rs.getString(1);
                
                return nick;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void disconnect() {
        try {

            conn.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
