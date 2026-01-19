package su.nightexpress.dungeons.hook.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.compat.BoardPlugin;
import su.nightexpress.dungeons.api.compat.GodPlugin;
import su.nightexpress.sunlight.SunLightAPI;
import su.nightexpress.sunlight.module.godmode.GodModule;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardProperties;
import su.nightexpress.sunlight.user.SunUser;

import java.util.function.Consumer;
import java.util.function.Function;

public class SunLightHook implements GodPlugin, BoardPlugin {

    @Override
    public boolean isGodEnabled(@NotNull Player player) {
        return this.checkUser(player, user -> user.getProperty(GodModule.GOD_MODE));
    }

    @Override
    public void disableGod(@NotNull Player player) {
        this.manageUser(player, user -> user.setProperty(GodModule.GOD_MODE, false));
    }

    @Override
    public void enableGod(@NotNull Player player) {
        this.manageUser(player, user -> user.setProperty(GodModule.GOD_MODE, true));
    }

    @Override
    public boolean isBoardEnabled(@NotNull Player player) {
        return this.checkUser(player, user -> user.getProperty(ScoreboardProperties.SCOREBOARD));
    }

    @Override
    public void disableBoard(@NotNull Player player) {
        ScoreboardModule module = SunLightAPI.getModuleRegistry().byType(ScoreboardModule.class).orElse(null);
        if (module == null) return;

        module.removeBoard(player);
    }

    @Override
    public void enableBoard(@NotNull Player player) {
        ScoreboardModule module = SunLightAPI.getModuleRegistry().byType(ScoreboardModule.class).orElse(null);
        if (module == null) return;

        module.addBoard(player);
    }

    @NotNull
    private SunUser getUserData(@NotNull Player player) {
        return SunLightAPI.getPlugin().getUserManager().getOrFetch(player);
    }

    private boolean checkUser(@NotNull Player player, @NotNull Function<SunUser, Boolean> function) {
        return function.apply(this.getUserData(player));
    }

    private void manageUser(@NotNull Player player, @NotNull Consumer<SunUser> consumer) {
        consumer.accept(this.getUserData(player));
    }
}
