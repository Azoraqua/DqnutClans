package dev.idqnutlikeit.clans.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.idqnutlikeit.clans.Clan;
import dev.idqnutlikeit.clans.ClanPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;

public interface Storage {

  void save(@NotNull Clan clan);

  void delete(@NotNull Clan clan);

  @Nullable
  Clan findById(@NotNull UUID id);

  @Nullable
  Clan findByLeader(@NotNull OfflinePlayer player);

  @Nullable
  Clan findByMember(@NotNull OfflinePlayer player);

  public static enum Type {
    FILE,
    SQLITE,
    MYSQL,
  }

  public static final class Factory {
    public static FileStorage createFile(File dataFolder) {
      return create(Type.FILE, dataFolder.getPath(), null, null);
    }

    public static SqliteStorage createSqlite(@NotNull String url) {
      return create(Type.SQLITE, url, null, null);
    }

    public static MysqlStorage createMysql(@NotNull String url, @NotNull String username, @NotNull String password) {
      return create(Type.MYSQL, url, username, password);
    }

    public static <T extends Storage> T createFromConfig(ConfigurationSection cs) {
      final Type type = Type.valueOf(cs.getString("type", "file").toUpperCase());
      final String url = cs.getString("url", new File(JavaPlugin.getPlugin(ClanPlugin.class).getDataFolder(), "clans").getPath());
      final String username = type != Type.FILE ? cs.getString("username") : null;
      final String password = type != Type.FILE ? cs.getString("password") : null;

      return create(type, url, username, password);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Storage> T create(@NotNull Type type, @NotNull String url, @Nullable String username, @Nullable String password) {
      return (T) switch (type) {
        case FILE -> new FileStorage(Paths.get(url).toFile());

        case SQLITE -> {
          yield new SqliteStorage(initDB(url, username, password));
        }

        case MYSQL -> {
          yield new MysqlStorage(initDB(url, username, password));
        }
      };
    }

    private static HikariDataSource initDB(@NotNull String url, @Nullable String username, @Nullable String password) {
      return new HikariDataSource(new HikariConfig() {{
        setJdbcUrl(url);

        if (username != null) {
          setUsername(username);
        }

        if (password != null) {
          setPassword(password);
        }
      }});
    }
  }
}
