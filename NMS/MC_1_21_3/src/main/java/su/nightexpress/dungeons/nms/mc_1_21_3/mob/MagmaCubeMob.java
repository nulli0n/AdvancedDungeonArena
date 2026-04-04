package su.nightexpress.dungeons.nms.mc_1_21_3.mob;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonHolder;
import su.nightexpress.dungeons.api.type.MobFaction;

public class MagmaCubeMob extends MagmaCube implements DungeonHolder {

    public MagmaCubeMob(@NotNull ServerLevel world) {
        super(EntityType.MAGMA_CUBE, world);
    }

    @Override
    protected int getJumpDelay() {
        return 8;
    }

//    public void jumpFromGround() {
//        Vec3 vec3d = this.getDeltaMovement();
//        this.setDeltaMovement(vec3d.x, this.getJumpPower(), vec3d.z);
//        this.hasImpulse = true;
//    }

    @Override
    public void push(Entity pusher) {
        if (pusher.getBukkitEntity() instanceof org.bukkit.entity.LivingEntity bukkitMob && this.getFaction() != this.getDungeon().getMobFaction(bukkitMob)) {
            this.dealDamage((LivingEntity) pusher);
        }

        //super.push(pusher);
    }

    @Override
    public void playerTouch(Player entity) {
        if (this.getFaction() == MobFaction.ALLY) return;

        super.playerTouch(entity);
    }
}
