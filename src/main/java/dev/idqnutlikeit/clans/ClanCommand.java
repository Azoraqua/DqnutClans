package dev.idqnutlikeit.clans;

import dev.idqnutlikeit.clans.util.Utils;
import me.mattstudios.mf.annotations.Optional;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
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
        /**
         *       - 'dqnutclans.create'
         *       - 'dqnutclans.disband'
         *       - 'dqnutclans.list'
         *       - 'dqnutclans.info'
         *       - 'dqnutclans.kick'
         *       - 'dqnutclans.ban'
         *       - 'dqnutclans.invite'
         *       - 'dqnutclans.join'
         *       - 'dqnutclans.leave'
         *       - 'dqnutclans.accept'
         *       - 'dqnutclans.deny'
         *       - 'dqnutclans.chat'
         */

        Utils.sendMessage(sender,
                Component.text()
                        .color(NamedTextColor.YELLOW)
                        .append(Component.text("Clan Commands:"))
                        .appendNewline()
                        .append(Component.text("Create §b<name>§e").clickEvent(ClickEvent.suggestCommand("/clans create")))
                        .appendNewline()
                        .append(Component.text("Disband§e").clickEvent(ClickEvent.suggestCommand("/clans disband")))
                        .appendNewline()
                        .append(Component.text("List").clickEvent(ClickEvent.suggestCommand("/clans list")))
                        .appendNewline()
                        .append(Component.text("Info §c[clan]§e").clickEvent(ClickEvent.suggestCommand("/clans info")))
                        .appendNewline()
                        .append(Component.text("Kick §b<player>§e").clickEvent(ClickEvent.suggestCommand("/clans kick")))
                        .appendNewline()
                        .append(Component.text("Ban §b<player>§e").clickEvent(ClickEvent.suggestCommand("/clans ban")))
                        .appendNewline()
                        .append(Component.text("Invite §b<player>§e").clickEvent(ClickEvent.suggestCommand("/clans invite")))
                        .appendNewline()
                        .append(Component.text("Join §b<clan>§e").clickEvent(ClickEvent.suggestCommand("/clans join")))
                        .appendNewline()
                        .append(Component.text("Leave").clickEvent(ClickEvent.suggestCommand("/clans leave")))
                        .appendNewline()
                        .append(Component.text("Accept").clickEvent(ClickEvent.suggestCommand("/clans accept")))
                        .appendNewline()
                        .append(Component.text("Deny").clickEvent(ClickEvent.suggestCommand("/clans deny")))
                        .appendNewline()
                        .append(Component.text("Invites").clickEvent(ClickEvent.suggestCommand("/clans invites")))
                        .appendNewline()
                        .append(Component.text("Chat §c[message]§e").clickEvent(ClickEvent.suggestCommand("/clans chat")))
        );
    }

    @SubCommand("create")
    @Permission("dqnutclans.create")
    @WrongUsage("§cYou must provide a name for a clan.")
    public void create(CommandSender sender, @NotNull String name) {
        if (!(sender instanceof Player leader)) {
            Utils.sendMessage(sender, "Only players can create clans.");
            return;
        }

        if (clanManager.hasClan(leader)) {
            Utils.sendMessage(sender, "You are already in a clan.");
            return;
        }

        if (clanManager.getClanByName(name).isPresent()) {
            Utils.sendMessage(sender, "A clan with that name already exists.");
            return;
        }

        Clan clan = clanManager.createClan(name, leader);
        clanManager.save();

        Utils.sendMessage(sender, "Clan " + clan.getName() + " created with you as the leader.");
    }

    @SubCommand("disband")
    @Permission("dqnutclans.disband")
    public void disband(CommandSender sender) {
        if (!(sender instanceof Player leader)) {
            Utils.sendMessage(sender, "Only players can disband clans.");
            return;
        }

        if (!sender.hasPermission("dqnutclans.disband")) {
            Utils.sendMessage(sender, "§cYou do not have permission to disband your clan.");
            return;
        }

        final java.util.Optional<Clan> optionalClan = clanManager.getClanByPlayer(leader);

        if (optionalClan.isEmpty()) {
            Utils.sendMessage(sender, "§cYou are not in a clan.");
            return;
        }

        final Clan ownClan = optionalClan.get();

        if (!ownClan.getLeader().equals(leader)) {
            Utils.sendMessage(sender, "§cYou are not the leader of this clan.");
            return;
        }

        clanManager.disbandClan(ownClan);
        Utils.sendMessage(sender, "§aYour clan has been disbanded.");
    }

    @SubCommand("list")
    @Permission("dqnutclans.list")
    public void list(CommandSender sender) {
        final List<Clan> clans = new ArrayList<>(clanManager.getClans());

        if (clans.isEmpty()) {
            Utils.sendMessage(sender, "§cThere are no clans currently.");
            return;
        }

        TextComponent msg = Component.text("Clans: ", NamedTextColor.YELLOW);

        for (int i = 0; i < clans.size(); i++) {
            final Clan clan = clans.get(i);

            TextComponent.Builder clanComponent = Component.text();
            clanComponent.append(Component.text(clan.getName(), NamedTextColor.GRAY));
            clanComponent.clickEvent(ClickEvent.runCommand("/clan info " + clan.getName()));
            clanComponent.hoverEvent(HoverEvent.showText(
                    Component.text()
                            .append(Component.text("§eLeader: §7" + clan.getLeader().getName()))
                            .appendNewline()
                            .append(Component.text("§eMembers: §7" + clan.getMembers()
                                    .stream()
                                    .map(OfflinePlayer::getName)
                                    .collect(Collectors.joining("§f,§7 "))))
            ));

            msg = msg.append(clanComponent.build());

            if ((i + 1) < clans.size()) {
                msg = msg.append(Component.text(", ", NamedTextColor.BLUE));
            }
        }

        Utils.sendMessage(sender, msg);
    }

    @SubCommand("info")
    @Permission("dqnutclans.info")
    public void info(CommandSender sender, @Completion("#clans") @Optional @Nullable Clan clan) {
        class Info {
            static void show(CommandSender s, Clan clan) {
                Utils.sendMessage(s, Component.text()
                        .append(Component.text("§eClan information for §9" + clan.getName() + "§e:"))
                        .appendNewline()
                        .append(Component.text("§eLeader: §7" + clan.getLeader().getName()))
                        .appendNewline()
                        .append(Component.text("§eMembers (§7" + clan.getMembers().size() + "§e): §7" + clan.getMembers().stream().map(OfflinePlayer::getName).collect(Collectors.joining("§f,§e "))))
                );
            }
        }

        if (clan == null) { // Own clan.
            if (!(sender instanceof Player player)) {
                Utils.sendMessage(sender, "§cOnly players can use this command.");
                return;
            }

            final java.util.Optional<Clan> optionalClan = clanManager.getClanByPlayer(player);

            if (optionalClan.isEmpty()) {
                Utils.sendMessage(sender, "§cYou are not in a clan.");
                return;
            }

            Info.show(player, optionalClan.get());
        } else { // Other clan.
            if (!sender.hasPermission("dqnutclans.info.others")) {
                Utils.sendMessage(sender, "§cYou do not have permission to view information about other clans.");
                return;
            }

            if (!clanManager.hasClan(clan)) {
                Utils.sendMessage(sender, "§cThe specified clan does not exist.");
                return;
            }

            Info.show(sender, clan);
        }
    }

    @SubCommand("kick")
    @Permission("dqnutclans.kick")
    @WrongUsage("§cYou must provide the name of a member.")
    public void kick(CommandSender sender, @Completion("#players") Player member) {
        if (!(sender instanceof Player kicker)) {
            Utils.sendMessage(sender, "§cOnly players can kick members from a clan.");
            return;
        }

        final java.util.Optional<Clan> optionalClan = clanManager.getClanByPlayer(kicker);

        if (optionalClan.isEmpty()) {
            Utils.sendMessage(sender, "§cYou are not in a clan.");
            return;
        }

        final Clan clan = optionalClan.get();

        if (!clan.isLeader(kicker)) {
            Utils.sendMessage(sender, "§cYou are not the leader of the clan.");
            return;
        }

        if (member.equals(kicker)) {
            Utils.sendMessage(sender, "§cYou cannot kick yourself out of the clan.");
            return;
        }

        if (!clan.hasMember(member)) {
            Utils.sendMessage(sender, "§cThat player is not part of your clan.");
            return;
        }

        clan.removeMember(member);
        Utils.sendMessage(sender, "§aYou have kicked §b" + member.getName() + "§a out of your clan.");

        if (member.isOnline()) {
            Utils.sendMessage(member, "§cYou have been kicked out of your clan.");
        }
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
        if (!(sender instanceof Player inviter)) {
            Utils.sendMessage(sender, Component.text("§cOnly players can invite others to a clan"));
            return;
        }

        final java.util.Optional<Clan> optionalClan = clanManager.getClanByPlayer(inviter);

        if (optionalClan.isEmpty()) {
            Utils.sendMessage(sender, Component.text("§cYou are not in a clan."));
            return;
        }

        final Clan clan = optionalClan.get();

        if (!clan.isLeader(inviter)) {
            Utils.sendMessage(sender, Component.text("§cYou are not the leader of the clan."));
            return;
        }

        if (inviter.equals(member)) {
            Utils.sendMessage(sender, Component.text("§cYou cannot invite yourself to the clan."));
            return;
        }

        if (clanManager.hasClan(member)) {
            Utils.sendMessage(sender, Component.text("§cThat player is already a member of another clan."));
            return;
        }

        clan.addInvitation(member);

        Utils.sendMessage(sender, "§aYou have invited §b" + member.getName() + "§a to your clan.");
        Utils.sendMessage(member, Component.text()
                .append(Component.text("§eYou have been invited to the §b" + clan.getName() + "§e clan."))
                .appendNewline()
                .append(Component.text("§ePlease §aaccept§e or §cdeny§e the request within §92 minutes§e."))
                .appendNewline()
                .appendNewline()
                .append(Component.text("          §a§lACCEPT").clickEvent(ClickEvent.runCommand("/clan accept")))
                .append(Component.text("      "))
                .append(Component.text("          §c§lDENY").clickEvent(ClickEvent.runCommand("/clan deny")))
                .appendNewline());
    }

    @SubCommand("join")
    @Permission("dqnutclans.join")
    @WrongUsage("§cYou must provide the name of a clan.")
    public void join(CommandSender sender, @Completion("#clans") @NotNull Clan clan) {
        if (!clanManager.hasClan(clan)) {
            Utils.sendMessage(sender, "§cThe specified clan does not exist.");
            return;
        }

        Utils.sendMessage(sender, "§eAttempted to join " + clan.getName()); // TODO: Implement.
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

    @SubCommand("invites")
    @Permission("dqnutclans.invites")
    public void invites(CommandSender sender, @Optional @Nullable Clan otherClan) {
        if (otherClan == null) {
            if (!(sender instanceof Player player)) {
                Utils.sendMessage(sender, Component.text("§cOnly players can see the invites of their own clan."));
                return;
            }

            final java.util.Optional<Clan> optionalClan = clanManager.getClanByPlayer(player);

            if (optionalClan.isEmpty()) {
                Utils.sendMessage(sender, Component.text("§cYou are not in a clan."));
                return;
            }

            final Clan clan = optionalClan.get();
            final Collection<OfflinePlayer> invitations = clan.getInvitations();

            Utils.sendMessage(sender, Component.text("§eInvited: §7" + invitations.stream()
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.joining("§f,§7 "))));
        } else {
            if (!clanManager.hasClan(otherClan)) {
                Utils.sendMessage(sender, "§cThe specified clan does not exist.");
                return;
            }

            final Collection<OfflinePlayer> invitations = otherClan.getInvitations();

            Utils.sendMessage(sender, Component.text("§eInvited: §7" + invitations.stream()
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.joining("§f,§7 "))));
        }
    }

    @SubCommand("chat")
    @Permission("dqnutclans.chat")
    public void chat(CommandSender sender, @Optional String message) {
        // TODO: Implement.
    }
}
