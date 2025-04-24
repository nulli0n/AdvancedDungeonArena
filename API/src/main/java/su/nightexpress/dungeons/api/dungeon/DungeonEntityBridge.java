package su.nightexpress.dungeons.api.dungeon;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DungeonEntityBridge {

    public static final Map<UUID, DungeonEntity> BY_ID = new HashMap<>();

    public static void clear() {
        BY_ID.clear();
    }

    @Nullable
    public static DungeonEntity getByMob(@NotNull LivingEntity entity) {
        if (entity.getType() == EntityType.PLAYER) {
            return null;
        }
        return getByMobId(entity.getUniqueId());
    }

    @Nullable
    public static DungeonEntity getByMobId(@NotNull UUID uuid) {
        return BY_ID.get(uuid);
    }

    public static void addHolder(@NotNull DungeonEntity holder) {
        BY_ID.put(holder.getBukkitEntity().getUniqueId(), holder);
    }

    public static void removeHolder(@NotNull DungeonEntity holder) {
        BY_ID.remove(holder.getBukkitEntity().getUniqueId());
    }
}
