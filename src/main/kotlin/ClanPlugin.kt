package dev.idqnutlikeit.clans

class ClanPlugin : JavaPlugin() {
  // End of constants.
  private val audience: java.util.function.Supplier<net.kyori.adventure.platform.bukkit.BukkitAudiences> =
    Suppliers.memoize<net.kyori.adventure.platform.bukkit.BukkitAudiences>(
      com.google.common.base.Supplier<net.kyori.adventure.platform.bukkit.BukkitAudiences> {
        net.kyori.adventure.platform.bukkit.BukkitAudiences.create(
          this
        )
      })
  private val commandManager: java.util.function.Supplier<CommandManager> =
    Suppliers.memoize<CommandManager>(com.google.common.base.Supplier<CommandManager> {
      CommandManager(
        this
      )
    })
  private val clanManager: java.util.function.Supplier<ClanManager> =
    Suppliers.memoize<ClanManager>(com.google.common.base.Supplier<ClanManager> {
      ClanManager(
        this
      )
    })
  private val clanDatafolder: java.util.function.Supplier<File> =
    Suppliers.memoize<File>(com.google.common.base.Supplier<File> {
      File(
        super.getDataFolder(),
        "clans"
      )
    })

  override fun onEnable() {
    if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
      super.getLogger().info("Vault found. Hooking into it.")
    }
    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      super.getLogger().info("PlaceholderAPI found. Hooking into it.")
      ClanPlaceholder(this).register()
    }
    run {
      super.saveResource("config.yml", false)
      super.saveResource("messages.yml", false)
    }
    run {

      // Completion for commands.
      commandManager.get().getCompletionHandler().register("#none", CompletionResolvers.none())
      commandManager.get().getCompletionHandler().register("#clans", CompletionResolvers.clan(this))

      // Parameters for commands.
      commandManager.get().getParameterHandler()
        .register(OfflinePlayer::class.java, ParameterResolvers.offlinePlayer(this))
      commandManager.get().getParameterHandler().register(Clan::class.java, ParameterResolvers.clan(this))
      commandManager.get().register(ClanCommand(this, clanManager.get()))
    }
    run { super.getServer().getPluginManager().registerEvents(ClanListener(this), this) }
    run { clanManager.get().load() }

    // Tasks
    Bukkit.getScheduler().runTaskTimerAsynchronously(this,
      java.lang.Runnable { clanManager.get().save() }, 20L * 10L, 20L * getConfig().getLong("auto-save-interval", 30L)
    )
  }

  override fun onDisable() {
    clanManager.get().save()
    audience.get().close()
  }

  fun getAudience(): net.kyori.adventure.platform.bukkit.BukkitAudiences {
    return audience.get()
  }

  fun getClanManager(): ClanManager {
    return clanManager.get()
  }

  fun getClanDatafolder(): File {
    return clanDatafolder.get()
  }

  companion object {
    // Constants
    val GSON: Gson = GsonBuilder().setLenient().setPrettyPrinting().create()
  }
}
