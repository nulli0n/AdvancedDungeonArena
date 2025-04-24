package su.nightexpress.dungeons.dungeon.script.action.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.level.Level;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.script.action.Action;
import su.nightexpress.dungeons.dungeon.script.action.ActionId;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.FileConfig;

public class SetLevelAction implements Action {

    private final String levelId;

    public SetLevelAction(@NotNull String levelId) {
        this.levelId = levelId;
    }

    @NotNull
    public static SetLevelAction load(@NotNull FileConfig config, @NotNull String path) {
        String levelId = config.getString(path + ".LevelId", "null");

        return new SetLevelAction(levelId);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".LevelId", this.levelId);
    }

    @Override
    public void perform(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        Level level = dungeon.getConfig().getLevelById(this.levelId);
        if (level == null) {
            ErrorHandler.error("Could not set level '" + this.levelId + "': level does not exist.", this, dungeon);
            return;
        }

        dungeon.setLevel(level);
    }

    @NotNull
    @Override
    public String getName() {
        return ActionId.SET_LEVEL;
    }

    @NotNull
    public String getLevelId() {
        return this.levelId;
    }
}
