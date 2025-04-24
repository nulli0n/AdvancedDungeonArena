package su.nightexpress.dungeons.registry.level.provider;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.hook.HookId;
import su.nightexpress.dungeons.registry.level.LevelProvider;

public class MMOCoreLevelProvider implements LevelProvider {

    @NotNull
    @Override
    public String getName() {
        return HookId.MMOCORE;
    }

    @Override
    public int getLevel(@NotNull Player player) {
        return PlayerData.get(player).getLevel();
    }
}
