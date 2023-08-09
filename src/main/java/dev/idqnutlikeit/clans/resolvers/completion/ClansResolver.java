package dev.idqnutlikeit.clans.resolvers.completion;

import dev.idqnutlikeit.clans.ClanPlugin;
import lombok.AllArgsConstructor;
import me.mattstudios.mf.base.components.CompletionResolver;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public final class ClansResolver implements CompletionResolver {
    private final ClanPlugin plugin;

    @Override
    public List<String> resolve(Object input) {
        return new ArrayList<>(plugin.getClanManager().getClanNames());
    }
}
