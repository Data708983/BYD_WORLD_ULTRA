package org.data7.bYD_WORLD_ULTRA.PAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.data7.bYD_WORLD_ULTRA.BYD_WORLD_ULTRA;
import org.jetbrains.annotations.NotNull;

public class PAPI extends PlaceholderExpansion {
    private final BYD_WORLD_ULTRA plugin; //

    public PAPI(BYD_WORLD_ULTRA plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "BWU";
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        return null;
    }
}
