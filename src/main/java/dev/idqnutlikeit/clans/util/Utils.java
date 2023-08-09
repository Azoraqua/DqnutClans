package dev.idqnutlikeit.clans.util;

import dev.idqnutlikeit.clans.ClanPlugin;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Utils {
    private Utils() {
        throw new AssertionError("Utils should not be constructed.");
    }

    public static void sendMessage(@NotNull CommandSender sender, @NotNull ComponentLike message) {
        final BukkitAudiences audience = JavaPlugin.getPlugin(ClanPlugin.class).getAudience();
        final Audience target = sender instanceof Player
                ? audience.player((Player) sender)
                : sender instanceof ConsoleCommandSender
                ? audience.console()
                : audience.sender(sender);

        target.sendMessage(message);
    }
}
