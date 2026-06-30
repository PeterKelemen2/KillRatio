package com.crimsonwarpedcraft.playerkillplugin;

import com.crimsonwarpedcraft.playerkillplugin.config.PluginConfig;
import com.crimsonwarpedcraft.playerkillplugin.data.PlayerStats;
import com.crimsonwarpedcraft.playerkillplugin.listener.PlayerKillListener;
import com.crimsonwarpedcraft.playerkillplugin.placeholder.KillStatsExpansion;
import com.crimsonwarpedcraft.playerkillplugin.placeholder.LeaderboardExpansion;
import com.crimsonwarpedcraft.playerkillplugin.placeholder.LeaderboardType;
import com.crimsonwarpedcraft.playerkillplugin.store.StatsStore;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Tracks per-player PvP kill and death statistics and exposes them via PlaceholderAPI.
 */
public class PlayerKillPlugin extends JavaPlugin {

  private StatsStore statsStore;
  private BukkitTask autoSaveTask;
  private final ConcurrentHashMap<UUID, PlayerStats> statsCache = new ConcurrentHashMap<>();

  @Override
  public void onEnable() {
    saveDefaultConfig();
    final PluginConfig config = new PluginConfig(getConfig());

    statsStore = new StatsStore(getDataFolder());
    statsCache.putAll(statsStore.loadAll());

    // Auto-save every 5 minutes (6000 ticks)
    autoSaveTask = getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
      try {
        statsStore.saveAll(new ConcurrentHashMap<>(statsCache));
      } catch (IOException e) {
        getLogger().warning("Auto-save failed: " + e.getMessage());
      }
    }, 6000L, 6000L);

    getServer().getPluginManager()
        .registerEvents(new PlayerKillListener(statsCache, config.isPvpDeathsOnly()), this);

    if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
      final Function<UUID, String> nameResolver = uuid -> {
        String name = Bukkit.getOfflinePlayer(uuid).getName();
        return name != null ? name : "Unknown";
      };
      new KillStatsExpansion(statsCache).register();
      new LeaderboardExpansion(
          "killed", LeaderboardType.KILLS, statsCache, nameResolver).register();
      new LeaderboardExpansion(
          "kd", LeaderboardType.KD, statsCache, nameResolver).register();
      new LeaderboardExpansion(
          "deaths", LeaderboardType.DEATHS, statsCache, nameResolver).register();
      new LeaderboardExpansion(
          "kill", LeaderboardType.KILLS, statsCache, nameResolver).register();
      new LeaderboardExpansion(
          "death", LeaderboardType.DEATHS, statsCache, nameResolver).register();
    } else {
      getLogger().info("PlaceholderAPI not found — placeholders disabled.");
    }
  }

  @Override
  public void onDisable() {
    if (autoSaveTask != null) {
      autoSaveTask.cancel();
    }
    if (statsStore != null) {
      try {
        statsStore.saveAll(statsCache);
      } catch (IOException e) {
        getLogger().severe("Failed to save stats on shutdown: " + e.getMessage());
      }
    }
  }
}
