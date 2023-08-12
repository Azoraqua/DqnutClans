package dev.idqnutlikeit.clans.util.resolvers.parameter;

import dev.idqnutlikeit.clans.ClanPlugin;
import lombok.AllArgsConstructor;
import me.mattstudios.mf.base.components.ParameterResolver;
import me.mattstudios.mf.base.components.TypeResult;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@AllArgsConstructor
public final class OfflinePlayerResolver implements ParameterResolver {
  @NotNull
  private ClanPlugin plugin;

  @NotNull
  @Override
  public TypeResult resolve(@NotNull Object argument) {
    return Arrays.stream(Bukkit.getOfflinePlayers())
      .filter(p -> String.valueOf(argument).equalsIgnoreCase(p.getName()))
      .findFirst().map(p -> new TypeResult(p, argument))
      .orElseGet(() -> new TypeResult(argument));

  }
}
