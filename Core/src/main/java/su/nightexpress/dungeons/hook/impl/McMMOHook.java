package su.nightexpress.dungeons.hook.impl;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.DungeonsAPI;
import su.nightexpress.nightcore.manager.AbstractListener;

public final class McMMOHook extends AbstractListener<DungeonPlugin> {

    private static McMMOHook instance;

    private McMMOHook(@NotNull DungeonPlugin plugin) {
        super(plugin);
        this.registerListeners();
    }

    public static void setup() {
        if (instance == null) {
            instance = new McMMOHook(DungeonsAPI.getPlugin());
        }
    }

    public static void shutdown() {
        if (instance != null) {
            instance.unregisterListeners();
            instance = null;
        }
    }

    @EventHandler
    public void onUseSkill(McMMOPlayerAbilityActivateEvent e) {
//        Player p = e.getPlayer();
//        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(p);
//        if (arenaPlayer == null) return;
//
//        Arena arena = arenaPlayer.getArena();
//        if (!arena.getConfig().getGameplaySettings().isMcmmoAllowed()) {
//            e.setCancelled(true);
//        }
    }

    @EventHandler
    public void onExpGain(McMMOPlayerXpGainEvent e) {
//        Player p = e.getPlayer();
//        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(p);
//        if (arenaPlayer == null) return;
//
//        Arena arena = arenaPlayer.getArena();
//        if (!arena.getConfig().getGameplaySettings().isMcmmoAllowed()) {
//            e.setCancelled(true);
//        }
    }
}
