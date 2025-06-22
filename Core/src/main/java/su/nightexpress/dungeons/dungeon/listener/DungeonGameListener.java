package su.nightexpress.dungeons.dungeon.listener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.api.type.GameState;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.config.Perms;
import su.nightexpress.dungeons.dungeon.DungeonManager;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.mob.DungeonMob;
import su.nightexpress.dungeons.dungeon.module.GameSettings;
import su.nightexpress.dungeons.dungeon.player.DungeonGamer;
import su.nightexpress.dungeons.util.MobUitls;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.nightcore.util.CommandUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.placeholder.Replacer;

@SuppressWarnings("UnstableApiUsage")
public class DungeonGameListener extends AbstractListener<DungeonPlugin> {

    private final DungeonManager manager;

    public DungeonGameListener(@NotNull DungeonPlugin plugin, @NotNull DungeonManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDungeonGenericFriendlyFire(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity victim)) return;

        // Avoid damage in lobby
        if (victim instanceof Player player) {
            DungeonInstance instance = this.manager.getInstance(player);
            if (instance != null && instance.getState() != GameState.INGAME) {
                event.setCancelled(true);
                return;
            }
        }

        DamageSource source = event.getDamageSource();
        if (!(source.getCausingEntity() instanceof LivingEntity damager)) return;

        if (!this.manager.canDamage(damager, victim)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDungeonGenericItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        DungeonInstance dungeon = this.manager.getInstanceByLocation(item.getLocation());
        if (dungeon == null) return;

        dungeon.addGroundItem(item);
    }



    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDungeonPlayerChat(AsyncPlayerChatEvent event) {
        if (!Config.CHAT_ENABLED.get()) return;

        Player player = event.getPlayer();
        DungeonGamer gamer = this.manager.getDungeonPlayer(player);
        if (gamer == null) return;

        event.setCancelled(true);

        DungeonInstance dungeon = gamer.getDungeon();

        String format = Replacer.create()
            .replace(dungeon.replacePlaceholders())
            .replace(gamer.replacePlaceholders())
            .replace(Placeholders.forPlayerWithPAPI(player))
            .replace(Placeholders.GENERIC_MESSAGE, event.getMessage())
            .apply(Config.CHAT_FORMAT.get());

        dungeon.broadcast(format);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDungeonPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(Perms.BYPASS_DUNGEON_COMMANDS)) return;

        DungeonGamer gamer = this.manager.getDungeonPlayer(player);
        if (gamer == null) return;

        String commandName = CommandUtil.getCommandName(event.getMessage());
        if (Lists.contains(plugin.getCommandAliases(), commandName)) return;

        DungeonInstance dungeon = gamer.getDungeon();
        if (dungeon.getConfig().gameSettings().isAllowedCommand(commandName)) return;

        event.setCancelled(true);

        dungeon.sendMessage(player, Lang.DUNGEON_GAME_BAD_COMMAND, replacer -> replacer.replace(dungeon.replacePlaceholders()).replace(Placeholders.GENERIC_COMMAND, commandName));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDungeonPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        DungeonGamer gamer = this.manager.getDungeonPlayer(player);
        if (gamer == null) return;

        DungeonInstance dungeon = gamer.getDungeon();

        Location location = gamer.getDeathLocation();
        if (location == null) location = dungeon.getLevel().getSpawnLocation(dungeon.getWorld());

        event.setRespawnLocation(location);
        gamer.handleRespawn(); // FIXME When set Spectator right in RespawnEvent, spectator hotbar & noclip are disabled (good), but this is MC bug (?), need better solution.
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDungeonPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        DungeonGamer gamer = this.manager.getDungeonPlayer(player);
        if (gamer == null) return;

        DungeonInstance dungeon = gamer.getDungeon();

        if (dungeon.getConfig().gameSettings().isKeepInventoryEnabled()) {
            if (gamer.hasExtraLives() || !dungeon.getConfig().gameSettings().isKeepInventoryRequiresLives()) {
                event.setKeepInventory(true);
                event.getDrops().clear();
            }
        }

        event.setDroppedExp(0);
        event.setKeepLevel(true);

        dungeon.handlePlayerDeath(gamer);

