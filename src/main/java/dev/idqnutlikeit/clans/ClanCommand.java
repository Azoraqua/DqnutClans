package dev.idqnutlikeit.clans;

import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("clan")
@Alias({ "clan", "c" })
public final class ClanCommand extends CommandBase {
    @Default
    public void help(CommandSender sender) {
        // TODO: Implement.
    }

    @SubCommand("create")
    @Permission("dqnutclans.create")
    @WrongUsage("§cYou must provide a name for a clan.")
    public void create(CommandSender sender, String name) {
        // TODO: Implement.
    }

    @SubCommand("disband")
    @Permission({"dqnutclans.disband"})
    public void disband(CommandSender sender) {
        // TODO: Implement.
    }

    @SubCommand("disband")
    @Permission("dqnutclans.disband.others")
    @WrongUsage("§cYou must provide the name of a clan.")
    public void disbandOthers(CommandSender sender, String name) {
        // TODO: Implement.
    }

    @SubCommand("list")
    @Permission("dqnutclans.list")
    public void list(CommandSender sender) {
        // TODO: Implement.
    }

    @SubCommand("info")
    @Permission("dqnutclans.info")
    public void info(CommandSender sender) {
        // TODO: Implement.
    }

    @SubCommand("kick")
    @Permission("dqnutclans.kick")
    @WrongUsage("§cYou must provide the name of a member.")
    public void kick(CommandSender sender, @Completion("#players") Player member) {
        // TODO: Implement.
    }

    @SubCommand("ban")
    @Permission("dqnutclans.ban")
    @WrongUsage("§cYou must provide the name of a member.")
    public void ban(CommandSender sender, @Completion("#players") Player member) {
        // TODO: Implement.
    }

    @SubCommand("invite")
    @Permission("dqnutclans.invite")
    @WrongUsage("§cYou must provide the name of a player.")
    public void invite(CommandSender sender, @Completion("#players") Player member) {
        // TODO: Implement.
    }

    @SubCommand("join")
    @Permission("dqnutclans.join")
    @WrongUsage("§cYou must provide the name of a clan.")
    public void join(CommandSender sender, String name) {
        // TODO: Implement.
    }

    @SubCommand("leave")
    @Permission("dqnutclans.leave")
    public void leave(CommandSender sender) {
        // TODO: Implement.
    }

    @SubCommand("accept")
    @Permission("dqnutclans.accept")
    public void accept(CommandSender sender) {
        // TODO: Implement.
    }

    @SubCommand("deny")
    @Permission("dqnutclans.deny")
    public void deny(CommandSender sender) {
        // TODO: Implement.
    }

    @SubCommand("chat")
    @Permission("dqnutclans.chat")
    public void chat(CommandSender sender, @Optional String message) {
        // TODO: Implement.
    }
}
