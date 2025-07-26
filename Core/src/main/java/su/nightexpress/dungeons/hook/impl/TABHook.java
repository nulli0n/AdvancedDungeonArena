package su.nightexpress.dungeons.hook.impl;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.scoreboard.ScoreboardManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.compat.BoardPlugin;

import java.util.function.BiFunction;

public class TABHook implements BoardPlugin {

    @Override
    public boolean isBoardEnabled(@NotNull Player player) {
        return this.manageScoreboard(player, (tabPlayer, manager) -> manager.hasCustomScoreboard(tabPlayer));
    }

    @Override
    public void disableBoard(@NotNull Player player) {
        this.manageScoreboard(player, (tabPlayer, manager) -> {
            manager.toggleScoreboard(tabPlayer, false);
            return true;
        });
    }

    @Override
    public void enableBoard(@NotNull Player player) {
        this.manageScoreboard(player, (tabPlayer, manager) -> {
            manager.toggleScoreboard(tabPlayer, true);
            return true;
        });
    }

    private boolean manageScoreboard(@NotNull Player player, @NotNull BiFunction<TabPlayer, ScoreboardManager, Boolean> consumer) {
        ScoreboardManager manager = TabAPI.getInstance().getScoreboardManager();
        if (manager == null) return false;

        TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
        if (tabPlayer == null) return false;

        return consumer.apply(tabPlayer, manager);
    }
}
