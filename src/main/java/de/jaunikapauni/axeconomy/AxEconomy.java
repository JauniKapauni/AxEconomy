package de.jaunikapauni.axeconomy;

import de.jaunikapauni.axeconomy.api.EconomyAPI;
import de.jaunikapauni.axeconomy.api.EconomyService;
import de.jaunikapauni.axeconomy.command.MoneyCommand;
import de.jaunikapauni.axeconomy.command.MoneyTabCompleter;
import de.jaunikapauni.axeconomy.listener.PlayerJoinListener;
import de.jaunikapauni.axeconomy.listener.PlayerQuitListener;
import de.jaunikapauni.axeconomy.manager.DatabaseManager;
import de.jaunikapauni.axeconomy.manager.EconomyManager;
import de.jaunikapauni.axeconomy.manager.PlayerManager;
import de.jaunikapauni.axeconomy.placeholder.MoneyPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public final class AxEconomy extends JavaPlugin {
    DatabaseManager databaseManager;
    public DatabaseManager getDatabaseManager(){
        return databaseManager;
    }
    EconomyManager economyManager;
    public EconomyManager getEconomyManager(){
        return economyManager;
    }
    PlayerManager playerManager;
    public PlayerManager getPlayerManager(){
        return playerManager;
    }
    EconomyAPI economyAPI;
    public EconomyAPI getEconomyAPI(){
        return economyAPI;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        try{
            databaseManager = new DatabaseManager(this);
            economyManager = new EconomyManager(this);
            playerManager = new PlayerManager(this);
            if(!databaseManager.initDatabaseTable1() || !databaseManager.initDatabaseTable2() || !databaseManager.initDatabaseTable3()){
                getLogger().severe("Error creating balances table!");
                Bukkit.getServer().shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        getCommand("money").setExecutor(new MoneyCommand(this));
        getCommand("money").setTabCompleter(new MoneyTabCompleter(this));
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                List<UUID> pending = economyManager.getPendingUUIDs();
                for(UUID uuid : pending){
                    Player p = Bukkit.getPlayer(uuid);
                    if(p != null){
                        List<String> messages = null;
                        try {
                            messages = economyManager.getAndDeleteNotifications(uuid);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        for(String msg : messages){
                            p.sendMessage(msg);
                        }
                    }
                }
            }
        }, 100L, 100L);
        if(Bukkit.getPluginManager().getPlugin("PlaceHolderAPI") != null){
            new MoneyPlaceholder(this).register();
            getLogger().info("Successfully registered AxEconomy placeholders!");
        }
        getLogger().info("");
        getLogger().info("----------------------------------------");
        getLogger().info("Name: " + getName());
        getLogger().info("Version: " + getDescription().getVersion());
        getLogger().info(String.join("Authors: " + ", ", getDescription().getAuthors()));
        getLogger().info("----------------------------------------");
        getLogger().info("");
        economyAPI = new EconomyService(economyManager);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        databaseManager.close();
    }
}
