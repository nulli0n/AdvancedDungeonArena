package su.nightexpress.dungeons.hook.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.compat.BoardPlugin;
import su.nightexpress.dungeons.api.compat.GodPlugin;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardProperties;
import su.nightexpress.sunlight.user.SunUser;

import java.util.function.Consumer;
import java.util.function.Function;

public class SunLightHook implements GodPlugin, BoardPlugin {

    private final SunLightPlugin plugin = SunLightPlugin.getPlugin(SunLightPlugin.class);

    @Override
    public boolean isGodEnabled(@NotNull Player player) {
        return player.isInvulnerable();
    }

    @Override
    public void disableGod(@NotNull Player player) {
        player.setInvulnerable(false);
    }

    @Override
    public void enableGod(@NotNull Player player) {
        player.setInvulnerable(true);
    }

    @Override
    public boolean isBoardEnabled(@NotNull Player player) {
        return this.checkUser(player, user -> user.getPropertyOrDefault(ScoreboardProperties.SCOREBOARD));
    }

    @Override
    public void disableBoard(@NotNull Player player) {
        ScoreboardModule module = this.plugin.getModuleRegistry().byType(ScoreboardModule.class).orElse(null);
        if (module == null) return;

        module.removeBoard(player);
    }

    @Override
    public void enableBoard(@NotNull Player player) {
        ScoreboardModule module = this.plugin.getModuleRegistry().byType(ScoreboardModule.class).orElse(null);
        if (module == null) return;

        module.addBoard(player);
    }

    @NotNull
    private SunUser getUserData(@NotNull Player player) {
        return this.plugin.getUserManager().getOrFetch(player);
    }

    private boolean checkUser(@NotNull Player player, @NotNull Function<SunUser, Boolean> function) {
        return function.apply(this.getUserData(player));
    }

    private void manageUser(@NotNull Player player, @NotNull Consumer<SunUser> consumer) {
        consumer.accept(this.getUserData(player));
    }
}
