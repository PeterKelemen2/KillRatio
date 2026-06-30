# KillRatio

A Paper plugin that tracks per-player PvP kill and death statistics and exposes them through [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/).

## Requirements

- Paper 1.18.2+
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) (optional — placeholders are disabled without it)

## Installation

1. Drop `KillRatio.jar` into your `plugins/` folder.
2. Restart the server.
3. Stats are saved automatically every 5 minutes and on server shutdown.

## Configuration

`plugins/KillRatio/config.yml`:

```yaml
# If true (default), only deaths caused directly by another player count toward %personal_deaths%.
# Set to false to count all deaths (fall damage, mobs, etc.).
pvp-deaths-only: true
```

---

## Placeholders

### Personal stats

These are per-player and resolve to the stats of the player requesting the placeholder.

| Placeholder | Description |
|---|---|
| `%personal_kills%` | Total player kills |
| `%personal_deaths%` | Total deaths (PvP-only by default, see config) |
| `%personal_kd%` | K/D ratio formatted to 2 decimal places; shows raw kill count when deaths = 0 |

---

### Leaderboards

Each leaderboard placeholder follows the pattern `%<identifier>_<query>%`.

**Identifiers**

| Identifier | Ranks by |
|---|---|
| `kills` / `kill` | Most kills |
| `deaths` / `death` | Most deaths |
| `kd` | Highest K/D ratio |

**Queries**

| Query | Returns |
|---|---|
| `top_<n>` | Newline-separated list of the top `n` players and their stat value (e.g. `top_5`) |
| `top_<n>_name` | Display name of the player ranked `n` only — not a list (e.g. `top_2_name` → just the #2 player's name) |
| `top_<n>_value` | Stat value of the player ranked `n` only |
| `top_name` / `top_value` | Aliases for `top_1_name` / `top_1_value` |

Out-of-range ranks (e.g. `top_5_name` when fewer than 5 players are tracked) resolve to an empty string.

`top_<n>_name` / `top_<n>_value` are intended for building custom leaderboard layouts (signs, scoreboards, GUIs) by placing one placeholder per rank, e.g. `%kills_top_1_name%`, `%kills_top_2_name%`, `%kills_top_3_name%`, ...

**Examples**

| Placeholder | Description |
|---|---|
| `%kills_top_10%` | Top 10 players by kill count |
| `%kills_top_5%` | Top 5 players by kill count |
| `%kills_top_2_name%` | Name of the player ranked #2 by kills |
| `%kills_top_2_value%` | Kill count of the player ranked #2 |
| `%kd_top_10%` | Top 10 players by K/D ratio |
| `%kd_top_name%` | Name of the player with the highest K/D |
| `%kd_top_value%` | K/D ratio of the #1 player |
| `%deaths_top_10%` | Top 10 players by death count |
| `%deaths_top_name%` | Name of the player with the most deaths |
| `%deaths_top_value%` | Death count of the #1 player |

`kill` and `death` are aliases for `kills` and `deaths` respectively and return identical values.

## Building

```text
./gradlew build
```

The JAR is output to `build/libs/`.
