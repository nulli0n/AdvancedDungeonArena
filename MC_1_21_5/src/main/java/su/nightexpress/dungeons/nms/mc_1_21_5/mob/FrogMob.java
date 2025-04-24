package su.nightexpress.dungeons.nms.mc_1_21_5.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonHolder;
import su.nightexpress.dungeons.nms.mc_1_21_5.brain.MobBrain;
import su.nightexpress.dungeons.nms.mc_1_21_5.brain.behavior.MobCoreBehaviors;
import su.nightexpress.dungeons.nms.mc_1_21_5.brain.behavior.MobFightBehaviors;
import su.nightexpress.dungeons.nms.mc_1_21_5.brain.behavior.MobIdleBehaviors;
import su.nightexpress.dungeons.nms.mc_1_21_5.brain.behavior.impl.ShootTongue;

public class FrogMob extends Frog implements DungeonHolder {

    private static final UniformInt TIME_BETWEEN_LONG_JUMPS = UniformInt.of(100, 140);

    public FrogMob(@NotNull ServerLevel world) {
        super(EntityType.FROG, world);
        this.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, TIME_BETWEEN_LONG_JUMPS.sample(this.random));
    }

    @Override
    protected Brain.Provider<Frog> brainProvider() {
        return MobBrain.brainProvider(this);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @NotNull
    public Brain<Frog> refreshBrain(@NotNull Frog pet, @NotNull Brain<Frog> brain) {
        BehaviorControl<LivingEntity> cooldownTicks = new CountDownCooldownTicks(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS);

        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            MobCoreBehaviors.lookAtTarget(),
            MobCoreBehaviors.moveToTarget(),
            //PetCoreBehaviors.swim(),
            cooldownTicks,
            MobFightBehaviors.stopAngryIfTargetDead())
        );

        brain.addActivity(Activity.IDLE, 10, ImmutableList.of(
            new RunOne<>(
                ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                ImmutableList.of(Pair.of(new Croak(), 3))
            ),
            MobIdleBehaviors.followOwner(),
            MobFightBehaviors.autoTargetAndAttack())
        );

        brain.addActivity(Activity.FIGHT, 10, ImmutableList.of(
            MobFightBehaviors.stopAttackIfTargetInvalid(pet),
            new ShootTongue())
        );

        brain.addActivityWithConditions(Activity.SWIM, ImmutableList.of(
            Pair.of(2, MobFightBehaviors.autoTargetAndAttack()),
            Pair.of(5, new GateBehavior<>(
                ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                ImmutableSet.of(),
                GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.TRY_ALL,
                ImmutableList.of(
                    Pair.of(RandomStroll.swim(0.75F), 1),
                    Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 1),
                    Pair.of(BehaviorBuilder.triggerIf(Entity::isInWater), 5)))
            )),
            ImmutableSet.of()
        );

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        this.brain = brain;
        return brain;
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        filler.push("frogBrain");
        this.getBrain().tick(level, this);
        filler.pop();
        filler.push("frogActivityUpdate");
        MobBrain.updateActivity(this, this.brain);
        filler.pop();
        super.customServerAiStep(level);
    }
}
