package org.data7.bYD_WORLD_UTRAL;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.awt.*;

import static org.bukkit.Bukkit.getServer;

public class Playerdeath implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getPlayer();
        Location deathLocation = player.getLocation();
        if (player.getScoreboardTags().contains("suicide")){
            event.deathMessage(null);
            player.removeScoreboardTag("suicide");
        }
        Component backmsg = Component.text("🪦死亡地点:(" + (int) deathLocation.getX() + "," + (int) deathLocation.getY() + "," + (int) deathLocation.getZ() + ") ").append(Component.text("[点击传送]", TextColor.color(8, 255, 242)).clickEvent(ClickEvent.callback(clicker->{
            player.teleport(deathLocation);
            PotionEffect backEffect1 = new PotionEffect(PotionEffectType.GLOWING,50,1,false,false);
            PotionEffect backEffect2 = new PotionEffect(PotionEffectType.RESISTANCE,50,999,false,false);
            PotionEffect backEffect3 = new PotionEffect(PotionEffectType.FIRE_RESISTANCE,50,999,false,false);
            player.addPotionEffect(backEffect1);
            player.addPotionEffect(backEffect2);
            player.addPotionEffect(backEffect3);
        })));
        player.sendMessage(backmsg);
    }
}
