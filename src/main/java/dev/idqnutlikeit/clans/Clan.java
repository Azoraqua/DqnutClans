package dev.idqnutlikeit.clans;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.idqnutlikeit.clans.util.MessageUtils;
import dev.idqnutlikeit.clans.util.SerializationUtils;
import lombok.*;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
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
  @NotNull
  private final Cache<OfflinePlayer, Optional<Boolean>> applications = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofHours(48)).build();
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

  public void addBannedMember(@NotNull OfflinePlayer player) {
    bannedMembers.add(player);
  }

  public void removeBannedMember(@NotNull OfflinePlayer player) {
    bannedMembers.remove(player);
  }

  public boolean isBanned(@NotNull OfflinePlayer player) {
    return bannedMembers.contains(player);
  }

  @NotNull
  public Collection<OfflinePlayer> getBannedMembers() {
    return Collections.unmodifiableCollection(bannedMembers);
  }

  public void addMutedMember(@NotNull OfflinePlayer player) {
    mutedMembers.add(player);
  }

  public void removeMutedMember(@NotNull OfflinePlayer player) {
    mutedMembers.remove(player);
  }

  public boolean isMuted(@NotNull OfflinePlayer player) {
    return mutedMembers.contains(player);
  }

  @NotNull
  public Collection<OfflinePlayer> getMutedMembers() {
    return Collections.unmodifiableCollection(mutedMembers);
  }

  public void addApplication(@NotNull OfflinePlayer player, boolean accepted) {
    applications.put(player, Optional.of(accepted));
  }

  public void addApplication(@NotNull OfflinePlayer player) {
    applications.put(player, Optional.empty());
  }

  public boolean hasApplied(@NotNull OfflinePlayer player) {
    return applications.asMap().containsKey(player);
  }

  public boolean isAccepted(@NotNull OfflinePlayer player) {
    return applications.asMap().containsKey(player) && applications.getIfPresent(player).get();
  }

  public boolean isRejected(@NotNull OfflinePlayer player) {
    return applications.asMap().containsKey(player) && !applications.getIfPresent(player).get();
  }

  public void accept(@NotNull OfflinePlayer player) {
    applications.put(player, Optional.of(true));
  }

  public void reject(@NotNull OfflinePlayer player) {
    applications.put(player, Optional.of(false));
  }

  @NotNull
  public Collection<OfflinePlayer> getApplications() {
    return Collections.unmodifiableCollection(applications.asMap().keySet());
  }

  public boolean hasSpawnpoint() {
    return spawnpoint != null;
  }

  public void broadcast(@NotNull ComponentLike message, boolean includeLeader) {
    if (includeLeader && leader instanceof Player) {
      MessageUtils.send((Player) leader, message);
    }

    members.stream()
      .filter(p -> p instanceof Player)
      .map(p -> (Player) p)
      .forEach(m -> MessageUtils.send(m, message));
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
      obj.addProperty("spawnpoint", SerializationUtils.stringifyLocation(spawnpoint, true));
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

    final JsonArray applicationsArr = new JsonArray();
    applications.asMap().forEach((m, a) -> {
      if (a.isPresent()) {
        applicationsArr.add(String.format("%s:%s", m.getUniqueId(), a.get()));
      } else {
        applicationsArr.add(String.format("%s", m.getUniqueId()));
      }
    });
    obj.add("applications", applicationsArr);

    return obj;
  }

  @NotNull
  public static Clan fromJson(JsonObject obj) {
    final Clan.Builder builder = new Builder()
      .id(UUID.fromString(obj.get("id").getAsString()))
      .name(obj.get("name").getAsString())
      .leader(Bukkit.getOfflinePlayer(UUID.fromString(obj.get("leader").getAsString())));

    if (obj.has("spawnpoint")) {
      builder.spawnpoint(SerializationUtils.parseLocation(obj.get("spawnpoint").getAsString()));
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

    obj.get("muted_members").getAsJsonArray().forEach(m -> {
      clan.mutedMembers.add(Bukkit.getOfflinePlayer(UUID.fromString(m.getAsString())));
    });

    obj.get("applications").getAsJsonArray().forEach((e) -> {
      final String[] parts = e.getAsString().split(":");
      final OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(parts[0]));

      if (parts.length == 2) { // Reviewed
        clan.addApplication(player, Boolean.parseBoolean(parts[1]));
      } else {
        clan.addApplication(player);
      }
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
}
