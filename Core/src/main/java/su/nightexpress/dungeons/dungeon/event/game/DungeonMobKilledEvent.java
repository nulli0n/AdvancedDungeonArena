package su.nightexpress.dungeons.dungeon.event.game;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.api.dungeon.DungeonEntity;
import su.nightexpress.dungeons.dungeon.player.DungeonGamer;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.DungeonEventType;

public class DungeonMobKilledEvent extends DungeonMobEvent implements GamerEvent {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    private DungeonGamer killer;

    public DungeonMobKilledEvent(@NotNull DungeonInstance dungeon, @NotNull DungeonEntity dungeonMob) {
        super(DungeonEventType.MOB_KILLED, dungeon, dungeonMob);
    }

    @Nullable
    public DungeonGamer getKiller() {
        return this.getGamer();
    }

    @Nullable
    @Override
    public DungeonGamer getGamer() {
        return this.killer;
    }

    @Override
    public void setGamer(@Nullable DungeonGamer gamer) {
        this.killer = gamer;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
