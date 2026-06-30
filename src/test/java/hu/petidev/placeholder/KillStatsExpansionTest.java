package hu.petidev.placeholder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hu.petidev.data.PlayerStats;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KillStatsExpansionTest {

  private ConcurrentHashMap<UUID, PlayerStats> cache;
  private KillStatsExpansion expansion;
  private OfflinePlayer player;
  private UUID uuid;

  @BeforeEach
  void setup() {
    cache = new ConcurrentHashMap<>();
    expansion = new KillStatsExpansion(cache);
    uuid = UUID.randomUUID();
    player = mock(OfflinePlayer.class);
    when(player.getUniqueId()).thenReturn(uuid);
  }

  @Test
  void returnsKillCount() {
    PlayerStats stats = new PlayerStats();
    stats.setKills(7);
    cache.put(uuid, stats);
    assertEquals("7", expansion.onRequest(player, "kills"));
  }

  @Test
  void returnsDeathCount() {
    PlayerStats stats = new PlayerStats();
    stats.setDeaths(3);
    cache.put(uuid, stats);
    assertEquals("3", expansion.onRequest(player, "deaths"));
  }

  @Test
  void returnsKdRatio() {
    PlayerStats stats = new PlayerStats();
    stats.setKills(3);
    stats.setDeaths(4);
    cache.put(uuid, stats);
    assertEquals("0.75", expansion.onRequest(player, "kd"));
  }

  @Test
  void returnsKillCountWhenDeathsIsZero() {
    PlayerStats stats = new PlayerStats();
    stats.setKills(5);
    cache.put(uuid, stats);
    assertEquals("5", expansion.onRequest(player, "kd"));
  }

  @Test
  void returnsNullForUnknownParam() {
    assertNull(expansion.onRequest(player, "bogus"));
  }

  @Test
  void returnsEmptyStringForNullPlayer() {
    assertEquals("", expansion.onRequest(null, "kills"));
  }
}
