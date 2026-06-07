package de.jaunikapauni.axeconomy;

import de.jaunikapauni.axeconomy.command.MoneyCommand;
import de.jaunikapauni.axeconomy.command.MoneyTabCompleter;
import de.jaunikapauni.axeconomy.manager.DatabaseManager;
import de.jaunikapauni.axeconomy.manager.EconomyManager;
import de.jaunikapauni.axeconomy.placeholder.MoneyPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AxEconomy extends JavaPlugin {
    DatabaseManager databaseManager;
    public DatabaseManager getDatabaseManager(){
        return databaseManager;
    }
    EconomyManager economyManager;
    public EconomyManager getEconomyManager(){
        return economyManager;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        try{
            databaseManager = new DatabaseManager(this);
            economyManager = new EconomyManager(this);
            if(databaseManager.initDatabaseTable1() == false){
                getLogger().severe("Error creating balances table!");
                Bukkit.getServer().shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        getCommand("money").setExecutor(new MoneyCommand(this));
        getCommand("money").setTabCompleter(new MoneyTabCompleter());
        if(Bukkit.getPluginManager().getPlugin("PlaceHolderAPI") != null){
            new MoneyPlaceholder(this).register();
            getLogger().info("Successfully registered AxEconomy placeholders!");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
