package dev.idqnutlikeit.clans.util.resolvers.parameter;

import dev.idqnutlikeit.clans.ClanPlugin;
import lombok.AllArgsConstructor;
import me.mattstudios.mf.base.components.ParameterResolver;
import me.mattstudios.mf.base.components.TypeResult;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public final class ClanResolver implements ParameterResolver {
  @NotNull
  private final ClanPlugin plugin;

  @NotNull
  @Override
  public TypeResult resolve(@NotNull Object argument) {
//    return plugin.getClanManager().getClanByName(String.valueOf(argument))
//      .map((c) -> new TypeResult(c, argument))
//      .orElseGet(() -> new TypeResult(null, argument));

    return new TypeResult(null);
  }
}
