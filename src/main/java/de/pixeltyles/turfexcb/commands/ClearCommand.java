package de.pixeltyles.turfexcb.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClearCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            if (Bukkit.getPlayer(args[0]) != null) {
                Bukkit.getPlayer(args[0]).getInventory().clear();
                commandSender.sendMessage("§aTurfex §8» §aDu hast §e" + args + " §agecleared.");
            } else {
                commandSender.sendMessage("§aTurfex §8» §cDieser Spieler ist nicht online!");
            }
        } else {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;

                player.getInventory().clear();
                commandSender.sendMessage("§aTurfex §8» §aDu hast dich gecleared.");
            } else {
                commandSender.sendMessage("Du kannst dich nicht selber clearen.");
            }
        }

        return false;
    }
}
