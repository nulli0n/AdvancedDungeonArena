package su.nightexpress.dungeons.nms.mc_1_21_8.brain.behavior.impl;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.animal.frog.Frog;
import su.nightexpress.dungeons.nms.mc_1_21_8.brain.MobAI;

import java.util.Optional;

public class ShootTongue extends Behavior<Frog> {

    public static final  int   TIME_OUT_DURATION         = 100;
    public static final  int   CATCH_ANIMATION_DURATION  = 6;
    public static final  int   TONGUE_ANIMATION_DURATION = 10;
    private static final float EATING_DISTANCE           = 2.5F;
    private static final float EATING_MOVEMENT_FACTOR    = 0.75F;

    private int   eatAnimationTimer;
    private int   calculatePathCounter;
    private int   attackCooldown;
    private State state;

    public ShootTongue() {
        super(ImmutableMap.of(
            MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
            MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), TIME_OUT_DURATION);
        this.state = State.MOVE_TO_TARGET;
    }

    protected boolean canStillUse(ServerLevel level, Frog frog, long var2) {
        LivingEntity target = MobAI.getAngerTarget(frog).orElse(null);
        return target != null && target.isAlive();
    }

    protected void start(ServerLevel level, Frog frog, long var2) {
        LivingEntity target = MobAI.getAngerTarget(frog).orElse(null);
        if (target != null) {
            BehaviorUtils.lookAtEntity(frog, target);
            frog.setTongueTarget(target);
            frog.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(target.position(), 2.0F, 0));
        }
        this.calculatePathCounter = 10;
        this.state = State.MOVE_TO_TARGET;
    }

    protected void stop(ServerLevel level, Frog frog, long var2) {
        frog.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        frog.eraseTongueTarget();
        frog.setPose(Pose.STANDING);
    }

    @Override
    protected boolean timedOut(long var0) {
        return false;
    }

    private void eatEntity(ServerLevel level, Frog frog) {
        level.playSound(null, frog, SoundEvents.FROG_EAT, SoundSource.NEUTRAL, 2.0F, 1.0F);
        Optional<Entity> tongueTarget = frog.getTongueTarget();
        if (tongueTarget.isEmpty()) return;

        Entity entity = tongueTarget.get();
        if (entity.isAlive()) {
            frog.doHurtTarget(level, entity);
            if (!entity.isAlive()) {
                entity.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    protected void tick(ServerLevel level, Frog frog, long var2) {
        LivingEntity target = MobAI.getAngerTarget(frog).orElse(null);
        if (target == null || !target.isAlive()) return;

        frog.setTongueTarget(target);
        BehaviorUtils.lookAtEntity(frog, target);
        this.attackCooldown--;

        switch (this.state) {
            case MOVE_TO_TARGET -> {
                if (target.distanceTo(frog) <= EATING_DISTANCE) {
                    frog.getNavigation().stop();
                    frog.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
                    if (this.attackCooldown <= 0) {
                        level.playSound(null, frog, SoundEvents.FROG_TONGUE, SoundSource.NEUTRAL, 2.0F, 1.0F);
                        frog.setPose(Pose.USING_TONGUE);
                        target.setDeltaMovement(target.position().vectorTo(frog.position()).normalize().scale(EATING_MOVEMENT_FACTOR));
                        this.eatAnimationTimer = 0;
                        this.state = State.CATCH_ANIMATION;
                    }
                }
                else if (--this.calculatePathCounter <= 0) {
                    frog.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(target.position(), 2.0F, 0));
                    this.calculatePathCounter = 10;
                }
            }
            case CATCH_ANIMATION -> {
                if (++this.eatAnimationTimer >= CATCH_ANIMATION_DURATION) {
                    this.state = State.EAT_ANIMATION;
                    this.eatEntity(level, frog);
                }
            }
            case EAT_ANIMATION -> {
                if (this.eatAnimationTimer++ >= TONGUE_ANIMATION_DURATION) {
                    this.state = State.MOVE_TO_TARGET;
                    this.attackCooldown = 40;
                    frog.setPose(Pose.STANDING);
                }
            }
        }

    }

    private enum State {
        MOVE_TO_TARGET,
        CATCH_ANIMATION,
        EAT_ANIMATION,
    }
}
