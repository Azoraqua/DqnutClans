package dev.idqnutlikeit.clans;

import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("clans")
public final class ClanCommand extends CommandBase {
    private final ClanPlugin plugin;

    public ClanCommand(ClanPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    public void base(CommandSender sender) {
        help(); // Perhaps implement differently?
    }

    @SubCommand("help")
    @Permission("dqnutclans.help")
    public void help() {
        // TODO: Implement /clan help
    }

    @SubCommand("create")
    @Permission("dqnutclans.create")
    @WrongUsage("§cYou must provide a name for a clan.")
    public void create(CommandSender sender, String name) {
        // TODO: Implement /clan create <name>
    }

    @SubCommand("disband")
    @Permission({"dqnutclans.disband"})
    public void disband(CommandSender sender) {
        // TODO: Implement /clan disband
    }

    @SubCommand("disband")
    @Permission("dqnutclans.disband.others")
    @WrongUsage("§cYou must provide the name of a clan.")
    public void disbandOthers(CommandSender sender, @CompleteFor("#clans") String name) {
        // TODO: Implement /clan disband <name>
    }

    @SubCommand("list")
    @Permission("dqnutclans.list")
    public void list(CommandSender sender) {
        // TODO: Implement /clan list
    }

    @SubCommand("info")
    @Permission("dqnutclans.info")
    public void info(CommandSender sender) {
        // TODO: Implement /clan info
    }

    @SubCommand("kick")
    @Permission("dqnutclans.kick")
    @WrongUsage("§cYou must provide the name of a member.")
    public void kick(CommandSender sender, @CompleteFor("#players") Player member) {
        // TODO: Implement /clan kick <member>
    }

    @SubCommand("ban")
    @Permission("dqnutclans.ban")
    @WrongUsage("§cYou must provide the name of a member.")
    public void ban(CommandSender sender, @CompleteFor("#players") Player member) {
        // TODO: Implement /clan ban <member>
    }

    @SubCommand("invite")
    @Permission("dqnutclans.invite")
    @WrongUsage("§cYou must provide the name of a player.")
    public void invite(CommandSender sender, @CompleteFor("#players") Player member) {
        // TODO: Implement /clan invite <member>
    }

    @SubCommand("join")
    @Permission("dqnutclans.join")
    @WrongUsage("§cYou must provide the name of a clan.")
    public void join(CommandSender sender, @CompleteFor("#clans") String name) {
        // TODO: Implement /clan join <name>
    }

    @SubCommand("leave")
    @Permission("dqnutclans.leave")
    public void leave(CommandSender sender) {
        // TODO: Implement /clan leave
    }

    @SubCommand("accept")
    @Permission("dqnutclans.accept")
    public void accept(CommandSender sender) {
        // TODO: Implement /clan accept
    }

    @SubCommand("deny")
    @Permission("dqnutclans.deny")
    public void deny(CommandSender sender) {
        // TODO: Implement /clan deny
    }

    @SubCommand("chat")
    @Permission("dqnutclans.chat")
    public void chat(CommandSender sender, @Optional String message) {
        // TODO: Implement /clan chat [message]
    }
}
