package su.nightexpress.dungeons.dungeon.listener;

import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.dungeon.DungeonManager;
import su.nightexpress.nightcore.manager.AbstractListener;

@SuppressWarnings("UnstableApiUsage")
public class DungeonProtectionListener extends AbstractListener<DungeonPlugin> {

    private final DungeonManager manager;

    public DungeonProtectionListener(@NotNull DungeonPlugin plugin, @NotNull DungeonManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDungeonEntityExplode(EntityExplodeEvent event) {
        if (event.blockList().stream().anyMatch(this.manager::isDungeonLocation)) {
            event.blockList().clear();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDungeonEntityBlockChange(EntityChangeBlockEvent event) {
        if (this.manager.isDungeonLocation(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDungeonEntityBlockForm(EntityBlockFormEvent event) {
        if (this.manager.isDungeonLocation(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDungeonEntityBreakVehicle(VehicleDamageEvent event) {
        if (!this.manager.canBreakDecoration(event.getAttacker(), event.getVehicle())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDungeonEntityBreakDecorations(EntityDamageEvent event) {
        Entity decoration = event.getEntity();
        if (decoration instanceof LivingEntity) return;

        DamageSource source = event.getDamageSource();
        Entity damager = source.getCausingEntity();
        if (!this.manager.canBreakDecoration(damager, decoration)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDungeonEntityBreakPainting(HangingBreakByEntityEvent event) {
        if (!this.manager.canBreakDecoration(event.getRemover(), event.getEntity())) {
            event.setCancelled(true);
        }
    }





    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDungeonNaturalBlockExplode(BlockExplodeEvent event) {
        if (event.blockList().stream().anyMatch(this.manager::isDungeonLocation)) {
            event.blockList().clear();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDungeonNaturalBlockBurn(BlockBurnEvent event) {
        Block block = event.getBlock();
        if (this.manager.isDungeonLocation(block)) {
            event.setCancelled(true);
        }
    }





    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDungeonPlayerBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (!this.manager.canBuild(player, block)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDungeonPlayerBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (!this.manager.canPlace(player, block, event.getItemInHand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDungeonPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!this.manager.canInteract(event.getPlayer(), event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDungeonPlayerInteractEntity2(PlayerInteractAtEntityEvent event) {
        if (!this.manager.canInteract(event.getPlayer(), event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDungeonPlayerInteractStand(PlayerArmorStandManipulateEvent event) {
        if (!this.manager.canInteract(event.getPlayer(), event.getRightClicked())) {
            event.setCancelled(true);
        }
    }
}
