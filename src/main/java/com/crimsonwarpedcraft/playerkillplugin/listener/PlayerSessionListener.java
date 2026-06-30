package com.crimsonwarpedcraft.playerkillplugin.listener;

import com.crimsonwarpedcraft.cwcommons.store.Repository;
import com.crimsonwarpedcraft.playerkillplugin.data.PlayerStats;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Loads and saves per-player stats when players join and leave the server.
 */
public class PlayerSessionListener implements Listener {

  private final ConcurrentHashMap<UUID, PlayerStats> statsCache;
  private final Repository<UUID, PlayerStats> repository;

  /**
   * Creates a PlayerSessionListener backed by the given cache and repository.
   *
   * @param statsCache the in-memory stats cache
   * @param repository the persistent stats repository
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Intentional shared references")
  public PlayerSessionListener(
      ConcurrentHashMap<UUID, PlayerStats> statsCache,
      Repository<UUID, PlayerStats> repository) {
    this.statsCache = statsCache;
    this.repository = repository;
  }

  /**
   * Loads the player's stats from the repository into the cache when they join.
   *
   * @param event the join event
   */
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    statsCache.putIfAbsent(uuid, new PlayerStats());
    repository.get(uuid).thenAccept(opt -> {
      if (opt.isPresent()) {
        // Merge: add any kills/deaths accumulated during the async load onto the stored base
        statsCache.merge(uuid, opt.get(), (current, loaded) -> {
          loaded.setKills(loaded.getKills() + current.getKills());
          loaded.setDeaths(loaded.getDeaths() + current.getDeaths());
          return loaded;
        });
      }
    });
  }

  /**
   * Saves the player's stats to the repository and evicts them from the cache when they leave.
   *
   * @param event the quit event
   */
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    PlayerStats stats = statsCache.remove(uuid);
    if (stats != null) {
      repository.put(uuid, stats);
    }
  }
}
