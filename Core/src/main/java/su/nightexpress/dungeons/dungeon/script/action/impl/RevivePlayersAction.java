package su.nightexpress.dungeons.dungeon.script.action.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.script.action.Action;
import su.nightexpress.dungeons.dungeon.script.action.ActionId;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.wrapper.UniInt;

import java.util.concurrent.TimeUnit;

public class RevivePlayersAction implements Action {

    private final boolean checkDeathTime;
    private final UniInt  secondsSinceDeath;

    public RevivePlayersAction(boolean checkDeathTime, @NotNull UniInt secondsSinceDeath) {
        this.checkDeathTime = checkDeathTime;
        this.secondsSinceDeath = secondsSinceDeath;
    }

    @NotNull
    public static RevivePlayersAction load(@NotNull FileConfig config, @NotNull String path) {
        boolean checkDeathTime = config.getBoolean(path + ".Check_Death_Time", false);
        UniInt secondsSinceDeath = UniInt.read(config, path + ".Seconds_Since_Death");

        return new RevivePlayersAction(checkDeathTime, secondsSinceDeath);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Check_Death_Time", this.checkDeathTime);
        this.secondsSinceDeath.write(config, path + ".Seconds_Since_Death");
    }

    @NotNull
    @Override
    public String getName() {
        return ActionId.REVIVE_PLAYERS;
    }

    @Override
    public void perform(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        if (dungeon.isAboutToEnd()) return;

        dungeon.getDeadPlayers().forEach(gamer -> {
            if (!gamer.hasExtraLives()) return;

            if (this.checkDeathTime) {
                long deathTime = gamer.getDeathTime();
                long difference = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - deathTime);

                int min = this.secondsSinceDeath.getMinValue();
                int max = this.secondsSinceDeath.getMaxValue();

                if (min > 0 && difference < min) return;
                if (max > 0 && difference > max) return;

//                if (!TimeUtil.isPassed(deathTime + TimeUnit.SECONDS.toMillis(this.secondsSinceDeath))) {
//                    Bukkit.broadcastMessage("Its too early to revive " + gamer.getPlayer().getName());
//                    return;
//                }
            }

            gamer.revive();
        });
    }
}
