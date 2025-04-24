package su.nightexpress.dungeons.dungeon.mob;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonEntity;
import su.nightexpress.dungeons.api.mob.MobIdentifier;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.api.mob.MobProvider;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;

import java.util.UUID;

public class DungeonMob implements DungeonEntity {

    private final DungeonInstance dungeon;
    private final LivingEntity    bukkitEntity;
    private final MobFaction      faction;
    private final MobProvider     provider;
    private final String          mobId;
    private final MobIdentifier   identifier;

    public DungeonMob(@NotNull DungeonInstance dungeon,
                      @NotNull LivingEntity bukkitEntity,
                      @NotNull MobFaction faction,
                      @NotNull MobProvider provider,
                      @NotNull String mobId) {
        this.dungeon = dungeon;
        this.bukkitEntity = bukkitEntity;
        this.faction = faction;
        this.provider = provider;
        this.mobId = mobId;
        this.identifier = MobIdentifier.from(this.provider, this.mobId);
    }

    @NotNull
    @Override
    public DungeonInstance getDungeon() {
        return this.dungeon;
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        return this.bukkitEntity.getUniqueId();
    }

    @Override
    public boolean isMob(@NotNull MobProvider provider, @NotNull String mobId) {
        return this.isProvider(provider) && this.isId(mobId);
    }

    @Override
    public boolean isMob(@NotNull MobIdentifier identifier) {
        return this.provider.getName().equalsIgnoreCase(identifier.getProviderId()) && this.isId(identifier.getMobId());
    }

    @Override
    public boolean isId(@NotNull String mobId) {
        return this.mobId.equalsIgnoreCase(mobId);
    }

    @Override
    public boolean isProvider(@NotNull MobProvider provider) {
        return this.provider == provider;
    }

    @Override
    public boolean isFaction(@NotNull MobFaction faction) {
        return this.faction == faction;
    }

    @Override
    public boolean isDead() {
        return this.bukkitEntity.isDead() || !this.bukkitEntity.isValid();
    }

    @Override
    public boolean isAlive() {
        return !this.isDead();
    }

    @Override
    @NotNull
    public MobIdentifier getIdentifier() {
        return this.identifier;
    }

    @Override
    @NotNull
    public String getProviderId() {
        return this.provider.getName();
    }

    @Override
    @NotNull
    public LivingEntity getBukkitEntity() {
        return this.bukkitEntity;
    }

    @Override
    @NotNull
    public MobFaction getFaction() {
        return this.faction;
    }

    @Override
    @NotNull
    public MobProvider getProvider() {
        return this.provider;
    }

    @Override
    @NotNull
    public String getMobId() {
        return this.mobId;
    }
}
