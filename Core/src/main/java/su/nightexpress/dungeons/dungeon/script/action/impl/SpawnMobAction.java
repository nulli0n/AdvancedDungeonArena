package su.nightexpress.dungeons.dungeon.script.action.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonSpawner;
import su.nightexpress.dungeons.api.mob.MobIdentifier;
import su.nightexpress.dungeons.api.mob.MobProvider;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.scale.ScalableAmount;
import su.nightexpress.dungeons.dungeon.script.action.Action;
import su.nightexpress.dungeons.dungeon.script.action.ActionId;
import su.nightexpress.dungeons.registry.mob.MobRegistry;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.FileConfig;

public class SpawnMobAction implements Action {

    private final MobIdentifier  mobId;
    private final String         spawnerId;
    private final ScalableAmount amount;
    private final ScalableAmount level;

    public SpawnMobAction(@NotNull MobIdentifier mobId, @NotNull String spawnerId, @NotNull ScalableAmount amount, @NotNull ScalableAmount level) {
        this.mobId = mobId;
        this.spawnerId = spawnerId;
        this.amount = amount;
        this.level = level;
    }

    @NotNull
    public static SpawnMobAction load(@NotNull FileConfig config, @NotNull String path) {
        MobIdentifier mobId = MobIdentifier.read(config, path + ".MobId");
        String spawnerId = config.getString(path + ".SpawnerId", "null");
        ScalableAmount amount = ScalableAmount.read(config, path + ".Amount");
        ScalableAmount level = ScalableAmount.read(config, path + ".Level");

        return new SpawnMobAction(mobId, spawnerId, amount, level);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".MobId", this.mobId.serialize());
        config.set(path + ".SpawnerId", this.spawnerId);
        config.set(path + ".Amount", this.amount);
        config.set(path + ".Level", this.level);
    }

    @NotNull
    @Override
    public String getName() {
        return ActionId.SPAWN_MOB;
    }

    @Override
    public void perform(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        DungeonSpawner spawner = dungeon.getConfig().getSpawnerById(this.spawnerId);
        if (spawner == null) {
            ErrorHandler.error("Invalid spawner '" + this.spawnerId + "'!", this, dungeon);
            return;
        }

        MobProvider provider = MobRegistry.getProviderByName(this.mobId.getProviderId());
        if (provider == null) {
            ErrorHandler.error("Invalid mob provider '" + this.mobId.getProviderId() + "'!", this, dungeon);
            return;
        }

        int amount = this.amount.getScaledInt(dungeon);
        int level = this.level.getScaledInt(dungeon);

        dungeon.spawnMob(provider, this.mobId.getMobId(), MobFaction.ENEMY, spawner, level, amount);
    }
}
