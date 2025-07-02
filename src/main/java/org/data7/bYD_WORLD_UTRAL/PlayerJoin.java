package org.data7.bYD_WORLD_UTRAL;

import com.destroystokyo.paper.ParticleBuilder;
import com.mojang.brigadier.Message;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class PlayerJoin {
    private static final Logger log = LoggerFactory.getLogger(PlayerJoin.class);

    public static class PlayerListener implements Listener{
        @EventHandler
        public void playerJoin(PlayerJoinEvent event){

            // 获取玩家对象
            Player player = event.getPlayer();

            // 构建欢迎消息
            Component joinMessage = Component.text("🎉欢迎")
                    .append(Component.text(player.getName())
                            .color(TextColor.color(255, 255, 0))) // 设置玩家名字为黄色
                    .append(Component.text("进入服务器!"));

            // 设置欢迎消息
            event.joinMessage(joinMessage);

            // 获取所有在线玩家
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                // 为每个在线玩家播放音效
                onlinePlayer.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 5f, 5f);}

            // 发光
            PotionEffect joinEffect = new PotionEffect(PotionEffectType.GLOWING,10,1,false,false);
            player.addPotionEffect(joinEffect);


            // 获取公告消息
            this.loadBroadcast(player);
        }
        @EventHandler
        public void PLayerExit(PlayerQuitEvent event){
            // 获取玩家对象
            Player player = event.getPlayer();

            // 构建退出消息
            Component quitmessage = Component.text("🚗")
                    .append(Component.text(player.getName())
                            .color(TextColor.color(255, 255, 0))) // 设置玩家名字为黄色
                    .append(Component.text("暂时离开了!"));

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
            File broadcastFile = new File(BYD_WORLD_UTRAL.getPlugin(BYD_WORLD_UTRAL.class).getDataFolder(), "broadcast.yml");
            YamlConfiguration broadcastConfig = YamlConfiguration.loadConfiguration(broadcastFile);
            if (!broadcastFile.exists()) {
                BYD_WORLD_UTRAL.getPlugin(BYD_WORLD_UTRAL.class).saveResource("config.yml", false);
            }
            Bukkit.getServer().getLogger().info("BROADCAST配置加载成功!");
            // 是否启用Broadcast
            boolean enableBroadcast = broadcastConfig.getBoolean("enable");
            // 是否启用debug (不受enable影响)
            boolean debug = broadcastConfig.getBoolean("debug");
            // 黑名单
            List<String> blacklist = broadcastConfig.getStringList("blacklist");
            // 默认标题
            String defaultTitle = broadcastConfig.getString("default.title");
            // 默认内容
            String defaultContent = broadcastConfig.getString("default.content");
            // 获取玩家特定公告
            List<Map<?, ?>> playerList = broadcastConfig.getMapList("player");

            // debug模式
            if(debug){
                Bukkit.getServer().getLogger().info(
                        "\n||DEBUG(可在配置文件 broadcast.yml 关闭)|| Broadcast Statues:\n"
                                + "Debug:\t" + debug + "\n"
                                + "Enable:\t" + enableBroadcast + "\n"
                                + "Blacklist:\t" + blacklist + "\n"
                                + "Default Title:\t" + defaultTitle + "\n"
                                + "Default Content:\t" + defaultContent + "\n"
                );
                for (Map<?, ?> players : playerList) {
                    String name = (String) players.get("name");
                    String title = (String) players.get("title");
                    String content = (String) players.get("content");
                    Bukkit.getServer().getLogger().info("\nPlayer:\t" + name
                            + "\nTitle:\t" + title
                            + "\nContent:\t" + content + "\n"
                    );
                }
            }
            boolean uniquePlayer = false;
            String Title = "";
            String Contentent = "";

            // 输出
            if(enableBroadcast){
                if (!blacklist.contains(player.getName())){
                    for(Map<?, ?> players : playerList) {
                        String name = (String) players.get("name");
                        String title = (String) players.get("title");
                        String content = (String) players.get("content");
                        if (player.getName().equals(name)) {
                            uniquePlayer = true;
                            Title = title;
                            Contentent = content;
                            break;
                        }
                    }
                    if (!uniquePlayer) {
                        Title = defaultTitle;
                        Contentent = defaultContent;
                    }
                    player.sendRawMessage(Title);
                    player.sendRawMessage(Contentent);
                }
            }
        }
    }
}
