package hu.petidev.store;

import hu.petidev.data.PlayerStats;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Loads and saves per-player stats to a YAML file in the plugin's data folder.
 */
public class StatsStore {

  private final File statsFile;
  private final FileConfiguration data;

  /**
   * Creates a StatsStore backed by {@code stats.yml} in the given data folder.
   *
   * @param dataFolder the plugin's data folder
   */
  public StatsStore(File dataFolder) {
    this.statsFile = new File(dataFolder, "stats.yml");
    this.data = YamlConfiguration.loadConfiguration(statsFile);
  }

  /**
   * Loads all stored player stats from disk and returns them as a map.
   *
   * @return a map of UUID to PlayerStats for every player with recorded data
   */
  public Map<UUID, PlayerStats> loadAll() {
    Map<UUID, PlayerStats> result = new HashMap<>();
    ConfigurationSection section = data.getConfigurationSection("players");
    if (section == null) {
      return result;
    }
    Set<String> keys = section.getKeys(false);
    for (String key : keys) {
      try {
        UUID uuid = UUID.fromString(key);
        PlayerStats stats = new PlayerStats();
        stats.setKills(data.getInt("players." + key + ".kills", 0));
        stats.setDeaths(data.getInt("players." + key + ".deaths", 0));
        result.put(uuid, stats);
      } catch (IllegalArgumentException ignored) {
        // Skip entries with malformed UUIDs
      }
    }
    return result;
  }

  /**
   * Writes all entries from the given cache to disk, overwriting any previous values.
   *
   * @param cache the in-memory stats cache to persist
   * @throws IOException if the YAML file cannot be written
   */
  public void saveAll(Map<UUID, PlayerStats> cache) throws IOException {
    for (Map.Entry<UUID, PlayerStats> entry : cache.entrySet()) {
      String path = "players." + entry.getKey();
      data.set(path + ".kills", entry.getValue().getKills());
      data.set(path + ".deaths", entry.getValue().getDeaths());
    }
    data.save(statsFile);
  }
}
