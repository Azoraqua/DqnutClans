package dev.idqnutlikeit.clans;

import com.google.common.base.Suppliers;
import me.mattstudios.mf.base.CommandManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.function.Supplier;

public final class ClanPlugin extends JavaPlugin {
    private final Supplier<BukkitAudiences> audience = Suppliers.memoize(() -> BukkitAudiences.create(this));
    private final Supplier<CommandManager> commandManager = Suppliers.memoize(() -> new CommandManager(this));

    @Override
    public void onEnable() {
        super.saveResource("config.yml", false);
        super.saveResource("messages.yml", false);

        commandManager.get().register(new ClanCommand(this));

        // Placeholders for completions.
        commandManager.get().getCompletionHandler().register("#clans", input -> Collections.emptyList()); // TODO: Implement.
    }

    @Override
    public void onDisable() {
        audience.get().close();
    }

    public BukkitAudiences getAudience() {
        return audience.get();
    }
}