        this.plugin.runTask(task -> player.spigot().respawn());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDungeonPlayerMovement(PlayerMoveEvent event) {
        Location to = event.getTo();
        if (to == null) return;

        Location from = event.getFrom();

        Player player = event.getPlayer();
        DungeonGamer gamer = this.manager.getDungeonPlayer(player);
        if (gamer == null) return;

        if (gamer.isAlive()) {
            if (to.getX() == from.getX() && to.getZ() == from.getZ()) return;
        }
        else {
            if (to.getX() == from.getX() && to.getZ() == from.getZ() && to.getY() == from.getY()) return;
        }

        DungeonInstance toDungeon = this.manager.getInstanceByLocation(to);
        if (toDungeon != gamer.getDungeon()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDungeonPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(Perms.CREATOR)) return;

        Location to = event.getTo();
        if (to == null) return;

        DungeonInstance toDungeon = this.manager.getInstanceByLocation(to);
        DungeonInstance fromDungeon = this.manager.getInstanceByLocation(event.getFrom());
        if (fromDungeon == toDungeon) return;

        DungeonGamer gamer = this.manager.getDungeonPlayer(player);
        if (gamer != null && !gamer.isTeleporting() && !gamer.getDungeon().isAboutToEnd() && toDungeon != gamer.getDungeon()) {
            event.setCancelled(true);
            return;
        }

        if (gamer == null && toDungeon != null && toDungeon.getState() == GameState.INGAME) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDungeonPlayerExhaust(EntityExhaustionEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        DungeonInstance dungeon = this.manager.getInstance(player);
        if (dungeon == null || dungeon.getConfig().gameSettings().isExhaustEnabled()) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDungeonPlayerHealthRegen(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        DungeonInstance dungeon = this.manager.getInstance(player);
        if (dungeon == null) return;

        if (!dungeon.getConfig().gameSettings().isHealthRegainEnabled()) {
            event.setCancelled(true);
            return;
        }

        boolean allowed = switch (event.getRegainReason()) {
            case MAGIC_REGEN, MAGIC -> dungeon.getConfig().gameSettings().isHealthRegainFromPotions();
            case SATIATED -> dungeon.getConfig().gameSettings().isHealthRegainFromSaturation();
            case EATING -> dungeon.getConfig().gameSettings().isHealthRegainFromFood();
            default -> dungeon.getConfig().gameSettings().isHealthRegainFromOther();
        };

        if (!allowed) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDungeonPlayerItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        DungeonGamer gamer = this.manager.getDungeonPlayer(player);
        if (gamer == null) return;

        DungeonInstance dungeon = gamer.getDungeon();
        GameSettings settings = dungeon.getConfig().gameSettings();

        if (!settings.isItemDropAllowed() || (settings.isKitsEnabled() && gamer.isInLobby())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDungeonPlayerItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();

        DungeonInstance dungeon = this.manager.getInstance(player);
        if (dungeon == null) return;

        if (!dungeon.getConfig().gameSettings().isItemDurabilityEnabled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDungeonPlayerItemUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;

        if (event.useItemInHand() == Event.Result.DENY) return;
        if (player.getGameMode() != GameMode.ADVENTURE && event.useInteractedBlock() == Event.Result.DENY) return;

        Block block = event.getClickedBlock();
        BlockFace face = event.getBlockFace();
        Action action = event.getAction();
        EquipmentSlot slot = event.getHand();

        if (!this.manager.canUseItem(player, itemStack, block, face, action, slot)) {
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);
        }
    }





    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDungeonMobSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getType() == EntityType.ARMOR_STAND) return;

        Location location = entity.getLocation();
        DungeonInstance dungeon = this.manager.getInstanceByLocation(location);
        if (dungeon == null) return;

        if (dungeon.getState() != GameState.INGAME || dungeon.isAboutToEnd()) {
            event.setCancelled(true);
            return;
        }

        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
        if (reason != CreatureSpawnEvent.SpawnReason.CUSTOM && !dungeon.getConfig().gameSettings().isAllowedExternalSpawn(reason)) {
            event.setCancelled(true);
            return;
        }

        // One tick delay, because custom mob's plugins don't have a way to distinguish an entity until it is spawned.
        this.plugin.runTask(task -> dungeon.handleMobSpawn(entity));
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDungeonMobTeleport(EntityTeleportEvent event) {
        Location to = event.getTo();
        if (to == null) return;

        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity mob)) return;

        DungeonInstance toDungeon = this.manager.getInstanceByLocation(to);
        DungeonInstance fromDungeon = this.manager.getInstanceByLocation(mob.getLocation());
        if (fromDungeon == toDungeon) return;

        if (toDungeon == null || !MobUitls.isPet(mob) || !toDungeon.getConfig().gameSettings().isPetsAllowed()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDungeonMobDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        DungeonMob mob = this.manager.getDungeonMob(entity);
        if (mob == null) return;

        mob.getDungeon().handleMobDeath(mob, event);
    }

//    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//    public void onDungeonMobHealthBarDamage(EntityDamageEvent event) {
//        if (!(event.getEntity() instanceof LivingEntity entity)) return;
//
//        this.plugin.runTask(task -> {
//            this.plugin.getMobManager().updateMobBar(entity);
//        });
//    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDungeonMobCombust(EntityCombustEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        DungeonMob mob = this.manager.getDungeonMob(entity);
        if (mob == null) return;

        if (!(event instanceof EntityCombustByEntityEvent eventBy)) {
            event.setCancelled(true);
            return;
        }

        if (eventBy.getCombuster() instanceof LivingEntity combuster) {
            if (!this.manager.canDamage(combuster, entity)) {
                event.setCancelled(true);
            }
        }
    }
}
