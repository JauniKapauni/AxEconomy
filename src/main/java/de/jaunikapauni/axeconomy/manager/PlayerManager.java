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
            try(PreparedStatement ps = conn.prepareStatement("REPLACE players(uuid, name, online) VALUES (?, ?, ?)")){
                ps.setString(1, uuid.toString());
                ps.setString(2, name);
                ps.setBoolean(3, online);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getOnlinePlayers(){
        List<String> list = new ArrayList<>();
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT name FROM players")){
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
