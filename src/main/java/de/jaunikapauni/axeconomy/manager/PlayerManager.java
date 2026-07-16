package de.jaunikapauni.axeconomy.manager;

import de.jaunikapauni.axeconomy.AxEconomy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager {
    AxEconomy reference;
    public PlayerManager(AxEconomy reference){
        this.reference = reference;
    }

    public void updatePlayerStatus(UUID uuid, String name, boolean online){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement select = conn.prepareStatement("SELECT uuid FROM players WHERE uuid = ?")){
                select.setString(1, uuid.toString());
                ResultSet selectrs = select.executeQuery();
                if(selectrs.next()){
                    try(PreparedStatement update = conn.prepareStatement("UPDATE players SET name = ?, online = ? WHERE uuid = ?")){
                        update.setString(1, name);
                        update.setBoolean(2, online);
                        update.setString(3, uuid.toString());
                        update.executeUpdate();
                    }
                } else {
                    try(PreparedStatement insert = conn.prepareStatement("INSERT INTO players(uuid, name, online) VALUES (?, ?, ?)")){
                        insert.setString(1, uuid.toString());
                        insert.setString(2, name);
                        insert.setBoolean(3, online);
                        insert.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getOnlinePlayers(){
        List<String> list = new ArrayList<>();
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT name FROM players WHERE online = true")){
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    list.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
