package su.nightexpress.dungeons.api.mob;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.api.dungeon.Dungeon;
import su.nightexpress.dungeons.api.type.MobFaction;

import java.util.List;
import java.util.function.Consumer;

public interface MobProvider {

    @NotNull String getName();

    @Nullable LivingEntity spawn(@NotNull Dungeon arena, @NotNull String mobId, @NotNull MobFaction faction, @NotNull Location location, int level, @Nullable Consumer<LivingEntity> prespawn);

    @Deprecated
    @NotNull List<String> getMobNames();

    boolean isProducedBy(@NotNull LivingEntity entity);

    @Nullable String getMobId(@NotNull LivingEntity entity);
}
