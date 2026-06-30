package hu.petidev.placeholder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hu.petidev.data.PlayerStats;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LeaderboardExpansionTest {

  private ConcurrentHashMap<UUID, PlayerStats> cache;
  private Map<UUID, String> names;

  @BeforeEach
  void setup() {
    cache = new ConcurrentHashMap<>();
    names = new HashMap<>();
  }

  private LeaderboardExpansion expansion(String identifier, LeaderboardType type) {
    return new LeaderboardExpansion(identifier, type, cache,
        uuid -> names.getOrDefault(uuid, "Unknown"));
  }

  private void addPlayer(UUID uuid, String name, int kills, int deaths) {
    names.put(uuid, name);
    PlayerStats stats = new PlayerStats();
    stats.setKills(kills);
    stats.setDeaths(deaths);
    cache.put(uuid, stats);
  }

  @Test
  void top10ReturnsSortedEntriesDescendingByKills() {
    UUID a = UUID.randomUUID();
    addPlayer(a, "Alpha", 10, 2);
    UUID b = UUID.randomUUID();
    addPlayer(b, "Beta", 5, 1);
    UUID c = UUID.randomUUID();
    addPlayer(c, "Gamma", 20, 3);

    String result = expansion("killed", LeaderboardType.KILLS).onRequest(null, "top_10");
    assertNotNull(result);
    String[] lines = result.split("\n");

    assertEquals(3, lines.length);
    assertTrue(lines[0].startsWith("Gamma"));
    assertTrue(lines[1].startsWith("Alpha"));
    assertTrue(lines[2].startsWith("Beta"));
  }

  @Test
  void top10FormatsEntriesAsNameDashValue() {
    UUID a = UUID.randomUUID();
    addPlayer(a, "Steve", 7, 2);

    String result = expansion("killed", LeaderboardType.KILLS).onRequest(null, "top_10");

    assertEquals("Steve - 7", result);
  }

  @Test
  void top10WithFewerThanTenPlayersReturnsAllLines() {
    addPlayer(UUID.randomUUID(), "A", 3, 1);
    addPlayer(UUID.randomUUID(), "B", 1, 2);

    String result = expansion("killed", LeaderboardType.KILLS).onRequest(null, "top_10");
    assertNotNull(result);

    assertEquals(2, result.split("\n").length);
  }

  @Test
  void top10EmptyCacheReturnsEmptyString() {
    assertEquals("", expansion("killed", LeaderboardType.KILLS).onRequest(null, "top_10"));
  }

  @Test
  void top10SortsByKdDescending() {
    UUID a = UUID.randomUUID();
    addPlayer(a, "High", 10, 2);
    UUID b = UUID.randomUUID();
    addPlayer(b, "Low", 10, 5);

    String result = expansion("kd", LeaderboardType.KD).onRequest(null, "top_10");
    assertNotNull(result);
    String[] lines = result.split("\n");

    assertTrue(lines[0].startsWith("High"));
    assertTrue(lines[1].startsWith("Low"));
  }

  @Test
  void topNameReturnsLeaderName() {
    UUID a = UUID.randomUUID();
    addPlayer(a, "Leader", 50, 1);
    UUID b = UUID.randomUUID();
    addPlayer(b, "Second", 30, 2);

    assertEquals("Leader",
        expansion("kill", LeaderboardType.KILLS).onRequest(null, "top_name"));
  }

  @Test
  void topValueReturnsLeaderValue() {
    UUID a = UUID.randomUUID();
    addPlayer(a, "Leader", 50, 1);

    assertEquals("50",
        expansion("kill", LeaderboardType.KILLS).onRequest(null, "top_value"));
  }

  @Test
  void topNameEmptyCacheReturnsEmptyString() {
    assertEquals("", expansion("kill", LeaderboardType.KILLS).onRequest(null, "top_name"));
  }

  @Test
  void topValueEmptyCacheReturnsEmptyString() {
    assertEquals("", expansion("kill", LeaderboardType.KILLS).onRequest(null, "top_value"));
  }

  @Test
  void unknownParamReturnsNull() {
    assertNull(expansion("killed", LeaderboardType.KILLS).onRequest(null, "bogus"));
  }
}
