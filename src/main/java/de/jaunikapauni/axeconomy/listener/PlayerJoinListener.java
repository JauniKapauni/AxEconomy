package de.jaunikapauni.axeconomy.listener;

import de.jaunikapauni.axeconomy.AxEconomy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.List;

public class PlayerJoinListener implements Listener {

    AxEconomy reference;
    public PlayerJoinListener(AxEconomy reference){
        this.reference = reference;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws SQLException {
        Player p = e.getPlayer();
        List<String> messages = reference.getEconomyManager().getAndDeleteNotifications(p.getUniqueId());
        for(String msg : messages){
            p.sendMessage(msg);
        }
        reference.getPlayerManager().updatePlayerStatus(p.getUniqueId(), p.getName(), true);
    }
}
