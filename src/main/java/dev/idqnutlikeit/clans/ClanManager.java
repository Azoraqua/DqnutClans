package dev.idqnutlikeit.clans;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class ClanManager {
    private final ClanPlugin plugin;
    private final Set<Clan> clans = Collections.synchronizedSet(new HashSet<>());

    public Clan createClan(String name, Player leader) {
        final Clan clan = new Clan.Builder(name, leader).build();

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

    public boolean hasClan(String name) {
        return getClanByName(name).isPresent();
    }

    public boolean hasClan(OfflinePlayer player) {
        return getClanByPlayer(player).isPresent();
    }

    @SneakyThrows
    public void save() {
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
    public void load() {
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
