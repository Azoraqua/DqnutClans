package dev.idqnutlikeit.clans;

import com.google.common.base.Suppliers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.idqnutlikeit.clans.util.resolvers.parameter.ParameterResolvers;
import me.mattstudios.mf.base.CommandManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.function.Supplier;


public final class ClanPlugin extends JavaPlugin {
    // Constants
    public static final Gson GSON = new GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .create();
    // End of constants.

    private final Supplier<BukkitAudiences> audience = Suppliers.memoize(() -> BukkitAudiences.create(this));
    private final Supplier<CommandManager> commandManager = Suppliers.memoize(() -> new CommandManager(this));
    private final Supplier<ClanManager> clanManager = Suppliers.memoize(() -> new ClanManager(this));
    private final Supplier<File> clanDatafolder = Suppliers.memoize(() -> new File(super.getDataFolder(), "clans"));

    @Override
    public void onEnable() {
        super.saveResource("config.yml", false);
        super.saveResource("messages.yml", false);

        commandManager.get().register(new ClanCommand());

        // Completion for commands.
//        commandManager.get().getCompletionHandler().register("#clans", CompletionResolvers.clan(this));

        // Parameters for commands.
        commandManager.get().getParameterHandler().register(OfflinePlayer.class, ParameterResolvers.offlinePlayer(this));
        commandManager.get().getParameterHandler().register(Clan.class, ParameterResolvers.clan(this));

        clanManager.get().load();

        // Tasks
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            clanManager.get().save();
        }, 20L * 10L, 20L * getConfig().getLong("auto-save-interval", 30L));
    }


    @Override
    public void onDisable() {
        clanManager.get().save();
        audience.get().close();
    }

    public BukkitAudiences getAudience() {
        return audience.get();
    }

    public ClanManager getClanManager() {
        return clanManager.get();
    }

    public File getClanDatafolder() {
        return clanDatafolder.get();
    }
}
