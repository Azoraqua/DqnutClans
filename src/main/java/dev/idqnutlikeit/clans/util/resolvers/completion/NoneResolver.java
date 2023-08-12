package dev.idqnutlikeit.clans.util.resolvers.completion;

import me.mattstudios.mf.base.components.CompletionResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public final class NoneResolver implements CompletionResolver {
  @NotNull
  @Override
  public List<String> resolve(@NotNull Object input) {
    return Collections.singletonList("none");
  }
}
