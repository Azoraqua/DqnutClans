package dev.idqnutlikeit.clans.commands;

import com.google.common.base.Suppliers;
import dev.idqnutlikeit.clans.ClanPlugin;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Supplier;

@AllArgsConstructor
public final class ClanCommandDelegate {
    public static final Supplier<ClanCommandDelegate> instance = Suppliers.memoize(() -> new ClanCommandDelegate(JavaPlugin.getPlugin(ClanPlugin.class)));
    private final ClanPlugin plugin;

    public void help(CommandSender sender) {
        plugin.getAudience().sender(sender).sendMessage(Component.text("HELP")); // TODO: Implement /clan help
    }

    public void create(CommandSender sender, String name) {
        plugin.getAudience().sender(sender).sendMessage(Component.text("CREATE")); // TODO: Implement /clan create <name>
    }

    public void disband(CommandSender sender) {
        plugin.getAudience().sender(sender).sendMessage(Component.text("DISBAND")); // TODO: Implement /clan disband
    }

    public void disbandOthers(CommandSender sender, String name) {
        plugin.getAudience().sender(sender).sendMessage(Component.text("DISBAND_OTHERS")); // TODO: Implement /clan disband <name>
    }

    public void list(CommandSender sender) {
        plugin.getAudience().sender(sender).sendMessage(Component.text("LIST")); // TODO: Implement /clan list
    }

    public void info(CommandSender sender) {
        plugin.getAudience().sender(sender).sendMessage(Component.text("INFO")); // TODO: Implement /clan info
    }

    public void kick(CommandSender sender, Player member) {
        plugin.getAudience().sender(sender).sendMessage(Component.text("KICK")); // TODO: Implement /clan kick <member>
    }

    public void ban(CommandSender sender, Player member) {
        plugin.getAudience().sender(sender).sendMessage(Component.text("BAN")); // TODO: Implement /clan ban <member>
    }

    public void invite(CommandSender sender, Player member) {
        plugin.getAudience().sender(sender).sendMessage(Component.text("INVITE")); // TODO: Implement /clan invite <member>
    }

    public void join(CommandSender sender, String name) {
        plugin.getAudience().sender(sender).sendMessage(Component.text("JOIN"));// TODO: Implement /clan join <name>
    }

    public void leave(CommandSender sender) {
        plugin.getAudience().sender(sender).sendMessage(Component.text("LEAVE"));// TODO: Implement /clan leave
    }

    public void accept(CommandSender sender) {
        plugin.getAudience().sender(sender).sendMessage(Component.text("ACCEPT"));// TODO: Implement /clan accept
    }

    public void deny(CommandSender sender) {
        plugin.getAudience().sender(sender).sendMessage(Component.text("DENY"));  // TODO: Implement /clan deny
    }

    public void chat(CommandSender sender, String message) {
        plugin.getAudience().sender(sender).sendMessage(Component.text("CHAT"));// TODO: Implement /clan chat [message]
    }
}
