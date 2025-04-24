package su.nightexpress.dungeons.selection.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.selection.SelectionManager;
import su.nightexpress.nightcore.manager.AbstractListener;

import java.util.stream.Stream;

public class SelectionListener extends AbstractListener<DungeonPlugin> {

    private final SelectionManager manager;

    public SelectionListener(@NotNull DungeonPlugin plugin, @NotNull SelectionManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onQuit(PlayerQuitEvent event) {
        this.manager.removeAll(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        this.manager.removeAll(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || block.getType().isAir()) return;

        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;

        if (!this.manager.isItem(itemStack)) return;

        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        Player player = event.getPlayer();
        this.manager.onItemUse(player, block, event.getAction());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (!this.manager.isItem(itemStack)) return;

        itemStack.setAmount(0);
        event.setCancelled(true);
        this.manager.onItemDrop(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onItemMove(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item != null && this.manager.isItem(item)) {
            Inventory inventory = event.getInventory();
            if (inventory.getType() != InventoryType.CRAFTING) {
                event.setCancelled(true);
                return;
            }
        }

        int hotkey = event.getHotbarButton();
        if (hotkey >= 0) {
            Player player = (Player) event.getWhoClicked();
            ItemStack hotItem = player.getInventory().getItem(hotkey);
            if (hotItem != null && this.manager.isItem(hotItem)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onItemCraft(PrepareItemCraftEvent event) {
        if (Stream.of(event.getInventory().getMatrix()).anyMatch(item -> item != null && this.manager.isItem(item))) {
            event.getInventory().setResult(null);
        }
    }
}
