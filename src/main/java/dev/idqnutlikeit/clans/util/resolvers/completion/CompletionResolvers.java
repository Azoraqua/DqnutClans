package dev.idqnutlikeit.clans.util.resolvers.completion;

import dev.idqnutlikeit.clans.ClanPlugin;

public final class CompletionResolvers {
    private CompletionResolvers() { }

    public static ClansResolver clan(ClanPlugin plugin) {
        return new ClansResolver(plugin);
    }
}
