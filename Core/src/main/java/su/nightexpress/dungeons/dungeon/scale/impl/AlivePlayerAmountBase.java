package su.nightexpress.dungeons.dungeon.scale.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.scale.ScaleBase;
import su.nightexpress.dungeons.dungeon.scale.ScaleBaseId;

public class AlivePlayerAmountBase implements ScaleBase {

    @NotNull
    @Override
    public String getName() {
        return ScaleBaseId.ALIVE_PLAYER_AMOUNT;
    }

    @Override
    public double getBaseValue(@NotNull DungeonInstance instance) {
        return instance.countAlivePlayers();
    }
}
