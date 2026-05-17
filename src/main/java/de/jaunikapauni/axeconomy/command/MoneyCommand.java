package de.jaunikapauni.axeconomy.command;

import de.jaunikapauni.axeconomy.AxEconomy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

        String subCommand = args[0].toLowerCase();
        Player sourcePlayer = (Player) sender;
        Player targetPlayer = Bukkit.getServer().getPlayer(args[1]);
        Double amount = Double.parseDouble(args[2]);
        Double balance = reference.getEconomyManager().getBalance(sourcePlayer.getUniqueId());

        switch (subCommand){
            case "set":
                reference.getEconomyManager().setBalance(targetPlayer.getUniqueId(), amount);
                sourcePlayer.sendMessage("You have set the balance of " + targetPlayer.getName() + " to " + balance);
                targetPlayer.sendMessage("Your balance was set to " + balance);
                break;
            case "add":
                reference.getEconomyManager().addBalance(targetPlayer.getUniqueId(), amount);
                sourcePlayer.sendMessage("You have added " + amount + " to the balance of " + targetPlayer.getName());
                targetPlayer.sendMessage(amount + " was added to your balance");
                break;
            case "remove":
                reference.getEconomyManager().removeBalance(targetPlayer.getUniqueId(), amount);
                sourcePlayer.sendMessage("You have removed " + amount + " from the balance of " + targetPlayer.getName());
                targetPlayer.sendMessage(amount + " were removed from your balance");
                break;
            case "pay":
                reference.getEconomyManager().removeBalance(sourcePlayer.getUniqueId(), amount);
                reference.getEconomyManager().addBalance(targetPlayer.getUniqueId(), amount);
                sourcePlayer.sendMessage("You have payed " + amount + " to " + targetPlayer.getName());
                targetPlayer.sendMessage("You got paid " + amount + " from " + sourcePlayer.getName());
                return true;
            default:
                sourcePlayer.sendMessage("Your balance: " + balance);
        }
        return true;
    }
}
