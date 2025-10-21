package org.data7.bYD_WORLD_ULTRA;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Playerdeath implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getPlayer();
        Location deathLocation = player.getLocation();
        if (player.getScoreboardTags().contains("suicide")){
            event.deathMessage(null);
            player.removeScoreboardTag("suicide");
        }
        Component backmsg = Component.translatable("player.death.msg",Component.text("("+(int) deathLocation.getX() + "," + (int) deathLocation.getY() + "," + (int) deathLocation.getZ()+")"),Component.translatable("player.death.button").color(TextColor.color(8, 255, 242)).clickEvent(ClickEvent.callback(clicker->{
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
