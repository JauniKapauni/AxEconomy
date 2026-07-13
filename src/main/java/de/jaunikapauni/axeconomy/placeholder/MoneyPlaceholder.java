package de.jaunikapauni.axeconomy.placeholder;

import de.jaunikapauni.axeconomy.AxEconomy;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MoneyPlaceholder extends PlaceholderExpansion {
    AxEconomy reference;
    public MoneyPlaceholder(AxEconomy reference){
        this.reference = reference;
    }

    @Override
    public @NotNull String getIdentifier(){
        return "axeconomy";
    }
    @Override
    public @NotNull String getAuthor(){
        return "JauniKapauni";
    }
    @Override
    public @NotNull String getVersion(){
        return "1.0.0";
    }
    @Override
    public @Nullable String onRequest(OfflinePlayer p, @NotNull String params){
        if(params.equalsIgnoreCase("balance")){
            double balance = reference.getEconomyManager().getBalance(p.getUniqueId());
            return String.format("%.2f", balance);
        }
        return null;
    }
}
