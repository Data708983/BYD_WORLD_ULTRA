package org.data7.bYD_WORLD_UTRAL;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.data7.bYD_WORLD_UTRAL.PlayerJoin;
import org.data7.bYD_WORLD_UTRAL.Tpa;

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

        //注册监听
        this.getServer().getPluginManager().registerEvents(new PlayerJoin.PlayerListener(), this);

        Tpa tpa = new Tpa();
        tpa.connect();
        tpa.InitialDB();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
