package dev.idqnutlikeit.clans;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.idqnutlikeit.clans.util.Utils;
import lombok.*;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Clan {
  @NotNull
  private final UUID id;
  @NotNull
  private final String name;
  @NotNull
  private final OfflinePlayer leader;
  @NotNull
  private final Set<OfflinePlayer> members;
  @Nullable
  private Location spawnpoint;

  public Clan(@NotNull UUID id, @NotNull String name, @NotNull OfflinePlayer leader, @NotNull Set<OfflinePlayer> members, @Nullable Location spawnpoint) {
    this.id = id;
    this.name = name;
    this.leader = leader;
    this.members = members;
    this.spawnpoint = spawnpoint;
  }

  public boolean isLeader(@NotNull OfflinePlayer player) {
    return leader.getUniqueId().equals(player.getUniqueId());
  }

  public void addMember(@NotNull OfflinePlayer player) {
    members.add(player);
  }

  public void removeMember(@NotNull OfflinePlayer player) {
    members.remove(player);
  }

  public boolean hasMembers() {
    return !members.isEmpty();
  }

  public boolean hasMember(@NotNull OfflinePlayer player) {
    return members.contains(player);
  }

  public boolean hasSpawnpoint() {
    return spawnpoint != null;
  }

  public void broadcast(@NotNull ComponentLike message, boolean includeLeader) {
    if (includeLeader && leader instanceof Player) {
      Utils.sendMessage((Player) leader, message);
    }

    members.stream()
      .filter(p -> p instanceof Player)
      .map(p -> (Player) p)
      .forEach(m -> Utils.sendMessage(m, message));
  }

  public void broadcast(@NotNull ComponentLike message) {
    broadcast(message, true);
  }

  @NotNull
  public JsonObject toJson() {
    final JsonObject obj = new JsonObject();
    obj.addProperty("id", id.toString());
    obj.addProperty("name", name);
    obj.addProperty("leader", leader.getUniqueId().toString());

    if (this.hasSpawnpoint()) {
      //noinspection DataFlowIssue - hasSpawnpoint does a null-check implicitly
      obj.addProperty("spawnpoint", Utils.stringifyLocation(spawnpoint, true));
    }

    final JsonArray memberArr = new JsonArray();
    members.forEach(m -> memberArr.add(m.getUniqueId().toString()));
    obj.add("members", memberArr);

    return obj;
  }

  @NotNull
  public static Clan fromJson(JsonObject obj) {
    final Clan.Builder builder = new Clan.Builder(
      UUID.fromString(obj.get("id").getAsString()),
      obj.get("name").getAsString(),
      Bukkit.getOfflinePlayer(UUID.fromString(obj.get("leader").getAsString()))
    );

    if (obj.has("spawnpoint")) {
      builder.spawnpoint(Utils.parseLocation(obj.get("spawnpoint").getAsString()));
    }

    final Clan clan = builder.build();

    obj.get("members").getAsJsonArray().forEach(m -> {
      clan.members.add(Bukkit.getOfflinePlayer(UUID.fromString(m.getAsString())));
    });

    return clan;
  }

  @Data
  public static final class Invitation {
    @NotNull private final OfflinePlayer player;
    @NotNull private final Clan clan;
  }

  public static final class Builder {
    @NotNull
    private final UUID id;
    @NotNull
    private final String name;
    @NotNull
    private final OfflinePlayer leader;
    @NotNull
    private final Set<OfflinePlayer> members = new HashSet<>();
    @Nullable
    private Location spawnpoint;

    public Builder(@NotNull UUID id, @NotNull String name, @NotNull OfflinePlayer leader) {
      this.id = id;
      this.name = name;
      this.leader = leader;
    }

    public Builder(String name, OfflinePlayer leader) {
      this(UUID.randomUUID(), name, leader);
    }

    public Builder spawnpoint(Location spawnpoint) {
      this.spawnpoint = spawnpoint;
      return this;
    }

    public Builder member(OfflinePlayer member) {
      this.members.add(member);
      return this;
    }

    public Clan build() {
      return new Clan(id, name, leader, members, spawnpoint);
    }
  }
}
