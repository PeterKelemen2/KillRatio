package hu.petidev.placeholder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hu.petidev.data.PlayerStats;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

/**
 * A configurable PlaceholderAPI expansion that provides leaderboard placeholders.
 * One instance is registered per identifier (e.g. "killed", "kd", "deaths", "kill", "death").
 */
public class LeaderboardExpansion extends PlaceholderExpansion {

  private static final int TOP_SIZE = 10;

  private final String identifier;
  private final LeaderboardType type;
  private final ConcurrentHashMap<UUID, PlayerStats> statsCache;
  private final Function<UUID, String> nameResolver;

  /**
   * Creates a LeaderboardExpansion for the given stat dimension.
   *
   * @param identifier   the PlaceholderAPI identifier (e.g. {@code "killed"})
   * @param type         the stat to rank players by
   * @param statsCache   the in-memory stats cache
   * @param nameResolver resolves a UUID to a player display name
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Intentional shared cache")
  public LeaderboardExpansion(
      String identifier,
      LeaderboardType type,
      ConcurrentHashMap<UUID, PlayerStats> statsCache,
      Function<UUID, String> nameResolver) {
    this.identifier = identifier;
    this.type = type;
    this.statsCache = statsCache;
    this.nameResolver = nameResolver;
  }

  @Override
  public String getIdentifier() {
    return identifier;
  }

  @Override
  public String getAuthor() {
    return "KillRatio";
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
    return identifier.hashCode();
  }

  /**
   * Resolves leaderboard placeholder values.
   *
   * @param player unused — leaderboard queries are not player-specific
   * @param params the placeholder name after the identifier (e.g. {@code "top_10"})
   * @return the resolved string, or null if the placeholder is unknown
   */
  @Override
  public String onRequest(OfflinePlayer player, String params) {
    List<Map.Entry<UUID, PlayerStats>> sorted = getSortedEntries();
    return switch (params) {
      case "top_10" -> formatTopList(sorted);
      case "top_name" -> sorted.isEmpty() ? "" : nameResolver.apply(sorted.get(0).getKey());
      case "top_value" -> sorted.isEmpty() ? "" : formatValue(sorted.get(0).getValue());
      default -> null;
    };
  }

  private List<Map.Entry<UUID, PlayerStats>> getSortedEntries() {
    return statsCache.entrySet().stream()
        .sorted((a, b) -> Double.compare(statValue(b.getValue()), statValue(a.getValue())))
        .collect(Collectors.toList());
  }

  private double statValue(PlayerStats stats) {
    return switch (type) {
      case KILLS -> stats.getKills();
      case DEATHS -> stats.getDeaths();
      case KD -> stats.getDeaths() == 0
          ? stats.getKills()
          : (double) stats.getKills() / stats.getDeaths();
    };
  }

  private String formatValue(PlayerStats stats) {
    return switch (type) {
      case KILLS -> String.valueOf(stats.getKills());
      case DEATHS -> String.valueOf(stats.getDeaths());
      case KD -> stats.getKd();
    };
  }

  private String formatTopList(List<Map.Entry<UUID, PlayerStats>> sorted) {
    return sorted.stream()
        .limit(TOP_SIZE)
        .map(e -> nameResolver.apply(e.getKey()) + " - " + formatValue(e.getValue()))
        .collect(Collectors.joining("\n"));
  }
}
