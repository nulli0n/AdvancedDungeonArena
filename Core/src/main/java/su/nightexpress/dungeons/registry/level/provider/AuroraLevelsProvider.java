package su.nightexpress.dungeons.registry.level.provider;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.hook.HookId;
import su.nightexpress.dungeons.registry.level.LevelProvider;

public class AuroraLevelsProvider implements LevelProvider {

    @NotNull
    @Override
    public String getName() {
        return HookId.AURORA_LEVELS;
    }

    @Override
    public int getLevel(@NotNull Player player) {
        return gg.auroramc.levels.api.AuroraLevelsProvider.getLeveler().getUserData(player).getLevel();
    }
}
