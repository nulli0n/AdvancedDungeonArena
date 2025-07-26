package su.nightexpress.dungeons.nms.mc_1_21_8.brain;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftLivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.api.dungeon.Dungeon;
import su.nightexpress.dungeons.api.dungeon.DungeonHolder;
import su.nightexpress.dungeons.api.type.MobFaction;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class MobAI {

    private static final TargetingConditions IGNORE_LOS                  = TargetingConditions.forCombat().ignoreLineOfSight();
    private static final TargetingConditions IGNORE_INVISIBILITY_AND_LOS = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();

    private static final double     SEE_DISTANCE        = 32D;
    //private static final UniformInt RETREAT_DURATION    = TimeUtil.rangeOfSeconds(5, 20);
    public static final  int        TIME_TO_RECALC_PATH = 20 * 20;

    @NotNull
    public static Set<LivingEntity> getTargetList(@NotNull LivingEntity mob, @NotNull MobFaction faction, @NotNull Dungeon dungeon) {
        Set<org.bukkit.entity.LivingEntity> bukkitEntities = new HashSet<>();
        if (faction == MobFaction.ENEMY) {
            dungeon.getAlivePlayers().forEach(arenaPlayer -> bukkitEntities.add(arenaPlayer.getPlayer()));
            dungeon.getAllyMobs().forEach(dungeonEntity -> bukkitEntities.add(dungeonEntity.getBukkitEntity()));
        }
        else if (faction == MobFaction.ALLY) {
            dungeon.getEnemyMobs().forEach(dungeonEntity -> bukkitEntities.add(dungeonEntity.getBukkitEntity()));
        }

        Set<LivingEntity> targetList = new HashSet<>();
        for (org.bukkit.entity.LivingEntity bukkitEntity : bukkitEntities) {
            LivingEntity entity = ((CraftLivingEntity) bukkitEntity).getHandle();
            targetList.add(entity);
        }

        return targetList;
    }

    @Nullable
    public static LivingEntity getNearestTarget(@NotNull LivingEntity mob, @NotNull MobFaction faction, @NotNull Dungeon dungeon) {
        return getNearestTarget(mob, getTargetList(mob, faction, dungeon));
    }

    @Nullable
    public static LivingEntity getNearestTarget(@NotNull LivingEntity mob, @NotNull Set<LivingEntity> entities) {
        //double followRange = mob.getAttributeValue(Attributes.FOLLOW_RANGE);
        //AABB searchArea = mob.getBoundingBox().inflate(followRange, 4D, followRange);

        double bestDistance = -1D;
        LivingEntity target = null;

        for (LivingEntity entity : entities) {
            if (!IGNORE_LOS.test((ServerLevel) mob.level(), mob, entity)) continue;
            //if (!searchArea.contains(entity.getX(), entity.getY(), entity.getZ())) continue;

            double distance = entity.distanceToSqr(mob);

            // Custom invisibility test, bc otherwise TargetConditions will fail for low range value.
            double visibilityPercent = entity.getVisibilityPercent(mob);
            if (visibilityPercent < 1D && entity.isInvisible()) {
                double seeDistance = SEE_DISTANCE * SEE_DISTANCE;
                double threshold = distance / seeDistance;
                double visibility = visibilityPercent - threshold;
                if (visibility <= 0D) continue;
            }

            if (bestDistance == -1D || distance < bestDistance) {
                bestDistance = distance;
                target = entity;
            }
        }

        return target;
    }

    public static boolean isEntityAttackableIgnoringLineOfSight(LivingEntity mob, LivingEntity target) {
        ServerLevel level = (ServerLevel) mob.level();
        return mob.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, target) ? IGNORE_INVISIBILITY_AND_LOS.test(level, mob, target) : IGNORE_LOS.test(level, mob, target);
    }

    public static boolean setAngerTarget(@NotNull Mob mob, @NotNull LivingEntity target, boolean force) {
        if (!force) {
            if (!isEntityAttackableIgnoringLineOfSight(mob, target)) return false;
        }

        mob.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        mob.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, target.getUUID(), Integer.MAX_VALUE);
        //pet.getBrain().setMemoryWithExpiry(MemoryModuleType.UNIVERSAL_ANGER, true, Integer.MAX_VALUE);
        mob.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
        return true;
    }

    public static void eraseTarget(@NotNull Mob pet) {
        pet.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
        pet.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        pet.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

//    public static void setAvoidTargetAndDontHuntForAWhile(@NotNull Mob pet, @NotNull LivingEntity target) {
//        pet.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
//        pet.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
//        pet.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
//        pet.getBrain().setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, target, RETREAT_DURATION.sample(pet.level().random));
//    }

    @NotNull
    public static Optional<LivingEntity> getAngerTarget(@NotNull Mob pet) {
        return BehaviorUtils.getLivingEntityFromUUIDMemory(pet, MemoryModuleType.ANGRY_AT);
    }

    public static boolean hurt(@NotNull LivingEntity mob, @NotNull DamageSource original, @NotNull Function<DamageSource, Boolean> hurtServer) {
        DamageSource fixed = new DamageSource(original.typeHolder(), original.getDirectEntity(), null, original.sourcePositionRaw());

        if (original.getEntity() instanceof ServerPlayer && mob instanceof DungeonHolder dungeonMob && dungeonMob.getFaction() == MobFaction.ALLY) {
            return false;
        }

        boolean flag = hurtServer.apply(fixed);

        if (original.getEntity() instanceof LivingEntity damager) {
            mob.setLastHurtByMob(damager);
        }
        if (original.getEntity() instanceof ServerPlayer player) {
            mob.setLastHurtByPlayer(player, LivingEntity.PLAYER_HURT_EXPERIENCE_TIME);
        }

        return flag;
    }
}
