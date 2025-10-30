package su.nightexpress.dungeons.nms.mc_1_21_10.brain.behavior.impl;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.PrepareRamNearestTarget;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_21_R6.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_21_R6.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import su.nightexpress.dungeons.nms.mc_1_21_10.brain.MobAI;
import su.nightexpress.dungeons.nms.mc_1_21_10.mob.GoatMob;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class PrepareRamTarget<E extends PathfinderMob> extends Behavior<E> {

    public static final int                     TIME_OUT_DURATION           = 160;
    private final       int                     minRamDistance;
    private final       int                     maxRamDistance;
    private final       float                   walkSpeed;
    private final       int                     ramPrepareTime;
    private final       Function<E, SoundEvent> getPrepareRamSound;
    private             Optional<Long>          reachedRamPositionTimestamp = Optional.empty();
    private             Optional<PrepareRamNearestTarget.RamCandidate> ramCandidate                = Optional.empty();

    public PrepareRamTarget(
        int minRamDistance,
        int maxRamDistance,
        float speed,
        int prepareTime,
        Function<E, SoundEvent> prepareRamSound) {
        super(ImmutableMap.of(
            MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
            MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
            MemoryModuleType.RAM_TARGET, MemoryStatus.VALUE_ABSENT), 160);
        this.minRamDistance = minRamDistance;
        this.maxRamDistance = maxRamDistance;
        this.walkSpeed = speed;
        this.ramPrepareTime = prepareTime;
        this.getPrepareRamSound = prepareRamSound;
    }

    protected void start(ServerLevel worldserver, PathfinderMob pet, long i) {
        MobAI.getAngerTarget(pet).ifPresent((target) -> {
            EntityTargetEvent event = CraftEventFactory.callEntityTargetLivingEvent(pet, target, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET);
            if (!event.isCancelled() && event.getTarget() != null) {
                target = ((CraftLivingEntity) event.getTarget()).getHandle();
                this.chooseRamPosition(pet, target);
            }
        });
    }

    protected void stop(ServerLevel level, E goat, long i) {
        Brain<?> brain = goat.getBrain();
        if (!brain.hasMemoryValue(MemoryModuleType.RAM_TARGET)) {
            level.broadcastEntityEvent(goat, (byte) 59);
            brain.setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, GoatMob.TIME_BETWEEN_RAMS.getMinValue());
        }

    }

    protected boolean canStillUse(ServerLevel worldserver, PathfinderMob entitycreature, long i) {
        return this.ramCandidate.isPresent() && this.ramCandidate.get().getTarget().isAlive();
    }

    protected void tick(ServerLevel level, E goat, long i) {
        if (this.ramCandidate.isPresent()) {
            goat.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(this.ramCandidate.get().getStartPosition(), this.walkSpeed, 0));
            goat.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(this.ramCandidate.get().getTarget(), true));
            /*boolean needRamPos = !this.ramCandidate.get().getTarget().blockPosition().equals(this.ramCandidate.get().getTargetPosition());
            if (needRamPos) {
                level.broadcastEntityEvent(goat, (byte)59);
                goat.getNavigation().stop();
                this.chooseRamPosition(goat, this.ramCandidate.get().getTarget());
            }
            else {*/
            BlockPos position = goat.blockPosition();
            if (position.equals(this.ramCandidate.get().getStartPosition())) {
                level.broadcastEntityEvent(goat, (byte) 58);
                if (this.reachedRamPositionTimestamp.isEmpty()) {
                    this.reachedRamPositionTimestamp = Optional.of(i);
                }

                if (i - this.reachedRamPositionTimestamp.get() >= (long) this.ramPrepareTime) {
                    goat.getBrain().setMemory(MemoryModuleType.RAM_TARGET, this.getEdgeOfBlock(position, this.ramCandidate.get().getTarget().blockPosition()));
                    level.playSound(null, goat, this.getPrepareRamSound.apply(goat), SoundSource.NEUTRAL, 1.0F, goat.getVoicePitch());
                    this.ramCandidate = Optional.empty();
                }
            }
            //}
        }

    }

    private Vec3 getEdgeOfBlock(BlockPos blockposition, BlockPos blockposition1) {
        double d0 = 0.5D;
        double d1 = 0.5D * (double) Mth.sign(blockposition1.getX() - blockposition.getX());
        double d2 = 0.5D * (double) Mth.sign(blockposition1.getZ() - blockposition.getZ());
        return Vec3.atBottomCenterOf(blockposition1).add(d1, 0.0D, d2);
    }

    private Optional<BlockPos> calculateRammingStartPosition(PathfinderMob pet, LivingEntity target) {
        BlockPos position = target.blockPosition();
        if (!this.isWalkableBlock(pet, position)) {
            return Optional.empty();
        }

        List<BlockPos> ramPositions = new ArrayList<>();
        BlockPos.MutableBlockPos mutableBlockPos = position.mutable();

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            mutableBlockPos.set(position);

            for (int range = 0; range < this.maxRamDistance; ++range) {
                if (!this.isWalkableBlock(pet, mutableBlockPos.move(direction))) {
                    mutableBlockPos.move(direction.getOpposite());
                    break;
                }
            }

            if (mutableBlockPos.distManhattan(position) >= this.minRamDistance) {
                ramPositions.add(mutableBlockPos.immutable());
            }
        }

        PathNavigation navigation = pet.getNavigation();
        BlockPos position1 = pet.blockPosition();
        return ramPositions.stream().sorted(Comparator.comparingDouble(position1::distSqr)).filter((blockposition2) -> {
            Path path = navigation.createPath(blockposition2, 0);
            return path != null && path.canReach();
        }).findFirst();
    }

    private boolean isWalkableBlock(PathfinderMob pet, BlockPos position) {
        return pet.getNavigation().isStableDestination(position) && pet.getPathfindingMalus(WalkNodeEvaluator.getPathTypeStatic(pet, position)) == 0.0F;
    }

    private void chooseRamPosition(PathfinderMob pet, LivingEntity target) {
        this.reachedRamPositionTimestamp = Optional.empty();
        this.ramCandidate = this.calculateRammingStartPosition(pet, target).map((blockposition) -> {
            return new PrepareRamNearestTarget.RamCandidate(blockposition, target.blockPosition(), target);
        });
    }
}
