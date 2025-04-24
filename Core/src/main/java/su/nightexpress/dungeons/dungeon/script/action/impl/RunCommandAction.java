package su.nightexpress.dungeons.dungeon.script.action.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonTarget;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.script.action.Action;
import su.nightexpress.dungeons.dungeon.script.action.ActionId;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Lists;

import java.util.List;

public class RunCommandAction implements Action {

    private final List<String>  commands;
    private final DungeonTarget target;

    public RunCommandAction(@NotNull List<String> commands, @NotNull DungeonTarget target) {
        this.commands = commands;
        this.target = target;
    }

    @NotNull
    public static RunCommandAction load(@NotNull FileConfig config, @NotNull String path) {
        List<String> commands = ConfigValue.create(path + ".Commands", Lists.newList()).read(config);
        DungeonTarget target = ConfigValue.create(path + ".Target", DungeonTarget.class, DungeonTarget.GLOBAL).read(config);

        return new RunCommandAction(commands, target);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Commands", this.commands);
        config.set(path + ".Target", this.target.name());
    }

    @NotNull
    @Override
    public String getName() {
        return ActionId.RUN_COMMAND;
    }

    @Override
    public void perform(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        dungeon.runCommand(this.commands, this.target, event);
    }
}
