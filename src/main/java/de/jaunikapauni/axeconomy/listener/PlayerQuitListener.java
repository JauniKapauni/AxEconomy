package de.jaunikapauni.axeconomy.listener;

import de.jaunikapauni.axeconomy.AxEconomy;
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
        reference.getEconomyManager().saveCachedBalance(uuid);
        reference.getEconomyManager().removeCache(uuid);
        reference.getPlayerManager().updatePlayerStatus(e.getPlayer().getUniqueId(), e.getPlayer().getName(), false);
    }
}
