package su.nightexpress.dungeons.dungeon.scale.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.scale.ScaleBase;
import su.nightexpress.dungeons.dungeon.scale.ScaleBaseId;

public class DeadPlayerAmountBase implements ScaleBase {

    @NotNull
    @Override
    public String getName() {
        return ScaleBaseId.DEAD_PLAYER_AMOUNT;
    }

    @Override
    public double getBaseValue(@NotNull DungeonInstance instance) {
        return instance.countDeadPlayers();
    }
}
