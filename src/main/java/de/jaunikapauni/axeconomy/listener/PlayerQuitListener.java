package de.jaunikapauni.axeconomy.listener;

import de.jaunikapauni.axeconomy.AxEconomy;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {
    AxEconomy reference;
    public PlayerQuitListener(AxEconomy reference){
        this.reference = reference;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        UUID uuid = e.getPlayer().getUniqueId();
        String name = e.getPlayer().getName();
        Bukkit.getScheduler().runTaskAsynchronously(reference, () -> {
            reference.getEconomyManager().saveCachedBalance(uuid);
            double sourcePlayerBalance = reference.getEconomyManager().getBalance(uuid);
            reference.getLoggingManager().log("SOURCE_PLAYER: " + name + " - " + "TYPE: " + "QUIT" + " - " + sourcePlayerBalance);
            reference.getEconomyManager().removeCache(uuid);
            reference.getPlayerManager().updatePlayerStatus(uuid, name, false);
        });
    }
}
