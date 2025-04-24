package su.nightexpress.dungeons.kit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.api.type.GameState;
import su.nightexpress.dungeons.dungeon.player.DungeonGamer;
import su.nightexpress.dungeons.kit.impl.Kit;
import su.nightexpress.nightcore.manager.AbstractListener;

public class KitListener extends AbstractListener<DungeonPlugin> {

    private final KitManager manager;

    public KitListener(@NotNull DungeonPlugin plugin, KitManager manager) {
        super(plugin);
        this.manager = manager;
    }

//    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//    public void onPlayerJoin(PlayerJoinEvent event) {
//        Player player = event.getPlayer();
//        ArenaUser user = plugin.getUserManager().getUserData(player);
//
//        this.manager.getKits().forEach(kit -> {
//            if (!user.hasKit(kit) && kit.hasPermission(player) && !kit.hasCost()) {
//                user.addKit(kit);
//            }
//        });
//    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onKitSharePickup(EntityPickupItemEvent event) {
        if (KitUtils.isItemShareAllowed()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        DungeonGamer gamer = plugin.getDungeonManager().getDungeonPlayer(player);
        if (gamer == null) return;

        ItemStack itemStack = event.getItem().getItemStack();
        Kit kit = this.manager.getKitByItem(itemStack);
        if (kit == null) return;

        if (gamer.getKit() != kit) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onKitSharePlace(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.CRAFTING) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        DungeonGamer gamer = plugin.getDungeonManager().getDungeonPlayer(player);
        if (gamer == null) return;

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType().isAir()) return;

        Kit kit = this.manager.getKitByItem(itemStack);
        if (kit == null) return;

        if (gamer.getKit() != kit || gamer.getDungeon().getState() != GameState.INGAME) {
            event.setCancelled(true);
        }
    }
}
