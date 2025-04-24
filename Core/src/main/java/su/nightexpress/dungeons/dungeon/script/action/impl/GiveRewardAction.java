package su.nightexpress.dungeons.dungeon.script.action.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonTarget;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.reward.GameReward;
import su.nightexpress.dungeons.dungeon.reward.Reward;
import su.nightexpress.dungeons.dungeon.script.action.Action;
import su.nightexpress.dungeons.dungeon.script.action.ActionId;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

public class GiveRewardAction implements Action {

    private final String        rewardId;
    private final DungeonTarget target;
    private final boolean       instant;
    private final boolean       keepOnDeath;
    private final boolean       keepOnDefeat;

    public GiveRewardAction(@NotNull String rewardId, @NotNull DungeonTarget target, boolean instant, boolean keepOnDeath, boolean keepOnDefeat) {
        this.rewardId = rewardId;
        this.target = target;
        this.instant = instant;
        this.keepOnDeath = keepOnDeath;
        this.keepOnDefeat = keepOnDefeat;
    }

    @NotNull
    public static GiveRewardAction load(@NotNull FileConfig config, @NotNull String path) {
        String rewardId = ConfigValue.create(path + ".RewardId", "null").read(config);
        DungeonTarget target = ConfigValue.create(path + ".Target", DungeonTarget.class, DungeonTarget.ALIVE_PLAYERS).read(config);
        boolean instant = ConfigValue.create(path + ".Instant", false).read(config);
        boolean keepOnDeath = ConfigValue.create(path + ".KeepOnDeath", true).read(config);
        boolean keepOnDefeat = ConfigValue.create(path + ".KeepOnDefeat", true).read(config);

        return new GiveRewardAction(rewardId, target, instant, keepOnDeath, keepOnDefeat);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".RewardId", this.rewardId);
        config.set(path + ".Target", this.target.name());
        config.set(path + ".Instant", this.instant);
        config.set(path + ".KeepOnDeath", this.keepOnDeath);
        config.set(path + ".KeepOnDefeat", this.keepOnDefeat);
    }

    @NotNull
    @Override
    public String getName() {
        return ActionId.GIVE_REWARD;
    }

    @Override
    public void perform(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        Reward reward = dungeon.getConfig().getRewardById(this.rewardId);
        if (reward == null) {
            ErrorHandler.error("Invalid reward '" + this.rewardId + "'!", this, dungeon);
            return;
        }

        GameReward gameReward = new GameReward(reward, this.keepOnDeath, this.keepOnDefeat);
        dungeon.giveReward(gameReward, this.instant, this.target, event);
    }
}
