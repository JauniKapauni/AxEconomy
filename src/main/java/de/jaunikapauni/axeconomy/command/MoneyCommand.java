package de.jaunikapauni.axeconomy.command;

import de.jaunikapauni.axeconomy.AxEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MoneyCommand implements CommandExecutor {
    AxEconomy reference;
    public MoneyCommand(AxEconomy reference){
        this.reference = reference;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("Only players can run this command!");
            return true;
        }
        if(args.length == 0){
            Player sourcePlayer = (Player) sender;
            Double balance = reference.getEconomyManager().getBalance(sourcePlayer.getUniqueId());
            sender.sendMessage("Your balance: " + balance);
            return true;
        }
        if(args.length < 3){
            return false;
        }
        String subCommand = args[0].toLowerCase();
        Player sourcePlayer = (Player) sender;
        if(!sourcePlayer.hasPermission("axeconomy.money")){
            sourcePlayer.sendMessage("You don't have the permission! [axeconomy.money]");
            return true;
        }
        Player targetPlayer = Bukkit.getServer().getPlayerExact(args[1]);
        Double amount;
        try{
            amount = Double.parseDouble(args[2]);
            if(amount <= 0){
                sourcePlayer.sendMessage(ChatColor.RED + "Amount must be positive!");
                return true;
            }
        } catch (NumberFormatException e) {
            sourcePlayer.sendMessage(ChatColor.RED + "Amount must be a number!");
            return false;
        }
        Double balance = reference.getEconomyManager().getBalance(sourcePlayer.getUniqueId());
        UUID targetUUID;
        String targetName;

        if(targetPlayer != null){
            targetUUID = targetPlayer.getUniqueId();
            targetName = targetPlayer.getName();
        } else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
            targetUUID = offlinePlayer.getUniqueId();
            if(offlinePlayer.getName() != null){
                targetName = offlinePlayer.getName();
            } else {
                targetName = args[1];
            }
        }

        switch (subCommand){
            case "set":
                if(!sourcePlayer.hasPermission("axeconomy.money.set")){
                    sourcePlayer.sendMessage("You don't have the permission! [axeconomy.money.set]");
                    return true;
                }
                reference.getEconomyManager().setBalance(targetUUID, amount);
                sourcePlayer.sendMessage("You have set the balance of " + targetName + " to " + amount);
                sendOrStore(targetPlayer, targetUUID, "Your balance was set to " + amount);
                break;
            case "add":
                if(!sourcePlayer.hasPermission("axeconomy.money.add")){
                    sourcePlayer.sendMessage("You don't have the permission! [axeconomy.money.add]");
                    return true;
                }
                reference.getEconomyManager().addBalance(targetUUID, amount);
                sourcePlayer.sendMessage("You have added " + amount + " to the balance of " + targetName);
                sendOrStore(targetPlayer, targetUUID, amount + " was added to your balance");
                break;
            case "remove":
                if(!sourcePlayer.hasPermission("axeconomy.money.remove")){
                    sourcePlayer.sendMessage("You don't have the permission! [axeconomy.money.remove]");
                    return true;
                }
                reference.getEconomyManager().removeBalance(targetUUID, amount);
                sourcePlayer.sendMessage("You have removed " + amount + " from the balance of " + targetName);
                sendOrStore(targetPlayer, targetUUID, amount + " were removed from your balance");
                break;
            case "pay":
                if(!sourcePlayer.hasPermission("axeconomy.money.pay")){
                    sourcePlayer.sendMessage("You don't have the permission! [axeconomy.money.pay]");
                    return true;
                }
                double senderBalance = reference.getEconomyManager().getBalance(sourcePlayer.getUniqueId());
                if(senderBalance < amount){
                    sourcePlayer.sendMessage(ChatColor.RED + "You don't have enough money!");
                    return true;
                }
                reference.getEconomyManager().removeBalance(sourcePlayer.getUniqueId(), amount);
                reference.getEconomyManager().addBalance(targetUUID, amount);
                sourcePlayer.sendMessage("You have payed " + amount + " to " + targetName);
                sendOrStore(targetPlayer, targetUUID, "You got paid " + amount + " from " + sourcePlayer.getName());
                return true;
            default:
                sourcePlayer.sendMessage("Your balance: " + balance);
        }
        return true;
    }

    public void sendOrStore(Player targetPlayer, UUID targetUUID, String message){
        if(targetPlayer != null){
            targetPlayer.sendMessage(message);
        } else {
            reference.getEconomyManager().addPendingNotification(targetUUID, message);
        }
    }
}
