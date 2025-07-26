package su.nightexpress.dungeons.api.dungeon;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.type.MobFaction;

import java.util.UUID;

public interface DungeonHolder {

    @NotNull
    default Dungeon getDungeon() {
        return this.getEntity().getDungeon();
    }

    @NotNull
    default MobFaction getFaction() {
        return this.getEntity().getFaction();
    }

    UUID getUUID();

    default DungeonEntity getEntity() {
        return DungeonEntityBridge.getByMobId(this.getUUID());
    }

    default boolean isValidDungeon() {
        return DungeonEntityBridge.getByMobId(this.getUUID()) != null;
    }
}
