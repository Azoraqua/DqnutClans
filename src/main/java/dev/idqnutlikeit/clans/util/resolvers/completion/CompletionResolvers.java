package dev.idqnutlikeit.clans.util.resolvers.completion;

import dev.idqnutlikeit.clans.ClanPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class CompletionResolvers {
  private CompletionResolvers() {
  }

  @Contract("_ -> new")
  @NotNull
  public static ClansResolver clan(@NotNull ClanPlugin plugin) {
    return new ClansResolver(plugin);
  }

  @Contract("-> new")
  @NotNull
  public static NoneResolver none() {
    return new NoneResolver();
  }
}
