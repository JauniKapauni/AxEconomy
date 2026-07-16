package de.jaunikapauni.axeconomy.manager;

import de.jaunikapauni.axeconomy.AxEconomy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EconomyManager {

    AxEconomy reference;

    public EconomyManager(AxEconomy reference) {
        this.reference = reference;
    }

    Map<UUID, Double> balances = new ConcurrentHashMap<>();

    public double getBalance(UUID uuid) {
        Double cached = balances.get(uuid);
        if(cached != null){
            return cached;
        }
        try (Connection conn = reference.getDatabaseManager().getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT balance FROM balances WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            double balance = 0.0;
            if (rs.next()) {
                balance = rs.getDouble("balance");
            }
            balances.put(uuid, balance);
            return balance;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setBalance(UUID uuid, Double amount) {
        balances.put(uuid, amount);
    }

    public void addBalance(UUID uuid, Double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public boolean removeBalance(UUID uuid, Double amount) {
        double current = getBalance(uuid);
        if (current < amount) {
            return false;
        }
        setBalance(uuid, current - amount);
        return true;
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

    public void saveCachedBalance(UUID uuid){
        Double balance = balances.get(uuid);
        if(balance == null){
            return;
        }
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT uuid FROM balances WHERE uuid = ?")){
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    try(PreparedStatement update = conn.prepareStatement("UPDATE balances SET balance = ? WHERE uuid = ?")){
                        update.setDouble(1, balance);
                        update.setString(2, uuid.toString());
                        update.executeUpdate();
                    }
                } else {
                    try(PreparedStatement insert = conn.prepareStatement("INSERT INTO balances(uuid, balance) VALUES (?, ?)")){
                        insert.setString(1, uuid.toString());
                        insert.setDouble(2, balance);
                        insert.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadBalance(UUID uuid){
        if(balances.containsKey(uuid)){
            return;
        }
        getBalance(uuid);
    }

    public void removeCache(UUID uuid){
        balances.remove(uuid);
    }
}