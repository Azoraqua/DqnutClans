package dev.idqnutlikeit.clans;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@EqualsAndHashCode
public final class Clan {
    private final UUID id;
    private final String name;
    private final OfflinePlayer leader;
    private final Set<OfflinePlayer> members;
    private Location spawnpoint;

    public Clan(UUID id, String name, OfflinePlayer leader, Set<OfflinePlayer> members, Location spawnpoint) {
        this.id = id;
        this.name = name;
        this.leader = leader;
        this.members = members;
        this.spawnpoint = spawnpoint;
    }

    public Clan(UUID id, String name, OfflinePlayer leader, Set<OfflinePlayer> members) {
        this.id = id;
        this.name = name;
        this.leader = leader;
        this.members = members;
    }

    public Clan(UUID id, String name, OfflinePlayer leader) {
        this(id, name, leader, Collections.emptySet());
    }

    public void addMember(OfflinePlayer player) {
        members.add(player);
    }

    @Override
    public String toString() {
        return ClanPlugin.GSON.toJson(this);
    }
}
