package dev.idqnutlikeit.clans;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.idqnutlikeit.clans.util.Utils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    @NotNull
    private final Cache<OfflinePlayer, Instant> invitations = CacheBuilder.newBuilder()
            .expireAfterWrite(120L, TimeUnit.SECONDS)
            .build();

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

    public Collection<OfflinePlayer> getInvitations() {
        return new HashSet<>(invitations.asMap().keySet());
    }

    public void addInvitation(Player player) {
        invitations.put(player, Instant.now());
    }

    public void removeInvitation(OfflinePlayer player) {
        invitations.invalidate(player);
    }

    public boolean hasInvitation(OfflinePlayer player) {
        return invitations.asMap().containsKey(player);
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

    public static final class Builder {
        private final UUID id;
        private final String name;
        private final OfflinePlayer leader;
        private final Set<OfflinePlayer> members = new HashSet<>();
        private Location spawnpoint;

        public Builder(UUID id, String name, OfflinePlayer leader) {
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
