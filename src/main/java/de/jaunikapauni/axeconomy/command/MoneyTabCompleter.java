package de.jaunikapauni.axeconomy.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MoneyTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> cmds = new ArrayList<>();
        cmds.add("set");
        cmds.add("add");
        cmds.add("remove");
        cmds.add("pay");
        if(args.length == 1){
            return cmds;
        }
        return cmds;
    }
}
