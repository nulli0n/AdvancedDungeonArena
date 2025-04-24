package su.nightexpress.dungeons.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonsAPI;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.script.action.Action;
import su.nightexpress.dungeons.dungeon.script.condition.Condition;
import su.nightexpress.nightcore.config.FileConfig;

public class ErrorHandler {

    public static void error(@NotNull String text, @NotNull FileConfig config) {
        DungeonsAPI.getPlugin().error(text + " Found in '" + config.getFile().getPath() + "'.");
    }

    public static void error(@NotNull String text, @NotNull FileConfig config, @NotNull String path) {
        DungeonsAPI.getPlugin().error(text + " Found in '" + config.getFile().getPath() + "' -> '" + path + "'.");
    }

    public static void error(@NotNull String text, @NotNull Action action, @NotNull DungeonInstance dungeon) {
        DungeonsAPI.getPlugin().error("[Dungeon: '" + dungeon.getId() + "', Action: '" + action.getName() + "'] " + text);
    }

    public static void error(@NotNull String text, @NotNull Condition condition, @NotNull DungeonInstance dungeon) {
        DungeonsAPI.getPlugin().error("[Dungeon: '" + dungeon.getId() + "', Condition: '" + condition.getName() + "'] " + text);
    }

    public static void error(@NotNull String text, @NotNull DungeonInstance dungeon) {
        DungeonsAPI.getPlugin().error("[Dungeon: '" + dungeon.getId() + "'] " + text);
    }
}
