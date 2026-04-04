package su.nightexpress.dungeons.nms.mc_1_21_10.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.RamTarget;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonHolder;
import su.nightexpress.dungeons.nms.mc_1_21_10.brain.MobAI;
import su.nightexpress.dungeons.nms.mc_1_21_10.brain.MobBrain;
import su.nightexpress.dungeons.nms.mc_1_21_10.brain.behavior.MobCoreBehaviors;
import su.nightexpress.dungeons.nms.mc_1_21_10.brain.behavior.MobFightBehaviors;
import su.nightexpress.dungeons.nms.mc_1_21_10.brain.behavior.MobIdleBehaviors;
import su.nightexpress.dungeons.nms.mc_1_21_10.brain.behavior.impl.PrepareRamTarget;

public class GoatMob extends Goat implements DungeonHolder {

    public static final  UniformInt          TIME_BETWEEN_RAMS     = UniformInt.of(100, 300);
    private static final TargetingConditions RAM_TARGET_CONDITIONS = TargetingConditions.forCombat().selector((level, entity) -> {
        return true;
    });
    public static final  int                 RAM_MIN_DISTANCE      = 4;

    public GoatMob(@NotNull ServerLevel level) {
        super(EntityType.GOAT, level);
        this.getBrain().setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, TIME_BETWEEN_RAMS.sample(this.level().random));
    }

    @Override
    protected Brain.Provider<Goat> brainProvider() {
        return MobBrain.brainProvider(this);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @NotNull
    public Brain<Goat> refreshBrain(@NotNull Goat pet, @NotNull Brain<Goat> brain) {
        BehaviorControl<LivingEntity> cooldownTicks = new CountDownCooldownTicks(MemoryModuleType.RAM_COOLDOWN_TICKS);

        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            MobCoreBehaviors.lookAtTarget(),
            MobCoreBehaviors.moveToTarget(),
            MobCoreBehaviors.swim(),
            cooldownTicks,
            MobFightBehaviors.stopAngryIfTargetDead())
        );

        brain.addActivity(Activity.IDLE, 10, ImmutableList.of(
            MobIdleBehaviors.followOwner(),
            MobFightBehaviors.autoTargetAndAttack())
        );

        brain.addActivity(Activity.FIGHT, 10, ImmutableList.of(
            MobFightBehaviors.stopAttackIfTargetInvalid(pet),
            MobFightBehaviors.reachTargetWhenOutOfRange(),
            MobFightBehaviors.meleeAttack())
        );

        brain.addActivityWithConditions(Activity.RAM,
            ImmutableList.of(
                Pair.of(0, new RamTarget(
                    goat -> TIME_BETWEEN_RAMS,
                    RAM_TARGET_CONDITIONS,
                    3.0F,
                    goat -> goat.isBaby() ? 1.0D : 2.5D,
                    goat -> goat.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_RAM_IMPACT : SoundEvents.GOAT_RAM_IMPACT,
                    goat -> SoundEvents.GOAT_HORN_BREAK)
                ),
                Pair.of(1, new PrepareRamTarget<>(
                    4, 10, 1.5F, 20,
                    goat -> goat.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_PREPARE_RAM : SoundEvents.GOAT_PREPARE_RAM)
                )
            ),
            ImmutableSet.of(
                Pair.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT)
            )
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
        filler.push("goatBrain");
        this.getBrain().tick(level, this);
        filler.pop();
        filler.push("goatActivityUpdate");
        this.updateActivity();
        filler.pop();
    }

    protected void updateActivity() {
        Brain<Goat> brain = this.getBrain();
        if (MobAI.getAngerTarget(this).isPresent()) {
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.RAM, Activity.FIGHT));
        }
        else {
            brain.setActiveActivityIfPossible(Activity.IDLE);
        }
        this.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

    @Override
    protected void ageBoundaryReached() {

    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public boolean dropHorn() {
        return false;
    }
}
