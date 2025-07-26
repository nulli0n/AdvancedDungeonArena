package su.nightexpress.dungeons.hook.impl;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.compat.GodPlugin;
import su.nightexpress.dungeons.hook.HookId;

public class EssentialsHook implements GodPlugin {

    private final Essentials essentials;

    public EssentialsHook() {
        this.essentials = (Essentials) Bukkit.getPluginManager().getPlugin(HookId.ESSENTIALS);
    }

    @Override
    public boolean isGodEnabled(@NotNull Player player) {
        return this.essentials.getUser(player).isGodModeEnabled();
    }

    public void disableGod(@NotNull Player player) {
        this.essentials.getUser(player).setGodModeEnabled(false);
    }

    @Override
    public void enableGod(@NotNull Player player) {
        this.essentials.getUser(player).setGodModeEnabled(true);
    }
}
