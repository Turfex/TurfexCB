package de.pixeltyles.turfexcb.listener;

import de.pixeltyles.turfexcb.Database.DataBaseManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;

public class ChatManager implements Listener {
    private final DataBaseManager dbManager;
    private final LuckPerms luckPerms;

    public ChatManager(DataBaseManager dbManager, LuckPerms luckPerms) {
        this.dbManager = dbManager;
        this.luckPerms = LuckPermsProvider.get();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());

        String prefix = "";
        if (user != null) {
            CachedMetaData metaData = user.getCachedData().getMetaData();
            prefix = metaData.getPrefix() != null ? metaData.getPrefix() : "";
        }

        event.setFormat(prefix + " §8| §7" + player.getName() + "§r: " + event.getMessage());
    }
}

