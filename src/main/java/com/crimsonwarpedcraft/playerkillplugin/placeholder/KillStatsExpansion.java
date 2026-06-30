package com.crimsonwarpedcraft.playerkillplugin.placeholder;

import com.crimsonwarpedcraft.playerkillplugin.data.PlayerStats;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

/**
 * Provides {@code %personal_kills%}, {@code %personal_deaths%}, and {@code %personal_kd%}
 * placeholders via PlaceholderAPI.
 */
public class KillStatsExpansion extends PlaceholderExpansion {

  private final ConcurrentHashMap<UUID, PlayerStats> statsCache;

  /**
   * Creates a KillStatsExpansion backed by the given stats cache.
   *
   * @param statsCache the in-memory stats cache to read from
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Intentional shared cache")
  public KillStatsExpansion(ConcurrentHashMap<UUID, PlayerStats> statsCache) {
    this.statsCache = statsCache;
  }

  @Override
  public String getIdentifier() {
    return "personal";
  }

  @Override
  public String getAuthor() {
    return "PlayerKillPlugin";
  }

  @Override
  public String getVersion() {
    return "1.0";
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public int hashCode() {
    return getIdentifier().hashCode();
  }

  /**
   * Resolves placeholder values for the requesting player.
   *
   * @param player the player requesting the placeholder (may be offline)
   * @param params the placeholder name after the identifier (e.g. "kills")
   * @return the resolved string, or null if the placeholder is unknown
   */
  @Override
  public String onRequest(OfflinePlayer player, String params) {
    if (player == null) {
      return "";
    }
    PlayerStats stats = statsCache.getOrDefault(player.getUniqueId(), new PlayerStats());
    return switch (params) {
      case "kills" -> String.valueOf(stats.getKills());
      case "deaths" -> String.valueOf(stats.getDeaths());
      case "kd" -> stats.getKd();
      default -> null;
    };
  }
}
