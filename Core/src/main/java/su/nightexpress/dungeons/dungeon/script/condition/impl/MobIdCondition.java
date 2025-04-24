package su.nightexpress.dungeons.dungeon.script.condition.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.mob.MobIdentifier;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.event.game.MobEvent;
import su.nightexpress.dungeons.dungeon.script.condition.Condition;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionId;
import su.nightexpress.nightcore.config.FileConfig;

public class MobIdCondition implements Condition {

    private final MobIdentifier identifier;

    public MobIdCondition(@NotNull MobIdentifier identifier) {
        this.identifier = identifier;
    }

    @NotNull
    public static MobIdCondition load(@NotNull FileConfig config, @NotNull String path) {
        MobIdentifier identifier = MobIdentifier.read(config, path + ".MobId");

        return new MobIdCondition(identifier);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".MobId", this.identifier);
    }

    @NotNull
    @Override
    public String getName() {
        return ConditionId.MOB_ID;
    }

    @Override
    public boolean test(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        if (event instanceof MobEvent mobEvent) {
            return mobEvent.getDungeonMob().isMob(this.identifier);
        }
        return false;
    }
}
