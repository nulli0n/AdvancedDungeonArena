package su.nightexpress.dungeons.nms.mc_1_21_8.mob;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonHolder;

public class IronGolemMob extends IronGolem implements DungeonHolder {

    public IronGolemMob(@NotNull ServerLevel level) {
        super(EntityType.IRON_GOLEM, level);
    }

    @Override
    protected void doPush(Entity entity) {
        entity.push(this);
    }
}
