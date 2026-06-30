package com.crimsonwarpedcraft.playerkillplugin.config;

import com.crimsonwarpedcraft.cwcommons.config.Config;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the plugin configuration loaded from config.yml.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PluginConfig implements Config {

  @JsonProperty("pvp-deaths-only")
  private boolean pvpDeathsOnly = true;

  PluginConfig() {}

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
