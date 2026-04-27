package su.nightexpress.dungeons.nms.mc_1_21_3.brain.goal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.Dungeon;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.EnumSet;

public class MeleeAttackGoal extends Goal {

    private final Dungeon arena;
    private final MobFaction faction;

    protected     PathfinderMob entity;
    protected     int           atkCooldown;
    private final double        speed;
    private       Path          path;
    private       int           ticksNextPathCalc;
    private       double        pathX;
    private       double        pathY;
    private       double        pathZ;

    public MeleeAttackGoal(@NotNull PathfinderMob entity, @NotNull Dungeon arena, @NotNull MobFaction faction) {
        this.arena = arena;
        this.faction = faction;
        this.entity = entity;
        this.speed = 1D;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public void start() {
        this.entity.getNavigation().moveTo(this.path, this.speed);
        this.entity.setAggressive(true);
        this.ticksNextPathCalc = 0;
    }

    @Override
    public void stop() {
        this.entity.setAggressive(false);
        this.entity.getNavigation().stop();
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        if (target == null) {
            return false;
        }
        if (!target.isAlive()) {
            return false;
        }

        this.path = this.entity.getNavigation().createPath(target, 0); // getControllerMove
        return this.path != null || this.getMinAttackRange(target) >= this.entity.distanceToSqr(target.getX(), target.getY(), target.getZ());
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.entity.getTarget();
        if (target == null || !target.isAlive() || this.entity.getNavigation().isDone()) {
            return false;
        }
        if (this.arena.getMobFaction((org.bukkit.entity.LivingEntity) target.getBukkitEntity()) == this.faction) {
            return false;
        }
        return this.entity.isWithinRestriction(target.blockPosition());
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (target == null) return;

        this.entity.getLookControl().setLookAt(target, 30.0f, 30.0f);
        double distance = this.entity.distanceToSqr(target.getX(), target.getY(), target.getZ());
        --this.ticksNextPathCalc;

        boolean isNoPath = (this.pathX == 0 && this.pathY == 0 && this.pathZ == 0);
        boolean isDistOne = target.distanceToSqr(this.pathX, this.pathY, this.pathZ) >= 1;
        boolean isRandom = Rnd.nextFloat() < 0.05f;

        if (this.ticksNextPathCalc <= 0 && (isNoPath || isDistOne || isRandom)) {
            this.pathX = target.getX();
            this.pathY = target.getY();
            this.pathZ = target.getZ();
            this.ticksNextPathCalc = 4 + Rnd.nextInt(7);
            if (distance > 1024.0) {
                this.ticksNextPathCalc += 10;
            }
            else if (distance > 256.0) {
                this.ticksNextPathCalc += 5;
            }
            if (!this.entity.getNavigation().moveTo(target, this.speed)) {
                this.ticksNextPathCalc += 15;
            }
        }
        this.atkCooldown = Math.max(this.atkCooldown - 1, 0);
        this.attack(target, distance);
    }

    protected void attack(LivingEntity target, double distance) {
        double atkRange = this.getMinAttackRange(target);
        if (distance <= atkRange && this.atkCooldown <= 0) {
            this.atkCooldown = 20;
            this.entity.getItemInHand(InteractionHand.MAIN_HAND);
            this.entity.doHurtTarget((ServerLevel) target.level(), target);
        }
    }

    protected double getMinAttackRange(LivingEntity target) {
        return this.entity.getBbWidth() * 2.0f * (this.entity.getBbWidth() * 2.0f) + target.getBbWidth();
    }
}
