package dev.idqnutlikeit.clans.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class SerializationUtils {
  private SerializationUtils() {
    throw new AssertionError("SerializationUtils should not be constructed.");
  }

  @Contract("_ -> new")
  @NotNull
  public static Location parseLocation(@NotNull String str) {
    if (!str.contains(":")) {
      throw new IllegalArgumentException("Str is not valid.");
    }

    final String[] parts = str.split(":");

    if (parts.length != 4 && parts.length != 6) {
      throw new IllegalArgumentException("Str must be either 4 or 6 parts.");
    }

    final World world = Bukkit.getWorld(parts[0]);
    final double x = Double.parseDouble(parts[1]);
    final double y = Double.parseDouble(parts[2]);
    final double z = Double.parseDouble(parts[3]);

    if (parts.length == 6) {
      final float yaw = Float.parseFloat(parts[4]);
      final float pitch = Float.parseFloat(parts[5]);

      return new Location(world, x, y, z, yaw, pitch);
    } else {
      return new Location(world, x, y, z);
    }
  }

  @Contract("_, _ -> new")
  @NotNull
  public static String stringifyLocation(Location loc, boolean withYawPitch) {
    final String worldName = Objects.requireNonNull(loc.getWorld()).getName();
    final double x = loc.getX();
    final double y = loc.getY();
    final double z = loc.getZ();

    return withYawPitch
      ? String.format("%s:%f:%f:%f:%f:%f", worldName, x, y, z, loc.getYaw(), loc.getPitch())
      : String.format("%s:%f:%f:%f", worldName, x, y, z);
  }
}
