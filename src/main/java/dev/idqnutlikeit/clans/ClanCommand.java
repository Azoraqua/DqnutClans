package dev.idqnutlikeit.clans;

import dev.idqnutlikeit.clans.util.Utils;
import io.papermc.lib.PaperLib;
import me.mattstudios.mf.annotations.Optional;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Command("clans")
@Alias({"clan", "c"})
public final class ClanCommand extends CommandBase {
  @NotNull
  private final ClanPlugin plugin;

  @NotNull
  private final ClanManager clanManager;
  @NotNull
  private final Map<Player, Long> disbandConfirmations = new HashMap<>();

  public ClanCommand(@NotNull ClanPlugin plugin, @NotNull ClanManager clanManager) {
    this.plugin = plugin;
    this.clanManager = clanManager;
  }

  @Default
  public void help(@NotNull CommandSender sender) {
    Utils.sendMessage(sender,
      Component.text()
        .color(NamedTextColor.YELLOW)
        .append(Component.text("Clan Commands:"))
        .appendNewline()
        .append(Component.text("Create §b<name>§e").clickEvent(ClickEvent.suggestCommand("/clan create")))
        .appendNewline()
        .append(Component.text("Disband§e").clickEvent(ClickEvent.suggestCommand("/clan disband")))
        .appendNewline()
        .append(Component.text("List").clickEvent(ClickEvent.suggestCommand("/clan list")))
        .appendNewline()
        .append(Component.text("Info §c[clan]§e").clickEvent(ClickEvent.suggestCommand("/clan info")))
        .appendNewline()
        .append(Component.text("Kick §b<player>§e").clickEvent(ClickEvent.suggestCommand("/clan kick")))
        .appendNewline()
        .append(Component.text("Ban §b<player>§e").clickEvent(ClickEvent.suggestCommand("/clan ban")))
        .appendNewline()
        .append(Component.text("Unban §b<player>§e").clickEvent(ClickEvent.suggestCommand("/clan unban")))
        .appendNewline()
        .append(Component.text("Mute §b<player>§e").clickEvent(ClickEvent.suggestCommand("/clan mute")))
        .appendNewline()
        .append(Component.text("Unmute §b<player>§e").clickEvent(ClickEvent.suggestCommand("/clan unmute")))
        .appendNewline()
        .append(Component.text("Invite §b<player>§e").clickEvent(ClickEvent.suggestCommand("/clan invite")))
        .appendNewline()
        .append(Component.text("Join §b<clan>§e").clickEvent(ClickEvent.suggestCommand("/clan join")))
        .appendNewline()
        .append(Component.text("Leave").clickEvent(ClickEvent.suggestCommand("/clan leave")))
        .appendNewline()
        .append(Component.text("Accept").clickEvent(ClickEvent.suggestCommand("/clan accept")))
        .appendNewline()
        .append(Component.text("Deny").clickEvent(ClickEvent.suggestCommand("/clan deny")))
        .appendNewline()
        .append(Component.text("Invites").clickEvent(ClickEvent.suggestCommand("/clan invites")))
        .appendNewline()
        .append(Component.text("Chat §c[message]§e").clickEvent(ClickEvent.suggestCommand("/clan chat")))
        .appendNewline()
        .append(Component.text("Set-Spawn").clickEvent(ClickEvent.suggestCommand("/clan setspawn")))
        .appendNewline()
        .append(Component.text("Spawn").clickEvent(ClickEvent.suggestCommand("/clan spawn")))
    );
  }

