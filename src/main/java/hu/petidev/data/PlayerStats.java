package hu.petidev.data;

/**
 * Holds per-player PvP kill and death statistics.
 */
public class PlayerStats {

  private int kills;
  private int deaths;

  /** Creates a {@link PlayerStats} with all stats defaulted to zero. */
  public PlayerStats() {}

  /**
   * Returns the number of players this player has killed.
   *
   * @return the kill count
   */
  public int getKills() {
    return kills;
  }

  /**
   * Sets the number of players this player has killed.
   *
   * @param kills the new kill count
   */
  public void setKills(int kills) {
    this.kills = kills;
  }

  /**
   * Returns the number of times this player has died in PvP.
   *
   * @return the death count
   */
  public int getDeaths() {
    return deaths;
  }

  /**
   * Sets the number of times this player has died in PvP.
   *
   * @param deaths the new death count
   */
  public void setDeaths(int deaths) {
    this.deaths = deaths;
  }

  /**
   * Returns the K/D ratio as a formatted string. When deaths is zero, returns the kill count as
   * a plain integer string to avoid division by zero.
   *
   * @return the K/D ratio string
   */
  public String getKd() {
    if (deaths == 0) {
      return String.valueOf(kills);
    }
    return String.format("%.2f", (double) kills / deaths);
  }
}
