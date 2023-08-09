package dev.idqnutlikeit.clans.util.resolvers.parameter;

import dev.idqnutlikeit.clans.ClanPlugin;

public final class ParameterResolvers {
    private ParameterResolvers() { }

    public static ClanResolver clan(ClanPlugin plugin) {
        return new ClanResolver(plugin);
    }

    public static OfflinePlayerResolver offlinePlayer(ClanPlugin plugin) {
        return new OfflinePlayerResolver(plugin);
    }
}
