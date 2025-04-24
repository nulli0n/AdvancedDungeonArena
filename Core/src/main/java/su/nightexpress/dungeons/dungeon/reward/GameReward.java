package su.nightexpress.dungeons.dungeon.reward;

import org.jetbrains.annotations.NotNull;

public class GameReward {

    private final Reward  reward;
    private final boolean keepOnDeath;
    private final boolean keepOnDefeat;

    public GameReward(@NotNull Reward reward, boolean keepOnDeath, boolean keepOnDefeat) {
        this.reward = reward;
        this.keepOnDeath = keepOnDeath;
        this.keepOnDefeat = keepOnDefeat;
    }

    @NotNull
    public Reward getReward() {
        return this.reward;
    }

    public boolean isKeepOnDeath() {
        return this.keepOnDeath;
    }

    public boolean isKeepOnDefeat() {
        return this.keepOnDefeat;
    }
}
