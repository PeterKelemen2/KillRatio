package com.crimsonwarpedcraft.playerkillplugin.listener;

import com.crimsonwarpedcraft.playerkillplugin.data.PlayerStats;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Credits kills and deaths to the appropriate players when a player is killed.
 */
public class PlayerKillListener implements Listener {

  private final ConcurrentHashMap<UUID, PlayerStats> statsCache;
  private final boolean pvpDeathsOnly;

  /**
   * Creates a PlayerKillListener that updates the given stats cache.
   *
   * @param statsCache    the in-memory cache to update
   * @param pvpDeathsOnly if true, only deaths caused by another player count toward death stat
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Intentional shared cache")
  public PlayerKillListener(
      ConcurrentHashMap<UUID, PlayerStats> statsCache, boolean pvpDeathsOnly) {
    this.statsCache = statsCache;
    this.pvpDeathsOnly = pvpDeathsOnly;
  }

  /**
   * Credits the killer with a kill and the victim with a death when applicable.
   *
   * @param event the player death event
   */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player victim = event.getEntity();
    Player killer = victim.getKiller();

    if (killer != null) {
      PlayerStats killerStats =
          statsCache.computeIfAbsent(killer.getUniqueId(), id -> new PlayerStats());
      killerStats.setKills(killerStats.getKills() + 1);
    }

    if (!pvpDeathsOnly || killer != null) {
      PlayerStats victimStats =
          statsCache.computeIfAbsent(victim.getUniqueId(), id -> new PlayerStats());
      victimStats.setDeaths(victimStats.getDeaths() + 1);
    }
  }
}
