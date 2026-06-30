package hu.petidev.placeholder;

import hu.petidev.data.PlayerStats;
import java.util.Map;
import java.util.UUID;

/** Defines the stat dimension used to rank players on the leaderboard. */
public enum LeaderboardType {
  KILLS,
  DEATHS,
  KD;

  /**
   * Returns the numeric value of this stat dimension for the given player stats.
   *
   * @param stats the player's stats
   * @return the stat value used for ranking
   */
  double value(PlayerStats stats) {
    return switch (this) {
      case KILLS -> stats.getKills();
      case DEATHS -> stats.getDeaths();
      case KD -> stats.getDeaths() == 0
          ? stats.getKills()
          : (double) stats.getKills() / stats.getDeaths();
    };
  }

  /**
   * Returns the display-formatted value of this stat dimension for the given player stats.
   *
   * @param stats the player's stats
   * @return the formatted stat value
   */
  String format(PlayerStats stats) {
    return switch (this) {
      case KILLS -> String.valueOf(stats.getKills());
      case DEATHS -> String.valueOf(stats.getDeaths());
      case KD -> stats.getKd();
    };
  }

  /**
   * Computes the 1-indexed competition rank of a player within the given stats cache for this
   * stat dimension. Players tied on the stat value share the same rank. A player with no entry
   * in the cache is treated as having zero stats.
   *
   * @param uuid       the player's UUID
   * @param statsCache the in-memory stats cache
   * @return the player's rank, where 1 is the best
   */
  public int rankOf(UUID uuid, Map<UUID, PlayerStats> statsCache) {
    double ownValue = value(statsCache.getOrDefault(uuid, new PlayerStats()));
    long ahead = statsCache.values().stream().filter(s -> value(s) > ownValue).count();
    return (int) ahead + 1;
  }
}
