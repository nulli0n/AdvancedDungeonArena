package su.nightexpress.dungeons.nms.mc_1_21_11.brain.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.Dungeon;
import su.nightexpress.dungeons.api.type.MobFaction;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class LastDamagerTargetGoal extends TargetGoal {

    private static final TargetingConditions CONDITIONS = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();

    private final Dungeon     arena;
    private final MobFaction faction;

    private int timestamp;

    public LastDamagerTargetGoal(@NotNull Mob mob, @NotNull Dungeon arena, @NotNull MobFaction faction) {
        super(mob, true);
        this.setFlags(EnumSet.of(Flag.TARGET));

        this.arena = arena;
        this.faction = faction;
    }

    public void start() {
        this.setTarget(this.mob.getLastHurtByMob());
        this.targetMob = this.mob.getTarget();
        this.timestamp = this.mob.getLastHurtByMobTimestamp();
        this.unseenMemoryTicks = 300;

        super.start();
    }

    public boolean canUse() {
        int lastHurtDate = this.mob.getLastHurtByMobTimestamp();
        LivingEntity lastDamager = this.mob.getLastHurtByMob();
        if (lastHurtDate == this.timestamp || lastDamager == null) return false;

        if (this.arena.getMobFaction((org.bukkit.entity.LivingEntity) lastDamager.getBukkitEntity()) == this.faction) {
            return false;
        }

        LivingEntity currentTarget = this.mob.getTarget();
        if (currentTarget != null) {
            if (currentTarget == lastDamager) return false;
            if (this.mob.distanceToSqr(lastDamager) > this.mob.distanceToSqr(currentTarget)) return false;
        }

        return this.canAttack(lastDamager, CONDITIONS);
    }

    private void setTarget(@Nullable LivingEntity target) {
        if (target == null) return;

        org.bukkit.entity.Mob bukkitMob = (org.bukkit.entity.Mob) this.mob.getBukkitEntity();
        bukkitMob.setTarget((org.bukkit.entity.LivingEntity) target.getBukkitEntity());
    }
}
