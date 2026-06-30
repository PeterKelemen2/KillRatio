package com.crimsonwarpedcraft.playerkillplugin.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PluginConfigTest {

  @Test
  void defaultsToTrue() {
    assertTrue(new PluginConfig().isPvpDeathsOnly());
  }

  @Test
  void acceptsFalse() {
    assertFalse(new PluginConfig(false).isPvpDeathsOnly());
  }

  @Test
  void acceptsTrue() {
    assertTrue(new PluginConfig(true).isPvpDeathsOnly());
  }
}
