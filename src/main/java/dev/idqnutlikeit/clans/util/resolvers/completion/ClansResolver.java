package dev.idqnutlikeit.clans.util.resolvers.completion;

import dev.idqnutlikeit.clans.ClanPlugin;
import lombok.AllArgsConstructor;
import me.mattstudios.mf.base.components.CompletionResolver;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public final class ClansResolver implements CompletionResolver {
  @NotNull
  private final ClanPlugin plugin;

  @Contract("_ -> new")
  @NotNull
  @Override
  public List<String> resolve(@NotNull Object input) {
    return new ArrayList<>(/* plugin.getClanManager().getClanNames() */);
  }
}
