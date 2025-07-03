package org.data7.bYD_WORLD_UTRAL;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.data7.bYD_WORLD_UTRAL.PlayerJoin;
import org.data7.bYD_WORLD_UTRAL.Tpa;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public final class BYD_WORLD_UTRAL extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getLogger().info("\n ____  _  _  ____        __  __  ____  ____    __    __   \n" +
                "(  _ \\( \\/ )(  _ \\      (  )(  )(_  _)(  _ \\  /__\\  (  )  \n" +
                " ) _ < \\  /  )(_) ) ___  )(__)(   )(   )   / /(__)\\  )(__ \n" +
                "(____/ (__) (____/ (___)(______) (__) (_)\\_)(__)(__)(____)");
        getServer().getLogger().info("BYD_WORLD_UTRAL is enabled!");
        String version = getDescription().getVersion();
        getServer().getLogger().info("Plugin version:" + "\033[1;34m"+ version +"\033[0m");

        //配置文件
        File config = new File(getDataFolder(), "config.yml");
        if (!config.exists()) {
            saveResource("config.yml", false);
        }

        File broadcastFile = new File(getDataFolder(), "broadcast.yml");
        if (!broadcastFile.exists()) {
            saveResource("broadcast.yml", false);
        }
        File tpaFile = new File(getDataFolder(), "tpa.yml");
        if (!tpaFile.exists()) {
            saveResource("tpa.yml", false);
        }

        //注册监听
        this.getServer().getPluginManager().registerEvents(new PlayerJoin.PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new Playerdeath(), this);

        Tpa tpa = new Tpa();
        tpa.connect();
        tpa.InitialDB();

        this.getCommand("suicide").setExecutor(this);
        this.getCommand("tpa").setExecutor(this);
        this.getCommand("tpac").setExecutor(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     *
     * @param sender Source of the command
     * @param command Command which was executed
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        /**
         * 紫砂
         */
        if (label.equalsIgnoreCase("suicide")){
            if ( sender instanceof Player){
                Player player = (Player) sender;
                Component suicideMessage = Component.text("💀")
                        .append(Component.text(player.getName())
                                .color(TextColor.color(255, 255, 0))) // 设置玩家名字为黄色
                        .append(Component.text("紫砂了!"));
                player.getServer().broadcast(suicideMessage);
                // 获取所有在线玩家
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    // 为每个在线玩家播放音效
                    onlinePlayer.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5f, 5f);}
                player.addScoreboardTag("suicide");
                player.setHealth(0);

                return true;
            }
        }

        return false;
    }

}
