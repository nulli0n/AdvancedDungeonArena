package su.nightexpress.dungeons.registry.mob.provider;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.api.dungeon.Dungeon;
import su.nightexpress.dungeons.api.mob.MobProvider;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.hook.impl.MythicMobsHook;
import su.nightexpress.dungeons.registry.mob.MobProviderId;

import java.util.List;
import java.util.function.Consumer;

public class MythicMobProvider implements MobProvider {

    @NotNull
    @Override
    public String getName() {
        return MobProviderId.MYTHIC_MOBS;
    }

    @Nullable
    @Override
    public LivingEntity spawn(@NotNull Dungeon arena, @NotNull String mobId, @NotNull MobFaction faction, @NotNull Location location, int level, @Nullable Consumer<LivingEntity> prespawn) {
        MythicMob mythicMob = MythicMobsHook.getMobConfig(mobId);
        if (mythicMob == null) return null;

        ActiveMob mob = mythicMob.spawn(BukkitAdapter.adapt(location), level, SpawnReason.OTHER, entity -> {
            if (prespawn != null && entity instanceof LivingEntity livingEntity) {
                prespawn.accept(livingEntity);
            }
        });

        if (!(mob.getEntity().getBukkitEntity() instanceof LivingEntity entity)) {
            mob.remove();
            return null;
        }

        return entity;
    }

    @NotNull
    @Override
    public List<String> getMobNames() {
        return MythicMobsHook.getMobConfigIds();
    }

    @Override
    public boolean isProducedBy(@NotNull LivingEntity entity) {
        return MythicMobsHook.isMythicMob(entity);
    }

    @Override
    @Nullable
    public String getMobId(@NotNull LivingEntity entity) {
        return MythicMobsHook.getMobInternalName(entity);
    }
}
