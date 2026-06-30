package hu.petidev.placeholder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hu.petidev.data.PlayerStats;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

/**
 * A configurable PlaceholderAPI expansion that provides leaderboard placeholders.
 * One instance is registered per identifier (e.g. "killed", "kd", "deaths", "kill", "death").
 */
public class LeaderboardExpansion extends PlaceholderExpansion {

  private static final Pattern TOP_PATTERN = Pattern.compile("^top_(\\d+)(?:_(name|value))?$");

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
   * Resolves leaderboard placeholder values. Supports {@code top_<n>} for a list of the top
   * {@code n} players, and {@code top_<n>_name} / {@code top_<n>_value} for the single name or
   * stat value of whichever player is ranked exactly {@code n} (1-indexed). {@code top_name} and
   * {@code top_value} remain as aliases for rank 1.
   *
   * @param player unused — leaderboard queries are not player-specific
   * @param params the placeholder name after the identifier (e.g. {@code "top_2_name"})
   * @return the resolved string, or null if the placeholder is unknown
   */
  @Override
  public String onRequest(OfflinePlayer player, String params) {
    List<Map.Entry<UUID, PlayerStats>> sorted = getSortedEntries();
    if ("top_name".equals(params)) {
      return nameAt(sorted, 1);
    }
    if ("top_value".equals(params)) {
      return valueAt(sorted, 1);
    }
    Matcher matcher = TOP_PATTERN.matcher(params);
    if (!matcher.matches()) {
      return null;
    }
    int rank = Integer.parseInt(matcher.group(1));
    String field = matcher.group(2);
    if (field == null) {
      return formatTopList(sorted, rank);
    }
    return "name".equals(field) ? nameAt(sorted, rank) : valueAt(sorted, rank);
  }

  private String nameAt(List<Map.Entry<UUID, PlayerStats>> sorted, int rank) {
    return rank < 1 || rank > sorted.size()
        ? ""
        : nameResolver.apply(sorted.get(rank - 1).getKey());
  }

  private String valueAt(List<Map.Entry<UUID, PlayerStats>> sorted, int rank) {
    return rank < 1 || rank > sorted.size()
        ? ""
        : type.format(sorted.get(rank - 1).getValue());
  }

  private List<Map.Entry<UUID, PlayerStats>> getSortedEntries() {
    return statsCache.entrySet().stream()
        .sorted((a, b) -> Double.compare(type.value(b.getValue()), type.value(a.getValue())))
        .collect(Collectors.toList());
  }

  private String formatTopList(List<Map.Entry<UUID, PlayerStats>> sorted, int limit) {
    return sorted.stream()
        .limit(limit)
        .map(e -> nameResolver.apply(e.getKey()) + " - " + type.format(e.getValue()))
        .collect(Collectors.joining("\n"));
  }
}
