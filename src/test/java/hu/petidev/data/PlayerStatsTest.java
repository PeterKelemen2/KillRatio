package hu.petidev.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PlayerStatsTest {

  @Test
  void getKdReturnsKillCountWhenDeathsAreZero() {
    PlayerStats stats = new PlayerStats();
    stats.setKills(5);
    assertEquals("5", stats.getKd());
  }

  @Test
  void getKdReturnsZeroWhenBothAreZero() {
    assertEquals("0", new PlayerStats().getKd());
  }

  @Test
  void getKdCalculatesRatioToTwoDecimalPlaces() {
    PlayerStats stats = new PlayerStats();
    stats.setKills(3);
    stats.setDeaths(4);
    assertEquals("0.75", stats.getKd());
  }
}
