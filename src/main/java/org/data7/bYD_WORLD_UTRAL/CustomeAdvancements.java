package org.data7.bYD_WORLD_UTRAL;

import com.fren_gor.ultimateAdvancementAPI.events.PlayerLoadingCompletedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CustomeAdvancements implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        // Called after a player has successfully been loaded by the API
        Player p = e.getPlayer();
        // Here you can show tabs to players
        BYD_WORLD_UTRAL.advancementTab.showTab(p);
    }
}
