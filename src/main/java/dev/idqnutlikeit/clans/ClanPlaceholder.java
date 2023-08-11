package dev.idqnutlikeit.clans;

import lombok.AllArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public final class ClanPlaceholder extends PlaceholderExpansion {
  @NotNull
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
  public @Nullable String onRequest(@NotNull OfflinePlayer player, @NotNull String name) {
    if (plugin.getClanManager().hasClan(player)) {
      final Clan clan = plugin.getClanManager().getClanByPlayer(player).get();

      return switch (name) {
        case "name" -> clan.getName();
        case "member_count" -> String.valueOf(clan.getMembers().size());
        case "leader" -> clan.getLeader().getName();
        case "invitation_count" -> String.valueOf(plugin.getClanManager().getInvitations(clan).size());
        default -> super.onRequest(player, name);
      };
    }

    return super.onRequest(player, name);
  }
}
