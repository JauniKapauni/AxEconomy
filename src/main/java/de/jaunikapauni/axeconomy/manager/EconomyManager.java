package de.jaunikapauni.axeconomy.manager;

import de.jaunikapauni.axeconomy.AxEconomy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EconomyManager {

    AxEconomy reference;

    public EconomyManager(AxEconomy reference) {
        this.reference = reference;
    }

    public double getBalance(UUID uuid) {
        try (Connection conn = reference.getDatabaseManager().getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT balance FROM balances WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            } else {
                setBalance(uuid, 0.0);
                return 0.0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setBalance(UUID uuid, Double amount) {
        try (Connection conn = reference.getDatabaseManager().getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT balance FROM balances WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PreparedStatement ps1 = conn.prepareStatement("UPDATE balances SET balance = ? WHERE uuid = ?");
                ps1.setDouble(1, amount);
                ps1.setString(2, uuid.toString());
                ps1.execute();
            } else {
                PreparedStatement ps2 = conn.prepareStatement("INSERT INTO balances(uuid, balance) VALUES(?, ?)");
                ps2.setString(1, uuid.toString());
                ps2.setDouble(2, amount);
                ps2.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addBalance(UUID uuid, Double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public void removeBalance(UUID uuid, Double amount) {
        double current = getBalance(uuid);
        if (current < amount) {
            return;
        }
        setBalance(uuid, current - amount);
    }

    public void addPendingNotification(UUID uuid, String message){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO pending_notifications(uuid, message) VALUES (?, ?)")){
                ps.setString(1, uuid.toString());
                ps.setString(2, message);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getAndDeleteNotifications(UUID uuid) throws SQLException {
        List<String> messages = new ArrayList<>();
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT message FROM pending_notifications WHERE uuid = ?")){
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    messages.add(rs.getString("message"));
                }
                if(!messages.isEmpty()){
                    try(PreparedStatement ps2 = conn.prepareStatement("DELETE FROM pending_notifications WHERE uuid = ?")){
                        ps2.setString(1, uuid.toString());
                        ps2.executeUpdate();
                    }
                }
            }
        }
        return messages;
    }

    public List<UUID> getPendingUUIDs(){
        List<UUID> uuids = new ArrayList<>();
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT uuid FROM pending_notifications")){
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    uuids.add(UUID.fromString(rs.getString("uuid")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return uuids;
    }
}