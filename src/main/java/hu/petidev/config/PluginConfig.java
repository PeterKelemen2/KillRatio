package hu.petidev.config;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Represents the plugin configuration loaded from config.yml.
 */
public class PluginConfig {

  private final boolean pvpDeathsOnly;

  /**
   * Creates a PluginConfig by reading values from the given Bukkit configuration.
   *
   * @param config the loaded Bukkit file configuration
   */
  public PluginConfig(FileConfiguration config) {
    this.pvpDeathsOnly = config.getBoolean("pvp-deaths-only", true);
  }

  PluginConfig(boolean pvpDeathsOnly) {
    this.pvpDeathsOnly = pvpDeathsOnly;
  }

  /**
   * Returns whether only deaths caused by another player count toward the death statistic.
   *
   * @return true if only PvP deaths count
   */
  public boolean isPvpDeathsOnly() {
    return pvpDeathsOnly;
  }
}
