package org.data7.bYD_WORLD_UTRAL.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.data7.bYD_WORLD_UTRAL.BYD_WORLD_UTRAL;
import org.jetbrains.annotations.NotNull;

public class HolderExpansions extends PlaceholderExpansion {
    private final BYD_WORLD_UTRAL plugin;
    public HolderExpansions(BYD_WORLD_UTRAL plugin) {
        this.plugin = plugin;
    }
    @Override
    public @NotNull String getIdentifier() {
        return "data7";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return String.join(", ", plugin.getDescription().getVersion());
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("placeholder1")) {
            Bukkit.getLogger().info(plugin.getConfig().getString("placeholders.placeholder1", "default1"));
            return plugin.getConfig().getString("placeholders.placeholder1", "default1");
        }

        if (params.equalsIgnoreCase("placeholder2")) {
            Bukkit.getLogger().info(plugin.getConfig().getString("placeholders.placeholder2", "default2"));
            return plugin.getConfig().getString("placeholders.placeholder2", "default2");
        }

        return null;
    }
}
