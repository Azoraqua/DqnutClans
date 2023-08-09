package dev.idqnutlikeit.clans;

import dev.idqnutlikeit.clans.util.Utils;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.annotations.Optional;
import me.mattstudios.mf.base.CommandBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Command("clan")
@Alias({"clan", "c"})
public final class ClanCommand extends CommandBase {

    private final ClanManager clanManager;
    private final Map<Player, Long> disbandConfirmations = new HashMap<>();

    public ClanCommand(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    @Default
    public void help(CommandSender sender) {
        // TODO: Implement.
    }

    @SubCommand("create")
    @Permission("dqnutclans.create")
    @WrongUsage("§cYou must provide a name for a clan.")
    public void create(CommandSender sender, @NotNull String name) {
        if (!(sender instanceof Player leader)) {
            Utils.sendMessage(sender, Component.text("Only players can create clans."));
            return;
        }

        if (clanManager.hasClan(leader)) {
            Utils.sendMessage(sender, Component.text("You are already in a clan."));
            return;
        }

        if (clanManager.getClanByName(name).isPresent()) {
            Utils.sendMessage(sender, Component.text("A clan with that name already exists."));
            return;
        }

        Clan clan = clanManager.createClan(name, leader);
        clanManager.save();

        Utils.sendMessage(sender, Component.text("Clan " + clan.getName() + " created with you as the leader."));
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
        TextComponent msg = Component.text("Clans: ", NamedTextColor.YELLOW);
        final List<String> clanNames = new ArrayList<>(clanManager.getClanNames());

        for (int i = 0; i < clanNames.size(); i++) {
            final String clanName = clanNames.get(i);

            msg = msg.append(Component.text(clanName, NamedTextColor.WHITE)
                    .clickEvent(ClickEvent.runCommand("/clan info " + clanName)));

            if ((i + 1) < clanNames.size()) {
                msg = msg.append(Component.text(", ", NamedTextColor.BLUE));
            }
        }

        Utils.sendMessage(sender, msg);
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
