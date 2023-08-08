package dev.idqnutlikeit.clans;

import com.google.common.base.Suppliers;
import me.mattstudios.mf.base.CommandManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Supplier;

public final class ClanPlugin extends JavaPlugin {
    private final Supplier<BukkitAudiences> audience = Suppliers.memoize(() -> BukkitAudiences.create(this));
    private final Supplier<CommandManager> commandManager = Suppliers.memoize(() -> new CommandManager(this));
    private final Supplier<ClanManager> clanManager = Suppliers.memoize(() -> new ClanManager(this));
    private final Supplier<File> clanDatafolder = Suppliers.memoize(() -> new File(super.getDataFolder(), "clans"));

    @Override
    public void onEnable() {
        super.saveResource("config.yml", false);
        super.saveResource("messages.yml", false);
        clanManager.get().load();

        // Useless code but funny - Dqnut :D

        System.out.println("Welcome by DqnutClans");
        System.out.println("Thanks for using our plugin");


        commandManager.get().register(new ClanCommand(this));

        // Placeholders for completions.
        commandManager.get().getCompletionHandler().register("#clans", input -> new ArrayList<>(clanManager.get().getClanNames()));

        // Tasks
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            clanManager.get().save();
        }, 0L, 20L * 10);
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
