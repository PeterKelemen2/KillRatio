package hu.petidev.store;

import static org.junit.jupiter.api.Assertions.assertEquals;

import hu.petidev.data.PlayerStats;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class StatsStoreTest {

  @TempDir
  File tempDir;

  @Test
  void loadAllReturnsEmptyMapWhenNoFile() {
    StatsStore store = new StatsStore(tempDir);
    assertEquals(0, store.loadAll().size());
  }

  @Test
  void saveAndLoadRoundTrip() throws IOException {
    UUID uuid = UUID.randomUUID();
    PlayerStats stats = new PlayerStats();
    stats.setKills(5);
    stats.setDeaths(3);

    Map<UUID, PlayerStats> cache = new ConcurrentHashMap<>();
    cache.put(uuid, stats);

    StatsStore store = new StatsStore(tempDir);
    store.saveAll(cache);

    StatsStore reloaded = new StatsStore(tempDir);
    Map<UUID, PlayerStats> loaded = reloaded.loadAll();

    assertEquals(1, loaded.size());
    assertEquals(5, loaded.get(uuid).getKills());
    assertEquals(3, loaded.get(uuid).getDeaths());
  }
}
