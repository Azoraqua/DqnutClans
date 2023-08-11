package dev.idqnutlikeit.clans;

import dev.idqnutlikeit.clans.util.Utils;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public final class ClanListener implements Listener {
  @NotNull
  private final ClanPlugin plugin;

  @EventHandler(priority = EventPriority.LOW)
  public void onChat(AsyncPlayerChatEvent e) {
    final Player player = e.getPlayer();

    plugin.getClanManager().getClanByPlayer(player).ifPresent(c -> {
      if (player.hasMetadata("IS_CLAN_CHANNEL")) {
        final boolean isClanChannel = player.getMetadata("IS_CLAN_CHANNEL").get(0).asBoolean();

        if (isClanChannel) {
          e.setFormat("[" + c.getName() + "] " + e.getFormat());
          e.getRecipients().removeIf(p -> !c.hasMember(p) && !c.isLeader(p));
        }
      }
    });
  }


  @EventHandler(priority = EventPriority.LOW)
  public void onDamage(EntityDamageByEntityEvent e) {
    final Entity damaged = e.getEntity();
    final Entity damager = e.getDamager();

    if (damaged instanceof Player damagedPlayer && damager instanceof Player damagerPlayer) {
      if (plugin.getClanManager().hasClan(damagedPlayer) && plugin.getClanManager().hasClan(damagerPlayer)) {
        final Clan damagedClan = plugin.getClanManager().getClanByPlayer(damagedPlayer).orElseThrow();
        final Clan damagerClan = plugin.getClanManager().getClanByPlayer(damagerPlayer).orElseThrow();

        if (damagedClan.getId().equals(damagerClan.getId())) {
          e.setCancelled(true);
          Utils.sendMessage(damagerPlayer, Component.text("§cYou cannot harm players of your own clan."));
        }
      }
    }
  }
}
