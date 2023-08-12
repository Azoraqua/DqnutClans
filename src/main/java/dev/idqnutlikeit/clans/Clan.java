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

import java.util.*;

@Builder(builderClassName = "Builder")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Clan {
  @NotNull
  private final UUID id;
  @NotNull
  private final String name;
  @NotNull
  private final OfflinePlayer leader;
  @Singular
  @NotNull
  private final Set<OfflinePlayer> members = new HashSet<>();
  @Singular
  @NotNull
  private final Set<OfflinePlayer> bannedMembers = new HashSet<>();
  @Singular
  @NotNull
  private final Set<OfflinePlayer> mutedMembers = new HashSet<>();
  @Nullable
  private Location spawnpoint;

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

  public void addBannedMember(OfflinePlayer player) {
    bannedMembers.add(player);
  }

  public void removeBannedMember(OfflinePlayer player) {
    bannedMembers.remove(player);
  }

  public boolean isBanned(OfflinePlayer player) {
    return bannedMembers.contains(player);
  }

  public Collection<OfflinePlayer> getBannedMembers() {
    return Collections.unmodifiableCollection(bannedMembers);
  }

  public void addMutedMember(OfflinePlayer player) {
    mutedMembers.add(player);
  }

  public void removeMutedMember(OfflinePlayer player) {
    mutedMembers.remove(player);
  }

  public boolean isMuted(OfflinePlayer player) {
    return mutedMembers.contains(player);
  }

  public Collection<OfflinePlayer> getMutedMembers() {
    return Collections.unmodifiableCollection(mutedMembers);
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

    final JsonArray bannedMemberArr = new JsonArray();
    bannedMembers.forEach(m -> bannedMemberArr.add(m.getUniqueId().toString()));
    obj.add("banned_members", bannedMemberArr);

    final JsonArray mutedMemberArr = new JsonArray();
    mutedMembers.forEach(m -> mutedMemberArr.add(m.getUniqueId().toString()));
    obj.add("muted_members", mutedMemberArr);

    return obj;
  }

  @NotNull
  public static Clan fromJson(JsonObject obj) {
    final Clan.Builder builder = new Builder()
      .id(UUID.fromString(obj.get("id").getAsString()))
      .name(obj.get("name").getAsString())
      .leader(Bukkit.getOfflinePlayer(UUID.fromString(obj.get("leader").getAsString())));

    if (obj.has("spawnpoint")) {
      builder.spawnpoint(Utils.parseLocation(obj.get("spawnpoint").getAsString()));
    }

    final Clan clan = builder.build();

    obj.get("members").getAsJsonArray().forEach(m -> {
      clan.members.add(Bukkit.getOfflinePlayer(UUID.fromString(m.getAsString())));
    });

    obj.get("banned_members").getAsJsonArray().forEach(m -> {
      clan.bannedMembers.add(Bukkit.getOfflinePlayer(UUID.fromString(m.getAsString())));
    });

    obj.get("muted_members").getAsJsonArray().forEach(m -> {
      clan.mutedMembers.add(Bukkit.getOfflinePlayer(UUID.fromString(m.getAsString())));
    });

    return clan;
  }

  @lombok.Builder
  @Data
  public static final class Invitation {
    @NotNull
    private final OfflinePlayer player;
    @NotNull
    private final Clan clan;
  }

  @lombok.Builder
  @Data
  public static final class Application {
    @NotNull
    private final OfflinePlayer player;
    @NotNull
    private final Clan clan;
    private boolean accepted;
  }
}
