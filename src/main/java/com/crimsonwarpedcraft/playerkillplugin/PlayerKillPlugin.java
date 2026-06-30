package com.crimsonwarpedcraft.playerkillplugin;

import com.crimsonwarpedcraft.cwcommons.config.ConfigManager;
import com.crimsonwarpedcraft.cwcommons.store.DataStore;
import com.crimsonwarpedcraft.cwcommons.store.KeySerializers;
import com.crimsonwarpedcraft.cwcommons.store.Repository;
import com.crimsonwarpedcraft.cwcommons.store.bukkit.AutoFlushTask;
import com.crimsonwarpedcraft.playerkillplugin.config.PluginConfig;
import com.crimsonwarpedcraft.playerkillplugin.data.PlayerStats;
import com.crimsonwarpedcraft.playerkillplugin.listener.PlayerKillListener;
import com.crimsonwarpedcraft.playerkillplugin.listener.PlayerSessionListener;
import com.crimsonwarpedcraft.playerkillplugin.placeholder.KillStatsExpansion;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Tracks per-player PvP kill and death statistics and exposes them via PlaceholderAPI.
 */
public class PlayerKillPlugin extends JavaPlugin {

  private DataStore store;
  private BukkitTask autoFlushTask;
  private Repository<UUID, PlayerStats> statsRepository;
  private final ConcurrentHashMap<UUID, PlayerStats> statsCache = new ConcurrentHashMap<>();

  @Override
  public void onEnable() {
    saveDefaultConfig();
    PluginConfig config;

    try {
      config = new ConfigManager()
          .load(new File(getDataFolder(), "config.yml"), PluginConfig.class);
    } catch (IOException | IllegalStateException e) {
      getLogger().severe("Failed to load config: " + e.getMessage());
      getServer().getPluginManager().disablePlugin(this);
      return;
    }

    try {
      store = DataStore.getLocalDataStore(getName(), getDataFolder());
    } catch (IOException e) {
      getLogger().severe("Failed to open data store: " + e.getMessage());
      getServer().getPluginManager().disablePlugin(this);
      return;
    }

    autoFlushTask = new AutoFlushTask(store, this).start();
    statsRepository =
        store.repository("player-stats", PlayerStats.class, KeySerializers.forUuid());

    // Pre-load stats for any online players (handles hot-reloads)
    for (Player player : getServer().getOnlinePlayers()) {
      UUID uuid = player.getUniqueId();
      statsCache.putIfAbsent(uuid, new PlayerStats());
      statsRepository.get(uuid)
          .thenAccept(opt -> opt.ifPresent(stats -> statsCache.put(uuid, stats)));
    }

    getServer().getPluginManager()
        .registerEvents(new PlayerSessionListener(statsCache, statsRepository), this);
    getServer().getPluginManager()
        .registerEvents(new PlayerKillListener(statsCache, config.isPvpDeathsOnly()), this);

    if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
      new KillStatsExpansion(statsCache).register();
    } else {
      getLogger().info("PlaceholderAPI not found — placeholders disabled.");
    }
  }

  @Override
  public void onDisable() {
    if (autoFlushTask != null) {
      autoFlushTask.cancel();
    }

    if (statsRepository != null) {
      for (Map.Entry<UUID, PlayerStats> entry : statsCache.entrySet()) {
        statsRepository.put(entry.getKey(), entry.getValue()).join();
      }
    }

    if (store != null) {
      try {
        store.close();
      } catch (Exception e) {
        getLogger().severe("Failed to close data store: " + e.getMessage());
      }
    }
  }
}
