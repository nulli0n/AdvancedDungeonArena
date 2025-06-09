package su.nightexpress.dungeons.api.dungeon;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.criteria.CriterionMob;
import su.nightexpress.dungeons.api.mob.MobIdentifier;
import su.nightexpress.dungeons.api.mob.MobProvider;
import su.nightexpress.dungeons.api.mob.MobSnapshot;

import java.util.UUID;

public interface DungeonEntity extends CriterionMob {

    @NotNull MobSnapshot getSnapshot();

    @NotNull Dungeon getDungeon();

    @NotNull UUID getUniqueId();

//    boolean isMob(@NotNull MobProvider provider, @NotNull String mobId);
//
//    boolean isMob(@NotNull MobIdentifier identifier);
//
//    boolean isId(@NotNull String mobId);
//
//    boolean isProvider(@NotNull MobProvider provider);
//
//    boolean isFaction(@NotNull MobFaction faction);

    boolean isDead();

    boolean isAlive();

//    @NotNull String getProviderId();

    @NotNull LivingEntity getBukkitEntity();

//    @NotNull MobFaction getFaction();

    @NotNull MobProvider getProvider();

    @NotNull MobIdentifier getIdentifier();

//    @NotNull String getMobId();
}
