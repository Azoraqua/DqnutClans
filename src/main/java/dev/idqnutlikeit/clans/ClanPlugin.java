package dev.idqnutlikeit.clans;

import com.google.common.base.Suppliers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.idqnutlikeit.clans.util.resolvers.completion.CompletionResolvers;
import dev.idqnutlikeit.clans.util.resolvers.parameter.ParameterResolvers;
import lombok.Getter;
import me.catcoder.sidebar.ProtocolSidebar;
import me.catcoder.sidebar.Sidebar;
import me.mattstudios.mf.base.CommandManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;


public final class ClanPlugin extends JavaPlugin {
  @NotNull
  // Constants
  public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
  // End of constants.

  @NotNull
  private final Supplier<BukkitAudiences> audience = Suppliers.memoize(() -> BukkitAudiences.create(this));
  @NotNull
  private final Supplier<CommandManager> commandManager = Suppliers.memoize(() -> new CommandManager(this));
  @NotNull
  private final Supplier<ClanManager> clanManager = Suppliers.memoize(() -> new ClanManager(this));
  @NotNull
  private final Supplier<File> clanDatafolder = Suppliers.memoize(() -> new File(super.getDataFolder(), "clans"));
  @Nullable
  @Getter
  private Sidebar<Component> scoreboard;

  @Override
  public void onEnable() {
//    if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
//      super.getLogger().info("Vault found. Hooking into it.");
//    }

    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      super.getLogger().info("PlaceholderAPI found. Hooking into it.");
      new ClanPlaceholder(this).register();
    }

    {
      super.saveResource("config.yml", false);

      if (getConfig().getBoolean("sidebar.enabled")) {
        scoreboard = ProtocolSidebar.newAdventureSidebar(
          Component.text(Objects.requireNonNull(getConfig().getString("sidebar.title"))),
          this
        );

        for (String line : getConfig().getStringList("sidebar.lines")) {
          if (line.isBlank()) {
            scoreboard.addBlankLine();
          } else {
            if (line.contains("%")) {
              scoreboard.addUpdatableLine((p) ->
                Component.text(line
                  .replace("%clan_name%", getClanManager().getClanByPlayer(p)
                    .map(Clan::getName).get())
                  .replace("%clan_leader%", getClanManager().getClanByPlayer(p)
                    .map(Clan::getLeader)
                    .map(OfflinePlayer::getName)
                    .get())
                  .replace("%clan_member_count%", getClanManager().getClanByPlayer(p)
                    .map(c -> String.valueOf(c.getMembers().size()))
                    .get())
                ));
            } else {
              scoreboard.addLine(Component.text(line));
            }
          }
        }

        if (scoreboard.getLines().stream().anyMatch(l -> l.getTeamName().contains("%"))) {
          scoreboard.updateLinesPeriodically(0, 1000, true);
        }
      }
    }

    {
      // Completion for commands.
      commandManager.get().getCompletionHandler().register("#none", CompletionResolvers.none());
      commandManager.get().getCompletionHandler().register("#clans", CompletionResolvers.clan(this));
      commandManager.get().getCompletionHandler().register("#applications_sub_commands", input -> Arrays.asList("accept", "reject"));

      // Parameters for commands.
      commandManager.get().getParameterHandler().register(OfflinePlayer.class, ParameterResolvers.offlinePlayer(this));
      commandManager.get().getParameterHandler().register(Clan.class, ParameterResolvers.clan(this));

      commandManager.get().register(new ClanCommand(this, clanManager.get()));
    }

    {
      super.getServer().getPluginManager().registerEvents(new ClanListener(this), this);
    }

    {
      clanManager.get().load();
    }

    // Tasks
    Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
      clanManager.get().save();
    }, 20L * 10L, 20L * getConfig().getLong("auto-save-interval", 30L));
  }

  @NotNull
  public BukkitAudiences getAudience() {
    return audience.get();
  }

  @NotNull
  public ClanManager getClanManager() {
    return clanManager.get();
  }

  @NotNull
  public File getClanDatafolder() {
    return clanDatafolder.get();
  }
}
