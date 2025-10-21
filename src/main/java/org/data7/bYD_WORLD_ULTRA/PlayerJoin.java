package org.data7.bYD_WORLD_ULTRA;

import com.destroystokyo.paper.ParticleBuilder;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.data7.bYD_WORLD_ULTRA.PAPI.PAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class PlayerJoin {
    private static final Logger log = LoggerFactory.getLogger(PlayerJoin.class);

    public static class PlayerListener implements Listener{
        @EventHandler
        public void playerJoin(PlayerJoinEvent event){
            Player player = event.getPlayer();
            Component joinMessage = Component.translatable("player.join.msg",Component.text(player.getName()).color(TextColor.color(255, 255, 0)));
            event.joinMessage(joinMessage);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 5f, 5f);}
            PotionEffect joinEffect = new PotionEffect(PotionEffectType.GLOWING,10,1,false,false);
            player.addPotionEffect(joinEffect);
            this.loadBroadcast(player);
        }
        @EventHandler
        public void PLayerExit(PlayerQuitEvent event){
            // 获取玩家对象
            Player player = event.getPlayer();

            // 构建退出消息
//            Component quitmessage = Component.text("🚗")
//                    .append(Component.text(player.getName())
//                            .color(TextColor.color(255, 255, 0))) // 设置玩家名字为黄色
//                    .append(Component.text("暂时离开了!"));

            Component quitmessage = Component.translatable("player.exit.msg",Component.text(player.getName()).color(TextColor.color(255, 255, 0)));

            // 设置离开消息
            event.quitMessage(quitmessage);

            // 生成粒子
            for (int i = 0; i < 500; i++) {
                double x = player.getLocation().getX() + Math.random() * 2 - 1;
                double y = player.getLocation().getY() + Math.random() * 2;
                double z = player.getLocation().getZ() + Math.random() * 2 - 1;
                new ParticleBuilder(Particle.ENCHANT)
                        .location(player.getLocation())
                        .count(1)
                        .spawn();
            }


            // 获取所有在线玩家
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                // 为每个在线玩家播放音效
                onlinePlayer.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 5f, 5f);}
        }

        /** 加载 Broadcast 公告模块: broadcast.yml */
        public void loadBroadcast(Player player) {
            File broadcastFile = new File(BYD_WORLD_ULTRA.getPlugin(BYD_WORLD_ULTRA.class).getDataFolder(), "broadcast.yml");
            YamlConfiguration broadcastConfig = YamlConfiguration.loadConfiguration(broadcastFile);
            if (!broadcastFile.exists()) {
                BYD_WORLD_ULTRA.getPlugin(BYD_WORLD_ULTRA.class).saveResource("config.yml", false);
            }
            boolean enableBroadcast = broadcastConfig.getBoolean("enable");
            boolean debug = broadcastConfig.getBoolean("debug");
            List<String> blacklist = broadcastConfig.getStringList("blacklist");
            String defaultTitle = broadcastConfig.getString("default.title");
            List<String> defaultContent = broadcastConfig.getStringList("default.content");
            List<Map<?, ?>> playerList = broadcastConfig.getMapList("player");
            if(debug){
                String defaultContentStr = String.join(", ", defaultContent);
                getServer().getLogger().info(
                        "\n||DEBUG(Available in the configuration file broadcast.yml)|| Broadcast Statues:\n"
                                + "Debug:\t" + debug + "\n"
                                + "Enable:\t" + enableBroadcast + "\n"
                                + "Blacklist:\t" + blacklist + "\n"
                                + "Default Title:\t" + defaultTitle + "\n"
                                + "Default Content:\t" + defaultContentStr + "\n"
                );
                for (Map<?, ?> players : playerList) {
                    String name = (String) players.get("name");
                    String title = (String) players.get("title");
                    List<String> contentList = (List<String>) players.get("content");
                    String contentStr = contentList != null ? String.join(", ", contentList) : "null";
                    getServer().getLogger().info("\nPlayer:\t" + name
                            + "\nTitle:\t" + title
                            + "\nContent:\t" + contentStr + "\n"
                    );
                }
            }
            boolean uniquePlayer = false;
            String Title = "";
            List<String> ContentList = new ArrayList<>();
            if(enableBroadcast){
                if (!blacklist.contains(player.getName())){
                    for(Map<?, ?> players : playerList) {
                        String name = (String) players.get("name");
                        String title = (String) players.get("title");
                        List<String> content = (List<String>) players.get("content");
                        if (player.getName().equals(name)) {
                            uniquePlayer = true;
                            Title = title;
                            ContentList = content != null ? content : new ArrayList<>();
                            break;
                        }
                    }
                    if (!uniquePlayer) {
                        Title = defaultTitle;
                        ContentList = defaultContent;
                    }
                    if (Title != null) {
                        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                            player.sendRawMessage(PlaceholderAPI.setPlaceholders(player,Title));
                        }
                        else player.sendRawMessage(Title);
                    }
                    // 按行发送内容
                    for (String line : ContentList) {
                        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                            player.sendRawMessage(PlaceholderAPI.setPlaceholders(player,line));
                        }
                        else player.sendRawMessage(line);
                    }
                }
            }
        }
    }
}
