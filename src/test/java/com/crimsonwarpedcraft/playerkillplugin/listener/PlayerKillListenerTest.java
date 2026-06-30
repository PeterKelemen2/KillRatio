package com.crimsonwarpedcraft.playerkillplugin.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.crimsonwarpedcraft.playerkillplugin.data.PlayerStats;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerKillListenerTest {

  private ConcurrentHashMap<UUID, PlayerStats> cache;
  private UUID killerUuid;
  private UUID victimUuid;
  private Player killer;
  private Player victim;
  private PlayerDeathEvent event;

  @BeforeEach
  void setup() {
    cache = new ConcurrentHashMap<>();
    killerUuid = UUID.randomUUID();
    victimUuid = UUID.randomUUID();
    killer = mock(Player.class);
    victim = mock(Player.class);
    event = mock(PlayerDeathEvent.class);
    when(killer.getUniqueId()).thenReturn(killerUuid);
    when(victim.getUniqueId()).thenReturn(victimUuid);
    when(event.getEntity()).thenReturn(victim);
  }

  @Test
  void incrementsKillerKillsOnPvpDeath() {
    when(victim.getKiller()).thenReturn(killer);
    cache.put(killerUuid, new PlayerStats());

    new PlayerKillListener(cache, true).onPlayerDeath(event);

    assertEquals(1, cache.get(killerUuid).getKills());
  }

  @Test
  void incrementsVictimDeathsOnPvpDeath() {
    when(victim.getKiller()).thenReturn(killer);

    new PlayerKillListener(cache, true).onPlayerDeath(event);

    assertEquals(1, cache.get(victimUuid).getDeaths());
  }

  @Test
  void skipsVictimDeathWhenPvpOnlyAndNonPvpKill() {
    when(victim.getKiller()).thenReturn(null);

    new PlayerKillListener(cache, true).onPlayerDeath(event);

    assertEquals(0, cache.getOrDefault(victimUuid, new PlayerStats()).getDeaths());
  }

  @Test
  void incrementsVictimDeathsForNonPvpKillWhenAllDeathsMode() {
    when(victim.getKiller()).thenReturn(null);

    new PlayerKillListener(cache, false).onPlayerDeath(event);

    assertEquals(1, cache.get(victimUuid).getDeaths());
  }
}
