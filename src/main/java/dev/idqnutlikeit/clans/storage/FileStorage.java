package dev.idqnutlikeit.clans.storage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.idqnutlikeit.clans.Clan;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public final class FileStorage implements Storage {
  private static final Gson gson = new Gson();
  private final File dataFolder;

  @SneakyThrows
  @Override
  public void save(@NotNull Clan clan) {
    try (FileWriter w = new FileWriter(new File(dataFolder, clan.getId() + ".json"))) {
      gson.toJson(clan.toJson(), w);
    }
  }

  @SneakyThrows
  @Override
  public void delete(@NotNull Clan clan) {
    Files.deleteIfExists(dataFolder.toPath().resolve(clan.getId() + ".json"));
  }

  @SneakyThrows
  @Override
  public @Nullable Clan findById(@NotNull UUID id) {
    try (Stream<Path> s = Files.find(dataFolder.toPath(), 0, (p, a) -> p.startsWith(id.toString()))) {
      final Optional<Path> optPath = s.findFirst();

      if (optPath.isPresent()) {
        try (FileReader r = new FileReader(optPath.get().toFile())) {
          return Clan.fromJson(gson.fromJson(r, JsonObject.class));
        }
      }
    }

    return null;
  }

  /**
   *
   * @apiNote Use this method sparingly it involves a rather costly operation.
   * @param player
   * @return
   */
  @SneakyThrows
  @Override
  public @Nullable Clan findByLeader(@NotNull OfflinePlayer player) {
    try (Stream<Path> s = Files.list(dataFolder.toPath())) {
      for (String fileName : s
        .filter(p -> p.toFile().getName().endsWith(".json"))
        .map(p -> p.toFile().getName())
        .toList()
      ) {
        final UUID id = UUID.fromString(fileName.replace(".json", ""));
        final Clan clan = findById(id);

        if (clan != null && player.equals(clan.getLeader())) {
          return clan;
        }
      }
    }

    return null;
  }

  @SneakyThrows
  @Override
  public @Nullable Clan findByMember(@NotNull OfflinePlayer player) {
    try (Stream<Path> s = Files.list(dataFolder.toPath())) {
      for (String fileName : s
        .filter(p -> p.toFile().getName().endsWith(".json"))
        .map(p -> p.toFile().getName())
        .toList()
      ) {
        final UUID id = UUID.fromString(fileName.replace(".json", ""));
        final Clan clan = findById(id);

        if (clan != null && clan.getMembers().contains(player)) {
          return clan;
        }
      }
    }

    return null;
  }
}
