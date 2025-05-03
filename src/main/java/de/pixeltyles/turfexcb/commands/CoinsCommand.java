package de.pixeltyles.turfexcb.commands;

import de.pixeltyles.turfexcb.TurfexCB;
import de.pixeltyles.turfexcb.Database.DataBaseManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoinsCommand implements CommandExecutor {

    private final TurfexCB plugin;
    private final DataBaseManager db;

    public CoinsCommand(TurfexCB plugin, DataBaseManager db) {
        this.plugin = plugin;
        this.db = db;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) return false;


        if (args.length != 3) {
            p.sendMessage("§eCoins §cBenutze: /coins set/add/remove [Spieler] [Zahl]");
            return true;
        }

        String sub = args[0];
        Player target = Bukkit.getPlayer(args[1]);
        int amount;

        if (target == null) {
            p.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            p.sendMessage("§cBitte gib eine gültige Zahl ein.");
            return true;
        }

        String uuid = target.getUniqueId().toString();
        int current = db.getCoins(uuid);

        switch (sub.toLowerCase()) {
            case "set" -> db.setCoins(uuid, amount);
            case "add" -> db.setCoins(uuid, current + amount);
            case "remove" -> db.setCoins(uuid, Math.max(0, current - amount));
            default -> {
                p.sendMessage("§cBenutze: /coins set/add/remove [Spieler] [Zahl]");
                return true;
            }
        }

        p.sendMessage("§aCoins von §e" + target.getName() + " §asind nun §e" + db.getCoins(uuid));
        return true;
    }
}
