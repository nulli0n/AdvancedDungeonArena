package su.nightexpress.dungeons.nms.mc_1_21_3.brain.behavior.impl;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class MoveToTarget extends Behavior<Mob> {

    private static final int      MAX_COOLDOWN_BEFORE_RETRYING = 40;

    private int      remainingCooldown;
    @Nullable
    private Path     path;
    @Nullable
    private BlockPos lastTargetPos;
    private float    speedModifier;

    public MoveToTarget() {
        this(150, 250);
    }

    public MoveToTarget(int minDuration, int maxDuration) {
        super(
            ImmutableMap.of(
                MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED,
                MemoryModuleType.PATH, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT
            ),
            minDuration, maxDuration
        );
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Mob mob) {
        if (this.remainingCooldown > 0) {
            --this.remainingCooldown;
            return false;
        }

        Brain<?> brain = mob.getBrain();
        WalkTarget walkTarget = brain.getMemory(MemoryModuleType.WALK_TARGET).orElse(null);
        if (walkTarget == null) return false;

        boolean reachedTarget = this.reachedTarget(mob, walkTarget);
        if (!reachedTarget && this.tryComputePath(mob, walkTarget, level.getGameTime())) {
            this.lastTargetPos = walkTarget.getTarget().currentBlockPosition();
            return true;
        }

        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        if (reachedTarget) {
            brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        }
        return false;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Mob mob, long gameTime) {
        if (this.path != null && this.lastTargetPos != null) {
            Optional<WalkTarget> walkTarget = mob.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
            PathNavigation navigation = mob.getNavigation();
            return !navigation.isDone() && walkTarget.isPresent() && !this.reachedTarget(mob, walkTarget.get());
        }

        mob.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
        mob.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        return false;
    }

    @Override
    protected void stop(ServerLevel level, Mob mob, long gameTime) {
        Brain<?> brain = mob.getBrain();
        WalkTarget walkTarget = brain.getMemory(MemoryModuleType.WALK_TARGET).orElse(null);

        if (brain.hasMemoryValue(MemoryModuleType.WALK_TARGET) && (walkTarget == null || !this.reachedTarget(mob, walkTarget)) && mob.getNavigation().isStuck()) {
            this.remainingCooldown = level.getRandom().nextInt(MAX_COOLDOWN_BEFORE_RETRYING);
        }
        mob.getNavigation().stop();
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.PATH);
        this.path = null;
    }

    @Override
    protected void start(ServerLevel level, Mob mob, long gameTime) {
        mob.getBrain().setMemory(MemoryModuleType.PATH, this.path);
        mob.getNavigation().moveTo(this.path, this.speedModifier);
    }

    @Override
    protected void tick(ServerLevel level, Mob mob, long gameTime) {
        Path navigationPath = mob.getNavigation().getPath();
        Brain<?> brain = mob.getBrain();
        if (this.path != navigationPath) {
            this.path = navigationPath;
            brain.setMemory(MemoryModuleType.PATH, navigationPath);
        }

        if (navigationPath != null && this.lastTargetPos != null) {
            WalkTarget walkTarget = brain.getMemory(MemoryModuleType.WALK_TARGET).orElse(null);
            if (walkTarget == null) return;

            if (walkTarget.getTarget().currentBlockPosition().distSqr(this.lastTargetPos) > 4D && this.tryComputePath(mob, walkTarget, level.getGameTime())) {
                this.lastTargetPos = walkTarget.getTarget().currentBlockPosition();
                this.start(level, mob, gameTime);
            }
        }
    }

    private boolean tryComputePath(Mob mob, WalkTarget walkTarget, long gameTime) {
        BlockPos position = walkTarget.getTarget().currentBlockPosition();
        this.path = mob.getNavigation().createPath(position, 0);
        this.speedModifier = walkTarget.getSpeedModifier();
        Brain<?> brain = mob.getBrain();
        if (this.reachedTarget(mob, walkTarget)) {
            brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            return false;
        }

        boolean canReach = this.path != null && this.path.canReach();
        if (canReach) {
            brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        }
        else if (!brain.hasMemoryValue(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
            brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, gameTime);
        }

        if (this.path != null) {
            return true;
        }

        Vec3 posTowards = DefaultRandomPos.getPosTowards((PathfinderMob) mob, 10, 7, Vec3.atBottomCenterOf(position), 1.5707963705062866D);
        if (posTowards != null) {
            this.path = mob.getNavigation().createPath(posTowards.x, posTowards.y, posTowards.z, 0);
            return this.path != null;
        }

        return false;
    }

    private boolean reachedTarget(Mob mob, WalkTarget walkTarget) {
        return walkTarget.getTarget().currentBlockPosition().distManhattan(mob.blockPosition()) <= walkTarget.getCloseEnoughDist();
    }
}
