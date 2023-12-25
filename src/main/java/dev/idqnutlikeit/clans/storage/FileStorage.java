package dev.idqnutlikeit.clans.storage;

import dev.idqnutlikeit.clans.Clan;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

@RequiredArgsConstructor
public final class FileStorage implements Storage {
  private final File dataFolder;

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
