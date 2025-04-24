package su.nightexpress.dungeons.dungeon.event.game;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.spot.Spot;
import su.nightexpress.dungeons.dungeon.spot.SpotState;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.DungeonEventType;

public class DungeonSpotChangeEvent extends DungeonGameEvent {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    private final Spot spot;
    private final SpotState state;

    public DungeonSpotChangeEvent(@NotNull DungeonInstance dungeon, @NotNull Spot spot, @NotNull SpotState state) {
        super(DungeonEventType.SPOT_CHANGED, dungeon);
        this.spot = spot;
        this.state = state;
    }

    @NotNull
    public Spot getSpot() {
        return this.spot;
    }

    @NotNull
    public SpotState getState() {
        return this.state;
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
