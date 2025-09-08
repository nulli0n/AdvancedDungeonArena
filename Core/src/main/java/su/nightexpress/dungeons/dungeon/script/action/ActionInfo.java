package su.nightexpress.dungeons.dungeon.script.action;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.nightcore.util.random.Rnd;

public class ActionInfo {

    private final String runIfCondition;
    private final double chance;
    private final Action action;

    public ActionInfo(@Nullable String runIfCondition, double chance, Action action) {
        this.runIfCondition = runIfCondition;
        this.chance = chance;
        this.action = action;
    }

    public boolean run(@NotNull DungeonInstance instance, @NotNull DungeonGameEvent event) {
        if (Rnd.chance(this.chance)) {
            this.action.perform(instance, event);
            return true;
        }
        return false;
    }

    @Nullable
    public String getRunIfCondition() {
        return this.runIfCondition;
    }

    @NotNull
    public Action getAction() {
        return this.action;
    }

    public double getChance() {
        return this.chance;
    }
}
