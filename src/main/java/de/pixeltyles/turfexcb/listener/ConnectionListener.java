package de.pixeltyles.turfexcb.listener;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ConnectionListener implements Listener {
    public void onConnect(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

}
