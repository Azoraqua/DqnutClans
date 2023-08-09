package dev.idqnutlikeit.clans.util.resolvers.parameter;

import dev.idqnutlikeit.clans.ClanPlugin;
import lombok.AllArgsConstructor;
import me.mattstudios.mf.base.components.ParameterResolver;
import me.mattstudios.mf.base.components.TypeResult;

@AllArgsConstructor
public final class ClanResolver implements ParameterResolver {
    private final ClanPlugin plugin;

    @Override
    public TypeResult resolve(Object argument) {
        return plugin.getClanManager().getClanByName(String.valueOf(argument))
                .map((c) -> new TypeResult(c, argument))
                .orElseGet(() -> new TypeResult(argument));
    }
}
