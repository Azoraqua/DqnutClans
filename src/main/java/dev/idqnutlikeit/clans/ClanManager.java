package dev.idqnutlikeit.clans;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class ClanManager {
  @NotNull
  private final ClanPlugin plugin;

  @NotNull
  private final Set<Clan> clans = Collections.synchronizedSet(new HashSet<>());
  @NotNull
  private final Cache<Clan.Invitation, Instant> invitations = CacheBuilder.newBuilder()
    .expireAfterWrite(Duration.ofSeconds(120))
    .build();

  @Contract("_, _ -> new")
  @NotNull
  public Clan createClan(@NotNull String name, @NotNull Player leader) {
    final Clan clan = new Clan.Builder()
      .id(UUID.randomUUID())
      .name(name)
      .leader(leader)
      .build();

    clans.add(clan);
    return clan;
  }

  @SneakyThrows
  public void disbandClan(@NotNull Clan clan) {
    clans.removeIf((c) -> c.getId() == clan.getId());
    invitations.invalidateAll(Arrays.asList(invitations.asMap().keySet().stream().filter(i -> i.getClan().equals(clan)).toArray()));
    Files.delete(plugin.getClanDatafolder().toPath().resolve(clan.getId() + ".json"));
  }

  @NotNull
  public Collection<Clan> getClans() {
    return Collections.unmodifiableSet(clans);
  }

  @NotNull
  public Collection<String> getClanNames() {
    return clans.stream().map(Clan::getName).collect(Collectors.toUnmodifiableSet());
  }

  @NotNull
  public Optional<Clan> getClanByName(@NotNull String name) {
    return clans.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst();
  }

  @NotNull
  public Optional<Clan> getClanByPlayer(@NotNull OfflinePlayer player) {
    return clans.stream().filter(c -> c.getLeader().equals(player) || c.getMembers().contains(player)).findFirst();
  }

  public boolean hasClan(@NotNull Clan clan) {
    return clans.contains(clan);
  }

  public boolean hasClan(@NotNull String name) {
    return getClanByName(name).isPresent();
  }

  public boolean hasClan(@NotNull OfflinePlayer player, Boolean onlyLeader) {
    if (onlyLeader == null) {
      return getClanByPlayer(player).isPresent(); // Either leader or member of clan.
    }

    if (onlyLeader) {
      return clans.stream().anyMatch(c -> c.isLeader(player));
    } else {
      return clans.stream().anyMatch(c -> c.hasMember(player));
    }
  }

  public boolean hasClan(@NotNull OfflinePlayer player) {
    return hasClan(player, null);
  }

  public void addInvitation(OfflinePlayer player, Clan clan) {
    invitations.put(new Clan.Invitation(player, clan), Instant.now());
  }

  public void removeInvitation(OfflinePlayer player) {
    invitations.asMap().keySet().stream()
      .filter(i -> i.getPlayer().equals(player))
      .forEach(invitations::invalidate);
  }

  public boolean hasInvitation(@NotNull OfflinePlayer player) {
    return getInvitation(player).isPresent();
  }

  public Optional<Clan.Invitation> getInvitation(@NotNull OfflinePlayer player) {
    return invitations.asMap().keySet().stream().filter(i -> i.getPlayer().equals(player))
      .findFirst();
  }

  public Collection<Clan.Invitation> getInvitations() {
    return Collections.unmodifiableCollection(invitations.asMap().keySet());
  }

  public Collection<Clan.Invitation> getInvitations(@NotNull Clan clan) {
    return invitations.asMap().keySet().stream()
      .filter(i -> i.getClan().equals(clan))
      .collect(Collectors.toSet());
  }

  public void cleanup() {
    clans.clear();
    invitations.invalidateAll();
  }

  @SneakyThrows
  public synchronized void save() {
    int numClans = 0;
    int numFailed = 0;

    for (Clan c : clans) {
      final File dataFile = new File(plugin.getClanDatafolder(), c.getId().toString() + ".json");
      numClans++;

      if (!dataFile.exists()) {
        dataFile.createNewFile();
      }

      try (FileWriter w = new FileWriter(dataFile)) {
        ClanPlugin.GSON.toJson(c.toJson(), w);
      } catch (IOException ex) {
        numFailed++;
        plugin.getLogger().warning("Failed to save clan (" + c.getId() + "):");
      }
    }

    plugin.getLogger().info("Saved " + numClans + " clans (" + numFailed + " failed)");
  }

  @SneakyThrows
  public synchronized void load() {
    if (!plugin.getClanDatafolder().exists()) {
      plugin.getClanDatafolder().mkdirs();
    }

    int numClans = 0;
    int numFailed = 0;

    for (File dataFile : Objects.requireNonNull(plugin.getClanDatafolder().listFiles((d, n) -> n.endsWith(".json")))) {
      numClans++;

      if (dataFile.length() == 0) {
        continue;
      }

      try (FileReader r = new FileReader(dataFile)) {
        final Clan clan = Clan.fromJson(ClanPlugin.GSON.fromJson(r, JsonObject.class));

        if (clans.contains(clan)) {
          // Replacing the clan.
          clans.remove(clan);
          clans.add(clan);
        } else {
          clans.add(clan);
        }
      } catch (IOException ex) {
        numFailed++;
        plugin.getLogger().warning("Failed to load clan (" + dataFile.getName().replace(".json", "") + "):");
      }
    }

    plugin.getLogger().info("Loaded " + numClans + " clans (" + numFailed + " failed)");
  }
}
