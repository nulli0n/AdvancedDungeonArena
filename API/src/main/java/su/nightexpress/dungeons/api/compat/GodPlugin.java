package su.nightexpress.dungeons.api.compat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface GodPlugin {

    boolean isGodEnabled(@NotNull Player player);

    void disableGod(@NotNull Player player);

    void enableGod(@NotNull Player player);
}
