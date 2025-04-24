package su.nightexpress.dungeons.hook.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.SunLightAPI;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.godmode.GodModule;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;

public class SunLightHook {

    public static void disableGod(@NotNull Player player) {
        SunUser user = SunLightAPI.getPlugin().getUserManager().getUserData(player);
        user.getSettings().set(GodModule.GOD_MODE, false);
    }

    public static void disableBoard(@NotNull Player player) {
        ScoreboardModule module = SunLightAPI.getModuleManager().getModule(ScoreboardModule.class).orElse(null);
        if (module == null) return;

        module.removeBoard(player);
    }
}
