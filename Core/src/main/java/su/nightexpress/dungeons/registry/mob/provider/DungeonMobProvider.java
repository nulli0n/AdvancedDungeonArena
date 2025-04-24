package su.nightexpress.dungeons.registry.mob.provider;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.api.dungeon.Dungeon;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.api.mob.MobProvider;
import su.nightexpress.dungeons.registry.mob.MobProviderId;
import su.nightexpress.dungeons.util.MobUitls;

import java.util.List;
import java.util.function.Consumer;

public class DungeonMobProvider implements MobProvider {

    private final DungeonPlugin plugin;

    public DungeonMobProvider(@NotNull DungeonPlugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @Override
    public String getName() {
        return MobProviderId.ADA;
    }

    @Nullable
    @Override
    public LivingEntity spawn(@NotNull Dungeon arena, @NotNull String mobId, @NotNull MobFaction faction, @NotNull Location location, int level, @Nullable Consumer<LivingEntity> prespawn) {
        return plugin.getMobManager().spawnIternalMob(arena, faction, mobId, location, level, prespawn);
    }

    @NotNull
    @Override
    public List<String> getMobNames() {
        return plugin.getMobManager().getTemplateIds();
    }

    @Override
    public boolean isProducedBy(@NotNull LivingEntity entity) {
        return MobUitls.isTemplateMob(entity);
    }

    @Override
    @Nullable
    public String getMobId(@NotNull LivingEntity entity) {
        return MobUitls.getTemplateId(entity);
    }
}
