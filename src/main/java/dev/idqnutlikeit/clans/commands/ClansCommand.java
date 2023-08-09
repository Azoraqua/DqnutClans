package dev.idqnutlikeit.clans.commands;

import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("clans")
public final class ClansCommand extends CommandBase {
    @Default
    public void help(CommandSender sender) {
        ClanCommandDelegate.instance.get().help(sender);
    }

    @SubCommand("create")
    @Permission("dqnutclans.create")
    @WrongUsage("§cYou must provide a name for a clan.")
    public void create(CommandSender sender, String name) {
        ClanCommandDelegate.instance.get().create(sender, name);
    }

    @SubCommand("disband")
    @Permission({"dqnutclans.disband"})
    public void disband(CommandSender sender) {
        ClanCommandDelegate.instance.get().disband(sender);
    }

    @SubCommand("disband")
    @Permission("dqnutclans.disband.others")
    @WrongUsage("§cYou must provide the name of a clan.")
    public void disbandOthers(CommandSender sender, String name) {
        ClanCommandDelegate.instance.get().disbandOthers(sender, name);
    }

    @SubCommand("list")
    @Permission("dqnutclans.list")
    public void list(CommandSender sender) {
        ClanCommandDelegate.instance.get().list(sender);
    }

    @SubCommand("info")
    @Permission("dqnutclans.info")
    public void info(CommandSender sender) {
        ClanCommandDelegate.instance.get().info(sender);
    }

    @SubCommand("kick")
    @Permission("dqnutclans.kick")
    @WrongUsage("§cYou must provide the name of a member.")
    public void kick(CommandSender sender, @Completion("#players") Player member) {
        ClanCommandDelegate.instance.get().kick(sender, member);
    }

    @SubCommand("ban")
    @Permission("dqnutclans.ban")
    @WrongUsage("§cYou must provide the name of a member.")
    public void ban(CommandSender sender, @Completion("#players") Player member) {
        ClanCommandDelegate.instance.get().ban(sender, member);
    }

    @SubCommand("invite")
    @Permission("dqnutclans.invite")
    @WrongUsage("§cYou must provide the name of a player.")
    public void invite(CommandSender sender, @Completion("#players") Player member) {
        ClanCommandDelegate.instance.get().invite(sender, member);
    }

    @SubCommand("join")
    @Permission("dqnutclans.join")
    @WrongUsage("§cYou must provide the name of a clan.")
    public void join(CommandSender sender, String name) {
        ClanCommandDelegate.instance.get().join(sender, name);
    }

    @SubCommand("leave")
    @Permission("dqnutclans.leave")
    public void leave(CommandSender sender) {
        ClanCommandDelegate.instance.get().leave(sender);
    }

    @SubCommand("accept")
    @Permission("dqnutclans.accept")
    public void accept(CommandSender sender) {
        ClanCommandDelegate.instance.get().accept(sender);
    }

    @SubCommand("deny")
    @Permission("dqnutclans.deny")
    public void deny(CommandSender sender) {
        ClanCommandDelegate.instance.get().deny(sender);
    }

    @SubCommand("chat")
    @Permission("dqnutclans.chat")
    public void chat(CommandSender sender, @Optional String message) {
        ClanCommandDelegate.instance.get().chat(sender, message);
    }
}
