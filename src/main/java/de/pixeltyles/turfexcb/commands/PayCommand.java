package de.pixeltyles.turfexcb.commands;

import de.pixeltyles.turfexcb.Database.DataBaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final DataBaseManager db;

    public PayCommand(DataBaseManager db) {
        this.db = db;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Dieser Befehl kann nur von Spielern benutzt werden.");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Benutzung: /pay <Spieler> <Betrag>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || !target.hasPlayedBefore()) {
            player.sendMessage(ChatColor.RED + "Spieler nicht gefunden.");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Bitte gib einen gültigen Betrag ein.");
            return true;
        }

        String senderUUID = player.getUniqueId().toString();
        String targetUUID = target.getUniqueId().toString();

        int senderCoins = db.getCoins(senderUUID);
        if (senderCoins < amount) {
            player.sendMessage(ChatColor.RED + "Du hast nicht genug Coins!");
            return true;
        }

        db.setCoins(senderUUID, senderCoins - amount);
        db.setCoins(targetUUID, db.getCoins(targetUUID) + amount);

        player.sendMessage(ChatColor.GREEN + "Du hast " + target.getName() + " " + amount + " Coins gesendet.");
        if (target.isOnline()) {
            ((Player) target).sendMessage(ChatColor.GREEN + "Du hast " + amount + " Coins von " + player.getName() + " erhalten.");
        }

        return true;
    }
}
