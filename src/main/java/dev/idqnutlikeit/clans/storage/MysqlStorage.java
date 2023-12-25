package dev.idqnutlikeit.clans.storage;

import com.zaxxer.hikari.HikariDataSource;
import dev.idqnutlikeit.clans.Clan;
import lombok.*;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@RequiredArgsConstructor
public final class MysqlStorage extends DbStorage {
  private final HikariDataSource db;

  @Override
  public void save(@NotNull Clan clan) {

  }

  @Override
  public void delete(@NotNull Clan clan) {

  }

  @Override
  public @Nullable Clan findById(@NotNull UUID id) {
    return null;
  }

  @Override
  public @Nullable Clan findByLeader(@NotNull OfflinePlayer player) {
    return null;
  }

  @Override
  public @Nullable Clan findByMember(@NotNull OfflinePlayer player) {
    return null;
  }
}
