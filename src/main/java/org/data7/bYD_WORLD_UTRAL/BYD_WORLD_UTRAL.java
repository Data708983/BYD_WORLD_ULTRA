package org.data7.bYD_WORLD_UTRAL;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.plugin.java.JavaPlugin;

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

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
