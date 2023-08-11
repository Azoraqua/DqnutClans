package dev.idqnutlikeit.clans.util.resolvers.parameter;

import dev.idqnutlikeit.clans.ClanPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class ParameterResolvers {
  private ParameterResolvers() {
  }

  @Contract("_ -> new")
  @NotNull
  public static ClanResolver clan(ClanPlugin plugin) {
    return new ClanResolver(plugin);
  }

  @Contract("_ -> new")
  @NotNull
  public static OfflinePlayerResolver offlinePlayer(ClanPlugin plugin) {
    return new OfflinePlayerResolver(plugin);
  }
}