  @SubCommand("create")
  @Permission("${base.name}.create")
  @WrongUsage("§cYou must provide a name for a clan.")
  public void create(@NotNull CommandSender sender, @NotNull String name) {
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
  @Permission("${base.name}.disband")
  public void disband(@NotNull CommandSender sender) {
    if (!(sender instanceof Player leader)) {
      Utils.sendMessage(sender, "Only players can disband clans.");
      return;
    }

    if (!sender.hasPermission("${base.name}.disband")) {
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
  @Permission("${base.name}.list")
  public void list(@NotNull CommandSender sender) {
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
  @Permission("${base.name}.info")
  public void info(@NotNull CommandSender sender, @Completion("#clans") @Optional @Nullable Clan clan) {
    class Info {
      static void show(CommandSender s, Clan clan) {
        final TextComponent.Builder builder = Component.text()
          .append(Component.text("§eClan information for §9" + clan.getName() + "§e:"))
          .appendNewline()
          .append(Component.text("§eLeader: §7" + clan.getLeader().getName()))
          .appendNewline()
          .append(Component.text("§eMembers (§7" + clan.getMembers().size() + "§e): §7" + clan.getMembers().stream().map(OfflinePlayer::getName).collect(Collectors.joining("§f,§e "))));

        if (clan.hasSpawnpoint()) {
          builder.appendNewline();

          if (s instanceof Player player) {
            builder.append(Component.text(String.format("§eSpawnpoint (§7%d§e): X=§b%d§e, Y=§b%d§e, Z=§b%d§e",
              Math.round(clan.getSpawnpoint().distance(player.getLocation())),
              clan.getSpawnpoint().getBlockX(),
              clan.getSpawnpoint().getBlockY(),
              clan.getSpawnpoint().getBlockZ())));
          }
        }

        if (s instanceof Player player && clan.isLeader(player)) {
          builder.appendNewline();
          builder.append(Component.text("§eBanned Members (§7" + clan.getBannedMembers().size() + "§e): §7" + clan.getBannedMembers().stream().map(OfflinePlayer::getName).collect(Collectors.joining("§f,§e "))));
          builder.appendNewline();
          builder.append(Component.text("§eMuted Members (§7" + clan.getMutedMembers().size() + "§e): §7" + clan.getMutedMembers().stream().map(OfflinePlayer::getName).collect(Collectors.joining("§f,§e "))));
        }

        Utils.sendMessage(s, builder.build());
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
      if (!sender.hasPermission("${base.name}.info.others")) {
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
  @Permission("${base.name}.kick")
  @WrongUsage("§cYou must provide the name of a member.")
  public void kick(@NotNull CommandSender sender, @Completion("#players") @NotNull Player member) {
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
  @Permission("${base.name}.ban")
  @WrongUsage("§cYou must provide the name of a member.")
  public void ban(@NotNull CommandSender sender, @Completion("#players") @NotNull OfflinePlayer member) {
    if (!(sender instanceof Player player)) {
      Utils.sendMessage(sender, Component.text("§cOnly players can use the ban command."));
      return;
    }

    final java.util.Optional<Clan> optionalClan = clanManager.getClanByPlayer(player);

    if (optionalClan.isEmpty()) {
      Utils.sendMessage(sender, Component.text("§cYou do not have a clan."));
      return;
    }

    final Clan clan = optionalClan.get();

    if (!clan.isLeader(player)) {
      Utils.sendMessage(sender, Component.text("§cOnly the clan leader can ban clan members."));
      return;
    }

    if (clan.isBanned(member)) {
      Utils.sendMessage(sender, Component.text("§cThe specified player is already banned from your clan."));
      return;
    }

    clan.removeMember(member);
    clan.addBannedMember(member);
    clanManager.removeInvitation(member);
    clanManager.save();
    Utils.sendMessage(sender, Component.text("§e" + member.getName() + " has been banned from the clan."));

    if (member instanceof Player) { // Is Online
      Utils.sendMessage((Player) member, Component.text("§cYou have been banned from the §b" + clan.getName() + "§e clan."));
    }
  }

  @SubCommand("unban")
  @Permission("${base.name}.unban")
  @WrongUsage("§cYou must provide the name of a member.")
  public void unban(@NotNull CommandSender sender, @Completion("#players") @NotNull OfflinePlayer member) {
    if (!(sender instanceof Player player)) {
      Utils.sendMessage(sender, Component.text("§cOnly players can use the unban command"));
      return;
    }

    final java.util.Optional<Clan> optionalClan = clanManager.getClanByPlayer(player);

    if (optionalClan.isEmpty()) {
      Utils.sendMessage(sender, Component.text("§cYou do not have a clan."));
      return;
    }

    final Clan clan = optionalClan.get();

    if (!clan.isLeader(player)) {
      Utils.sendMessage(sender, Component.text("§cOnly the leader of the clan can unban people."));
      return;
    }

    if (!clan.isBanned(member)) {
      Utils.sendMessage(sender, Component.text("§cThe specified player is not banned from your clan."));
      return;
    }

    clan.removeBannedMember(member);
    clanManager.save();
    Utils.sendMessage(sender, Component.text("§e" + member.getName() + " has been unbanned."));

    if (member instanceof Player) { // Is Online
      Utils.sendMessage(member.getPlayer(), Component.text("§aYou have been unbanned from the §b" + clan.getName() + "§e clan."));
    }
  }

  @SubCommand("mute")
  @Permission("${base.name}.mute")
  @WrongUsage("§cYou must provide the name of a member.")
  public void mute(@NotNull CommandSender sender, @Completion("#players") @NotNull OfflinePlayer member) {
    if (!(sender instanceof Player player)) {
      Utils.sendMessage(sender, Component.text("§cOnly players can use the mute command."));
      return;
    }

    final java.util.Optional<Clan> optionalClan = clanManager.getClanByPlayer(player);

    if (optionalClan.isPresent()) {
      Clan clan = optionalClan.get();

      if (!clan.isLeader(player)) {
        Utils.sendMessage(sender, Component.text("§cOnly the clan leader can mute people."));
        return;
      }

      if (!clan.hasMember(member)) {
        Utils.sendMessage(sender, Component.text("§cYou are not a member of a clan."));
        return;
      }

      clan.addMutedMember(member);
      clanManager.save();
      Utils.sendMessage(sender, Component.text("§b" + member.getName() + "§e has been muted in the clan."));

      if (member instanceof Player) { // Is Online
        Utils.sendMessage((Player) member, Component.text("§cYou have been muted in the §b" + clan.getName() + "§e clan."));
      }
    }
  }

  @SubCommand("unmute")
  @Permission("${base.name}.unmute")
  @WrongUsage("§cYou must provide the name of a member.")
  public void unmute(@NotNull CommandSender sender, @Completion("#players") @NotNull OfflinePlayer member) {
    if (!(sender instanceof Player player)) {
      Utils.sendMessage(sender, Component.text("§cOnly players can use the unmute command"));
      return;
    }

    final java.util.Optional<Clan> optionalClan = clanManager.getClanByPlayer(player);

    if (optionalClan.isEmpty()) {
      Utils.sendMessage(sender, Component.text("§cYou do not have a clan."));
      return;
    }

    final Clan clan = optionalClan.get();

    if (!clan.isLeader(player)) {
      Utils.sendMessage(sender, Component.text("§cOnly the leader of the clan can unmute people."));
      return;
    }

    if (!clan.isMuted(member)) {
      Utils.sendMessage(sender, Component.text("§cThe specified player is not muted in your clan."));
      return;
    }

    clan.removeMutedMember(member);
    clanManager.save();
    Utils.sendMessage(sender, Component.text("§b" + member.getName() + "§e has been unmuted."));

    if (member instanceof Player) { // Is Online
      Utils.sendMessage(member.getPlayer(), Component.text("§aYou have been unmuted from the §b" + clan.getName() + "§eclan."));
    }
  }

  @SubCommand("invite")
  @Permission("${base.name}.invite")
  @WrongUsage("§cYou must provide the name of a player.")
  public void invite(@NotNull CommandSender sender, @Completion("#players") @NotNull Player member) {
    if (!(sender instanceof Player player)) {
      Utils.sendMessage(sender, Component.text("§cOnly players can invite others to a clan"));
      return;
    }

    final java.util.Optional<Clan> optionalClan = clanManager.getClanByPlayer(player);

    if (optionalClan.isEmpty()) {
      Utils.sendMessage(sender, Component.text("§cYou are not in a clan."));
      return;
    }

    final Clan clan = optionalClan.get();

    if (!clan.isLeader(player)) {
      Utils.sendMessage(sender, Component.text("§cYou are not the leader of the clan."));
      return;
    }

    if (player.equals(member)) {
      Utils.sendMessage(sender, Component.text("§cYou cannot invite yourself to the clan."));
      return;
    }

    if (clanManager.hasClan(member)) {
      Utils.sendMessage(sender, Component.text("§cThat player is already a member of another clan."));
      return;
    }

    if (clan.isBanned(member)) {
      Utils.sendMessage(sender, "§cYou cannot invite a banned clan member.");
      return;
    }

    clanManager.addInvitation(member, clan);

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

  @SubCommand("leave")
  @Permission("${base.name}.leave")
  public void leave(@NotNull CommandSender sender) {
    if (!(sender instanceof Player player)) {
      Utils.sendMessage(sender, Component.text("§cOnly players can leave a clan."));
      return;
    }

    final java.util.Optional<Clan> optionalClan = clanManager.getClanByPlayer(player);

    if (optionalClan.isEmpty()) {
      Utils.sendMessage(sender, Component.text("§cYou are not a member of a clan."));
      return;
    }

    Clan clan = optionalClan.get();

    if (clan.isLeader(player)) {
      Utils.sendMessage(sender, Component.text("§cYou are the leader of the clan. Use §b/clan disband§c to disband the clan."));
      return;
    }

    clan.removeMember(player);
    clanManager.save();

    Utils.sendMessage(sender, Component.text("§eYou have left §b" + clan.getName() + "§e clan."));
    clan.broadcast(Component.text("§b" + player.getName() + "§e has left the clan."));
  }

  @SubCommand("accept")
  @Permission("${base.name}.accept")
  public void accept(@NotNull CommandSender sender) {
    if (!(sender instanceof Player player)) {
      Utils.sendMessage(sender, Component.text("§cOnly players can accept clan invitations."));
      return;
    }

    if (clanManager.hasClan(player)) {
      Utils.sendMessage(sender, Component.text("§cYou are already in a clan."));
      return;
    }

    clanManager.getInvitation(player).ifPresentOrElse((i) -> {
      final Clan clan = i.getClan();

      if (clan.isBanned(player)) {
        Utils.sendMessage(player, "§cYou cannot accept the invitation of §b" + clan.getName() + "§e because you are banned from it.");
        return;
      }

      clan.addMember(player);
      clanManager.removeInvitation(player);
      clanManager.save();

      Utils.sendMessage(sender, "§aYou have accepted the invitation of §b" + clan.getName() + "§e.");

      if (clan.getLeader() instanceof Player) /* Leader Is Online */ {
        Utils.sendMessage((Player) clan.getLeader(), Component.text("§eYour invitation to §b" + player.getName() + "§e has been accepted."));
      }
    }, () -> {
      Utils.sendMessage(sender, "§cYou did not get an invitation.");
    });
  }


  @SubCommand("deny")
  @Permission("${base.name}.deny")
  public void deny(@NotNull CommandSender sender) {
    if (!(sender instanceof Player player)) {
      Utils.sendMessage(sender, Component.text("§cOnly players can deny clan invitations."));
      return;
    }

    if (clanManager.hasClan(player)) {
      Utils.sendMessage(sender, Component.text("§cYou are already in a clan."));
      return;
    }

    clanManager.getInvitation(player).ifPresentOrElse((i) -> {
      final Clan clan = i.getClan();

      clanManager.removeInvitation(player);
      clanManager.save();

      Utils.sendMessage(sender, "§aYou have denied the invitation of §b" + clan.getName() + "§e.");

      if (clan.getLeader() instanceof Player) /* Leader Is Online */ {
        Utils.sendMessage((Player) clan.getLeader(), Component.text("§eYour invitation to §b" + player.getName() + "§e has been denied."));
      }
    }, () -> {
      Utils.sendMessage(sender, "§cYou did not get an invitation.");
    });
  }

  @SubCommand("invites")
  @Permission("${base.name}.invites")
  public void invites(@NotNull CommandSender sender) {
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
    final Collection<Clan.Invitation> invitations = clanManager.getInvitations(clan);

    if (invitations.isEmpty()) {
      Utils.sendMessage(sender, Component.text("§cThere are no invitations currently."));
      return;
    }

    Utils.sendMessage(sender, Component.text("§eInvited: §7").append(Component.text(invitations.stream()
      .map(Clan.Invitation::getPlayer)
      .map(OfflinePlayer::getName)
      .collect(Collectors.joining("§f,§7 ")))));
  }

  @SubCommand("chat")
  @Permission("${base.name}.chat")
  public void chat(@NotNull CommandSender sender) {
    if (!(sender instanceof Player player)) {
      Utils.sendMessage(sender, Component.text("§cOnly players can use clan chats."));
      return;
    }

    final java.util.Optional<Clan> optionalClan = clanManager.getClanByPlayer(player);

    if (optionalClan.isEmpty()) {
      Utils.sendMessage(sender, Component.text("You are not a member of a clan!."));
      return;
    }

    final boolean isClanChannel = player.hasMetadata("IS_CLAN_CHANNEL") && player.getMetadata("IS_CLAN_CHANNEL").get(0).asBoolean();
    final boolean toggledClanChannel = !isClanChannel;

    player.setMetadata("IS_CLAN_CHANNEL", new FixedMetadataValue(plugin, toggledClanChannel));
    player.saveData();
    Utils.sendMessage(player, Component.text("§eClan chat is " + (toggledClanChannel ? "enabled" : "disabled")));
  }

  @SubCommand("setspawn")
  @Permission("${base.name}.setspawn")
  public void setSpawn(@NotNull CommandSender sender, @Completion("#none") @Optional String arg0) {
    if (!(sender instanceof Player player)) {
      Utils.sendMessage(sender, Component.text("§cOnly players can set clan spawns."));
      return;
    }

    final java.util.Optional<Clan> optionalClan = clanManager.getClanByPlayer(player);

    if (optionalClan.isEmpty()) {
      Utils.sendMessage(sender, Component.text("You are not a member of a clan!."));
      return;
    }

    final Clan clan = optionalClan.get();
    final Location spawnpoint = player.getLocation();

    if (!spawnpoint.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
      Utils.sendMessage(sender, Component.text("§cClan spawnpoint is potentially unsafe."));
      return;
    }

    clan.setSpawnpoint("none".equalsIgnoreCase(arg0) ? null : spawnpoint);
    clanManager.save();

    Utils.sendMessage(sender, Component.text("§aYou have set your clan's spawnpoint to your location."));
  }

  @SubCommand("spawn")
  @Permission("${base.name}.spawn")
  public void spawn(@NotNull CommandSender sender) {
    if (!(sender instanceof Player player)) {
      Utils.sendMessage(sender, Component.text("§cOnly players can teleport to clan spawns."));
      return;
    }

    final java.util.Optional<Clan> optionalClan = clanManager.getClanByPlayer(player);

    if (optionalClan.isEmpty()) {
      Utils.sendMessage(sender, Component.text("You are not a member of a clan!."));
      return;
    }

    final Clan clan = optionalClan.get();

    if (!clan.hasSpawnpoint()) {
      Utils.sendMessage(sender, Component.text("§cYour clan has no spawnpoint set."));
      return;
    }

    final Location spawnpoint = clan.getSpawnpoint();

    PaperLib.teleportAsync(player, spawnpoint);
    Utils.sendMessage(sender, Component.text("§eYou have been teleported to your clan's spawnpoint."));
  }
}
