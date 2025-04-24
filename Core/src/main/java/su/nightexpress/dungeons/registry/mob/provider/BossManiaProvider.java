package su.nightexpress.dungeons.registry.mob.provider;

import it.dado997.BossMania.BossMania;
import it.dado997.BossMania.Objects.ActiveBoss;
import it.dado997.BossMania.Objects.Boss;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.api.dungeon.Dungeon;
import su.nightexpress.dungeons.api.mob.MobProvider;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.registry.mob.MobProviderId;

import java.util.List;
import java.util.function.Consumer;

public class BossManiaProvider implements MobProvider {

    @NotNull
    @Override
    public String getName() {
        return MobProviderId.BOSS_MANIA;
    }

    @Nullable
    @Override
    public LivingEntity spawn(@NotNull Dungeon arena, @NotNull String mobId, @NotNull MobFaction faction, @NotNull Location location, int level, @Nullable Consumer<LivingEntity> prespawn) {
        Boss boss = BossMania.api.getBosses().find(mobId);
        if (boss == null) return null;

        LivingEntity entity = boss.spawn(location).getLivingEntity();
        if (prespawn != null) {
            prespawn.accept(entity);
        }
        return entity;
    }

    @NotNull
    @Override
    public List<String> getMobNames() {
        return BossMania.api.getBosses().stream().map(Boss::getKey).toList();
    }

    @Override
    public boolean isProducedBy(@NotNull LivingEntity entity) {
        ActiveBoss boss = BossMania.api.getBoss(entity);
        return boss != null;
    }

    @Override
    @Nullable
    public String getMobId(@NotNull LivingEntity entity) {
        ActiveBoss boss = BossMania.api.getBoss(entity);
        return boss == null ? null : boss.getType().getKey();
    }
}
