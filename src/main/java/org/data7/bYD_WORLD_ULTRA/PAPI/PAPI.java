package org.data7.bYD_WORLD_ULTRA.PAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PAPI extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "byd";
    }

    @Override
    public @NotNull String getAuthor() {
        return "data7";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("smile")) {
            return "😀";
        }

        if (params.equalsIgnoreCase("sad")) {
            return "😭";
        }
        return null;
    }
}
