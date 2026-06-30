package hu.petidev.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class PluginConfigTest {

  @Test
  void defaultsToTrueWhenKeyMissing() {
    assertTrue(new PluginConfig(new YamlConfiguration()).isPvpDeathsOnly());
  }

  @Test
  void readsFalseFromConfig() {
    YamlConfiguration yaml = new YamlConfiguration();
    yaml.set("pvp-deaths-only", false);
    assertFalse(new PluginConfig(yaml).isPvpDeathsOnly());
  }

  @Test
  void readsTrueFromConfig() {
    YamlConfiguration yaml = new YamlConfiguration();
    yaml.set("pvp-deaths-only", true);
    assertTrue(new PluginConfig(yaml).isPvpDeathsOnly());
  }
}
