package dev.idqnutlikeit.clans;

import lombok.AllArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public final class ClanPlaceholder extends PlaceholderExpansion {
    private final ClanPlugin plugin;

    @Override
    public @NotNull String getIdentifier() {
        return "clan";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getName();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.endsWith("_name")) {
            return plugin.getClanManager().getClanByPlayer(player)
                    .map(Clan::getName)
                    .orElse(null);
        }

        return super.onRequest(player, params);
    }
}
