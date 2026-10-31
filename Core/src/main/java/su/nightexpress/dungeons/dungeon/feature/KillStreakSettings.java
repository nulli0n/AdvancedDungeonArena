package su.nightexpress.dungeons.dungeon.feature;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class KillStreakSettings implements Writeable {

    public static final String PATH = "KillStreaks";

    private Boolean                  enabled;
    private Integer                  decayTime;
    private Boolean                  oncePerRun;
    private Map<String, KillStreak> streaks;

    public KillStreakSettings() {

    }

    @Nullable
    public static KillStreakSettings read(@NotNull FileConfig config, @NotNull String path) {
        if (!config.contains(path)) return null;

        KillStreakSettings settings = new KillStreakSettings();

        if (config.contains(path + ".Enabled")) {
            settings.enabled = config.getBoolean(path + ".Enabled");
        }
        if (config.contains(path + ".DecayTime")) {
            settings.decayTime = config.getInt(path + ".DecayTime");
        }
        if (config.contains(path + ".OncePerRun")) {
            settings.oncePerRun = config.getBoolean(path + ".OncePerRun");
        }
        if (config.contains(path + ".List")) {
            Map<String, KillStreak> map = new LinkedHashMap<>();
            config.getSection(path + ".List").forEach(id -> {
                KillStreak streak = KillStreak.read(config, path + ".List." + id, id);
                map.put(streak.getId(), streak);
            });
            settings.streaks = map;
        }

        return settings;
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        if (this.enabled != null) config.set(path + ".Enabled", this.enabled);
        if (this.decayTime != null) config.set(path + ".DecayTime", this.decayTime);
        if (this.oncePerRun != null) config.set(path + ".OncePerRun", this.oncePerRun);
        if (this.streaks != null) {
            config.remove(path + ".List");
            this.streaks.forEach((id, streak) -> config.set(path + ".List." + id, streak));
        }
    }

    public boolean isEnabled() {
        return this.enabled != null ? this.enabled : Config.KILL_STREAKS_ENABLED.get();
    }

    public int getDecayTime() {
        return this.decayTime != null ? this.decayTime : Config.KILL_STREAKS_DECAY_TIME.get();
    }

    public boolean isOncePerRun() {
        return this.oncePerRun != null ? this.oncePerRun : Config.KILL_STREAKS_ONCE_PER_RUN.get();
    }

    @NotNull
    public Map<String, KillStreak> getStreaks() {
        return this.streaks != null ? this.streaks : Config.KILL_STREAKS_LIST.get();
    }

    @Nullable
    public KillStreak getKillStreak(int kills) {
        return this.getStreaks().values().stream()
                .filter(killStreak -> killStreak.isGoodStreak(kills))
                .max(Comparator.comparingInt(KillStreak::getKills))
                .orElse(null);
    }
}