package de.pixeltyles.turfexcb.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DiscordCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            TextComponent textComponent = new TextComponent("Du willst auf unseren Discord? ");

            TextComponent textComponent1 = new TextComponent("Klicke hier!");
            textComponent1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/5uDdbxmnp8"));

            textComponent.addExtra(textComponent1);

            sender.sendMessage(textComponent);
        } else {
            sender.sendMessage("DU musst ein Spieler sein!");
        }
        
        return true;
    }
}
