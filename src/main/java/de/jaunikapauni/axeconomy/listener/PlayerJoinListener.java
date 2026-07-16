package de.jaunikapauni.axeconomy.listener;

import de.jaunikapauni.axeconomy.AxEconomy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

    AxEconomy reference;
    public PlayerJoinListener(AxEconomy reference){
        this.reference = reference;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        String name = p.getName();
        Bukkit.getScheduler().runTaskAsynchronously(reference, () -> {
            reference.getEconomyManager().loadBalance(uuid);
            List<String> messages;
            try{
                messages = reference.getEconomyManager().getAndDeleteNotifications(uuid);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            reference.getPlayerManager().updatePlayerStatus(uuid, name, true);
            Bukkit.getScheduler().runTask(reference, () -> {
                for(String msg : messages){
                    p.sendMessage(msg);
                }
            });
        });
    }
}
