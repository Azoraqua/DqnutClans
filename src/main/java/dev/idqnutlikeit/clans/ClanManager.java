package dev.idqnutlikeit.clans;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class ClanManager {
    private final ClanPlugin plugin;
    private final Set<Clan> clans = Collections.synchronizedSet(new HashSet<>());

    public Clan createClan(String name, Player leader) {
        final Clan clan = Clan.builder()
                .id(UUID.randomUUID())
                .name(name)
                .leader(leader)
                .build();

        clans.add(clan);
        return clan;
    }

    @SneakyThrows
    public void disbandClan(Clan clan) {
        clans.removeIf((c) -> c.getId() == clan.getId());
        Files.delete(plugin.getClanDatafolder().toPath().resolve(clan.getId().toString() + ".yml"));
    }

    public Collection<Clan> getClans() {
        return Collections.unmodifiableSet(clans);
    }

    public Collection<String> getClanNames() {
        return clans.stream().map(Clan::getName).collect(Collectors.toUnmodifiableSet());
    }

    public Optional<Clan> getClanByName(String name) {
        return clans.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst();
    }

    public Optional<Clan> getClanByPlayer(OfflinePlayer player) {
        return clans.stream().filter(c -> c.getLeader().equals(player) || c.getMembers().contains(player)).findFirst();
    }

    @SneakyThrows
    public void save() {
        for (Clan c : clans) {
            final File dataFile = new File(plugin.getClanDatafolder(), c.getId().toString() + ".yml");

            if (!dataFile.exists()) {
                dataFile.createNewFile();
            }

            final FileConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
            cfg.set("name", c.getName());
            cfg.set("leader", c.getLeader().getUniqueId());
            cfg.set("members", c.getMembers().stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toSet()));
            cfg.set("spawnpoint", c.getSpawnpoint());
            cfg.save(dataFile);
        }
    }

    @SneakyThrows
    public void load() {
        for (File dataFile : Objects.requireNonNull(plugin.getClanDatafolder().listFiles((d, n) -> n.endsWith(".yml")))) {
            final FileConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
            final Clan clan = Clan.builder()
                    .id(UUID.fromString(dataFile.getName().replace(".yml", "")))
                    .name(cfg.getString("name"))
                    .leader(Bukkit.getOfflinePlayer(UUID.fromString(Objects.requireNonNull(cfg.getString("leader")))))
                    .spawnpoint(cfg.getSerializable("spawnpoint", Location.class))
                    .build();

            for (String memberIdStr : Objects.requireNonNull(cfg.getStringList("members"))) {
                final UUID memberId = UUID.fromString(memberIdStr);

                clan.addMember(Bukkit.getOfflinePlayer(memberId));
            }
        }
    }
}
