package su.nightexpress.dungeons.api.compat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface BoardPlugin {

    boolean isBoardEnabled(@NotNull Player player);

    void disableBoard(@NotNull Player player);

    void enableBoard(@NotNull Player player);
}
