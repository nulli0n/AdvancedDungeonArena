package su.nightexpress.dungeons.nms.mc_26_1_1.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.nms.mc_26_1_1.brain.behavior.MobCoreBehaviors;
import su.nightexpress.dungeons.nms.mc_26_1_1.brain.behavior.MobFightBehaviors;
import su.nightexpress.dungeons.nms.mc_26_1_1.brain.behavior.MobIdleBehaviors;

public class MobBrain {

    protected static final ImmutableList<SensorType<? extends Sensor<? extends LivingEntity>>> SENSOR_TYPES;
    protected static final ImmutableList<MemoryModuleType<?>>                                  MEMORY_TYPES;

    static {
        SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_ITEMS,
            SensorType.HURT_BY
            //SensorType.PIGLIN_SPECIFIC_SENSOR
        );
        MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.IS_IN_WATER,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.LIKED_PLAYER,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
            MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.INTERACTION_TARGET,
            MemoryModuleType.PATH,
            MemoryModuleType.ANGRY_AT,
            MemoryModuleType.UNIVERSAL_ANGER,
            MemoryModuleType.AVOID_TARGET,
            MemoryModuleType.RIDE_TARGET,
            MemoryModuleType.ATE_RECENTLY,
            MemoryModuleType.RAM_COOLDOWN_TICKS,
            MemoryModuleType.RAM_TARGET,
            MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS,
            MemoryModuleType.NEAREST_REPELLENT);
    }

    public static <E extends Mob> ImmutableList<SensorType<? extends Sensor<? super E>>> getSensorTypes(@NotNull E entity) {
        return ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_ITEMS,
            SensorType.HURT_BY,
            SensorType.PIGLIN_SPECIFIC_SENSOR);
    }

//    public static Player getOwner(@NotNull Mob pet) {
//        Optional<UUID> optional = pet.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);
//        return optional.map(id -> pet.level().getPlayerByUUID(id)).orElse(null);
//    }
//
//    public static void setOwnerMemory(@NotNull Mob pet, @NotNull Player player) {
//        pet.getBrain().setMemory(MemoryModuleType.LIKED_PLAYER, player.getUUID());
//    }

    public static <E extends Mob> Brain.Provider<E> brainProvider(@NotNull E entity) {
//        return Brain.provider(MEMORY_TYPES, getSensorTypes(entity));
        return Brain.provider(getSensorTypes(entity));
    }

    public static <E extends Mob> Brain.Provider<E> brainProvider(@NotNull E entity, @NotNull Brain.ActivitySupplier<E> activities) {
        return Brain.provider(getSensorTypes(entity), activities);
    }

    @NotNull
    public static <E extends Mob> Brain<@NotNull E> refreshBrain(@NotNull E pet, @NotNull Brain<@NotNull E> brain) {
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        // noinspection unchecked
        MobBrainConfigurable.optionalCast(pet).ifPresent(configurable -> configurable.setBrain((Brain<@NotNull Mob>) brain));

        return brain;
    }

    public static <E extends Mob> ImmutableList<ActivityData<E>> getActivities(@NotNull E entity) {
        return ImmutableList.of(
                ActivityData.create(Activity.CORE, 0, ImmutableList.of(
                        MobCoreBehaviors.lookAtTarget(),
                        MobCoreBehaviors.moveToTarget(),
                        MobCoreBehaviors.swim(),
                        MobFightBehaviors.stopAngryIfTargetDead()
                )),
                ActivityData.create(Activity.IDLE, 10, ImmutableList.of(
                        MobIdleBehaviors.followOwner(),
                        MobFightBehaviors.autoTargetAndAttack()
                )),
                ActivityData.create(Activity.FIGHT, 10, ImmutableList.of(
                        MobFightBehaviors.stopAttackIfTargetInvalid(entity),
                        MobFightBehaviors.reachTargetWhenOutOfRange(),
                        MobFightBehaviors.meleeAttack()
                ))
        );
    }

    public static <T extends LivingEntity> void updateActivity(@NotNull Mob entity, @NotNull Brain<T> brain) {
        if (MobAI.getAngerTarget(entity).isPresent()) {
            brain.setActiveActivityIfPossible(Activity.FIGHT);
        }
        else if (entity.isInWater()) {
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.SWIM));
        }
        else {
            brain.setActiveActivityIfPossible(Activity.IDLE);
        }
        entity.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }
}
