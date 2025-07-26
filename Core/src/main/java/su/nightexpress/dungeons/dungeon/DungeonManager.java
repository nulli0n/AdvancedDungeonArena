package su.nightexpress.dungeons.dungeon;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.api.dungeon.DungeonPos;
import su.nightexpress.dungeons.api.type.GameState;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.dungeon.config.DungeonConfig;
import su.nightexpress.dungeons.dungeon.event.normal.DungeonJoinEvent;
import su.nightexpress.dungeons.dungeon.event.normal.DungeonJoinedEvent;
import su.nightexpress.dungeons.dungeon.event.normal.DungeonLeftEvent;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.listener.DungeonGameListener;
import su.nightexpress.dungeons.dungeon.listener.DungeonGenericListener;
import su.nightexpress.dungeons.dungeon.listener.DungeonPotionListener;
import su.nightexpress.dungeons.dungeon.listener.DungeonProtectionListener;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.config.Perms;
import su.nightexpress.dungeons.user.DungeonUser;
import su.nightexpress.dungeons.dungeon.menu.DungeonBrowseMenu;
import su.nightexpress.dungeons.dungeon.mob.DungeonMob;
import su.nightexpress.dungeons.dungeon.player.DungeonGamer;
import su.nightexpress.dungeons.kit.KitUtils;
import su.nightexpress.dungeons.kit.impl.Kit;
import su.nightexpress.dungeons.util.MobUitls;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.ui.UIUtils;
import su.nightexpress.nightcore.ui.menu.confirmation.Confirmation;
import su.nightexpress.nightcore.util.BlockUtil;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.LocationUtil;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.geodata.Cuboid;
import su.nightexpress.nightcore.util.geodata.pos.ChunkPos;

import java.io.File;
import java.util.*;

public class DungeonManager extends AbstractManager<DungeonPlugin> {

    private final Map<String, DungeonConfig>     dungeonByIdMap;
    private final Map<DungeonPos, DungeonConfig> dungeonByPosMap;
    private final Map<String, DungeonInstance>   instanceByIdMap;
    private final Map<UUID, DungeonGamer>        playerByIdMap;

    private DungeonBrowseMenu browseMenu;

    public DungeonManager(@NotNull DungeonPlugin plugin) {
        super(plugin);
        this.dungeonByIdMap = new HashMap<>();
        this.dungeonByPosMap = new HashMap<>();
        this.instanceByIdMap = new HashMap<>();
        this.playerByIdMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.loadDungeons();
        this.loadUI();

        this.addTask(this::tickInstances, 1);

        this.addListener(new DungeonGenericListener(this.plugin, this));
        this.addListener(new DungeonGameListener(this.plugin, this));
        this.addListener(new DungeonProtectionListener(this.plugin, this));
        this.addListener(new DungeonPotionListener(this.plugin, this));
    }

    @Override
    protected void onShutdown() {
        if (this.browseMenu != null) this.browseMenu.clear();

        this.getInstances().forEach(DungeonInstance::stop);

        this.playerByIdMap.clear();
        this.instanceByIdMap.clear();
        this.dungeonByIdMap.clear();
        this.dungeonByPosMap.clear();
    }

    private void loadDungeons() {
        for (File dir : FileUtil.getFolders(plugin.getDataFolder() + Config.DIR_DUNGEONS)) {
            File file = new File(dir, DungeonConfig.FILE_NAME);
            DungeonConfig dungeonConfig = new DungeonConfig(this.plugin, file, dir.getName());
            this.loadDungeon(dungeonConfig);
        }
        this.plugin.info("Loaded " + this.instanceByIdMap.size() + " dungeons.");
    }

    public void loadDungeon(@NotNull DungeonConfig dungeonConfig) {
        if (!dungeonConfig.load()) {
            this.plugin.error("Dungeon not loaded: '" + dungeonConfig.getFile().getPath() + "'.");
            return;
        }

        DungeonInstance instance = new DungeonInstance(this.plugin, dungeonConfig);
        instance.activate();
        dungeonConfig.setInstance(instance);
        dungeonConfig.validate();

        this.instanceByIdMap.put(dungeonConfig.getId(), instance);
        this.dungeonByIdMap.put(dungeonConfig.getId(), dungeonConfig);
        this.updateDungeonPositions(dungeonConfig);
    }

    private void loadUI() {
        this.browseMenu = new DungeonBrowseMenu(this.plugin);
    }

    public void updateDungeonPositions(@NotNull DungeonConfig dungeonConfig) {
        dungeonConfig.getDungeonPositions().forEach(dungeonPos -> this.dungeonByPosMap.put(dungeonPos, dungeonConfig));
    }

    public void removeDungeonPositions(@NotNull DungeonConfig dungeonConfig) {
        dungeonConfig.getDungeonPositions().forEach(this.dungeonByPosMap::remove);
    }

    public void tickInstances() {
        this.getInstances().forEach(DungeonInstance::tick);
    }

    public void browseDungeons(@NotNull Player player) {
        this.browseMenu.open(player);
    }

    public void prepareForInstance(@NotNull Player player, @NotNull DungeonInstance dungeon) {
        if (!dungeon.isActive()) {
            dungeon.sendMessage(player, Lang.DUNGEON_ENTER_ERROR_INACTIVE, replacer -> replacer.replace(dungeon.replacePlaceholders()));
            return;
        }

        if (dungeon.isKitsMode()) {
            boolean hasKits = plugin.getKitManager().hasAnyAccess(player);

            if (KitUtils.isRentMode() || !hasKits) {
                this.plugin.getKitManager().openShop(player, dungeon);
            }
            else {
                this.plugin.getKitManager().openSelector(player, dungeon);
            }
        }
        else {
            UIUtils.openConfirmation(player, Confirmation.builder()
                .setIcon(dungeon.getConfig().getIcon()
                    .localized(Lang.UI_CONFIRMATION_DUNGEON_ENTER_NO_KITS)
                    .hideAllComponents()
                    .replacement(replacer -> replacer.replace(dungeon.replacePlaceholders())))
                .onAccept((viewer, event) -> this.enterInstance(player, dungeon,null))
                .onReturn((viewer, event) -> plugin.runTask(task -> player.closeInventory()))
                .build());
        }
    }

    public boolean enterInstance(@NotNull Player player, @NotNull DungeonInstance dungeon, @Nullable Kit kit) {
        return this.enterInstance(player, dungeon, kit, false);
    }

    public boolean enterInstance(@NotNull Player player, @NotNull DungeonInstance dungeon, @Nullable Kit kit, boolean force) {
        if (this.isPlaying(player)) {
            Lang.DUNGEON_ERROR_MUST_BE_OUT.getMessage().send(player);
            return false;
        }

        if (!dungeon.canJoin(player, force, true)) {
            return false;
        }

        if (dungeon.isKitsMode()) {
            if (kit == null) {
                Lang.DUNGEON_ENTER_ERROR_NO_KIT.getMessage().send(player, replacer -> replacer.replace(dungeon.replacePlaceholders()));
                return false;
            }

            if (!force) {
                // Double check kit restrictions after selection and confirmation since things can change in meanwhile.
                if (!kit.hasPermission(player)) {
                    Lang.DUNGEON_ENTER_ERROR_NO_KIT_PERMISSION.getMessage().send(player, replacer -> replacer.replace(kit.replacePlaceholders()).replace(dungeon.replacePlaceholders()));
                    return false;
                }

                if (!dungeon.isKitAllowed(kit)) {
                    Lang.DUNGEON_ENTER_ERROR_KIT_NOT_ALLOWED.getMessage().send(player, replacer -> replacer.replace(kit.replacePlaceholders()).replace(dungeon.replacePlaceholders()));
                    return false;
                }

                if (dungeon.isKitLimitReached(kit)) {
                    Lang.DUNGEON_ENTER_ERROR_NO_KIT_SLOTS.getMessage().send(player, replacer -> replacer.replace(kit.replacePlaceholders()).replace(dungeon.replacePlaceholders()));
                    return false;
                }

                // Check if player can pay for kit rent.
                if (KitUtils.isRentMode() && !kit.canAfford(player)) {
                    Lang.KIT_BUY_ERROR_INSUFFICIENT_FUNDS.getMessage().send(player, replacer -> replacer.replace(kit.replacePlaceholders()));
                    return false;
                }
            }
        }


        // Call join event before player joins.
        DungeonJoinEvent joinEvent = new DungeonJoinEvent(dungeon, player, kit);
        plugin.getPluginManager().callEvent(joinEvent);
        if (joinEvent.isCancelled()) return false;

        // Create dungeon player and enter the dungeon.
        DungeonGamer gamer = new DungeonGamer(player, dungeon);

        if (dungeon.isKitsMode() && kit != null) {
            gamer.setKit(kit);
        }

        this.playerByIdMap.put(player.getUniqueId(), gamer);
        dungeon.handlePlayerJoin(gamer, force);

        // Call joined event after everything.
        DungeonJoinedEvent joinedEvent = new DungeonJoinedEvent(dungeon, gamer);
        this.plugin.getPluginManager().callEvent(joinedEvent);

        return true;
    }

    public boolean leaveInstance(@NotNull Player player) {
        DungeonGamer gamer = this.getDungeonPlayer(player);
        if (gamer == null) {
            Lang.DUNGEON_ERROR_MUST_BE_IN.getMessage().send(player);
            return false;
        }

        return this.leaveInstance(gamer);
    }

    public boolean leaveInstance(@NotNull DungeonGamer gamer) {
        Player player = gamer.getPlayer();
        DungeonInstance dungeon = gamer.getDungeon();

        dungeon.handlePlayerLeave(gamer);

        this.playerByIdMap.remove(player.getUniqueId());
        Lang.DUNGEON_LEAVE_INFO.getMessage().send(player, replacer -> replacer.replace(dungeon.replacePlaceholders()));

        DungeonLeftEvent event = new DungeonLeftEvent(dungeon, gamer);
        this.plugin.getPluginManager().callEvent(event);

        return true;
    }

    public void setJoinCooldown(@NotNull Player player, @NotNull DungeonInstance dungeon) {
        int cooldown = dungeon.getConfig().features().getEntranceCooldown().getSmallest(player).intValue();
        if (cooldown == 0) return;

        DungeonUser user = this.plugin.getUserManager().getOrFetch(player);
        user.setArenaCooldown(dungeon, TimeUtil.createFutureTimestamp(cooldown));
        this.plugin.getUserManager().save(user);
    }

    public boolean isDungeonLocation(@NotNull Block block) {
        return this.isDungeonLocation(block.getLocation());
    }

    public boolean isDungeonLocation(@NotNull Location location) {
        return this.getDungeonByLocation(location) != null;
    }

    public boolean containsDungeons(@NotNull World world, @NotNull Cuboid cuboid) {
        return this.containsDungeons(world, cuboid, null);
    }

    public boolean containsDungeons(@NotNull World world, @NotNull Cuboid cuboid, @Nullable DungeonConfig source) {
        return this.getDungeons().stream().anyMatch(dungeon -> dungeon != source && dungeon.isWorld(world) && cuboid.isIntersectingWith(dungeon.getCuboid()));
    }

    @NotNull
    public Set<String> getDungeonIds() {
        return new HashSet<>(this.dungeonByIdMap.keySet());
    }

    @NotNull
    public Set<DungeonConfig> getDungeons() {
        return new HashSet<>(this.dungeonByIdMap.values());
    }

    @Nullable
    public DungeonConfig getDungeonById(@NotNull String id) {
        return this.dungeonByIdMap.get(id.toLowerCase());
    }

    @Nullable
    public DungeonConfig getDungeonByLocation(@NotNull Block block) {
        return this.getDungeonByLocation(block.getLocation());
    }

    @Nullable
    public DungeonConfig getDungeonByLocation(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return null;

        ChunkPos pos = ChunkPos.from(location);
        DungeonPos dungeonPos = new DungeonPos(world.getName(), pos);

        return this.dungeonByPosMap.get(dungeonPos);
    }

    @NotNull
    public Set<DungeonInstance> getInstances() {
        return new HashSet<>(this.instanceByIdMap.values());
    }

    @Nullable
    public DungeonInstance getInstance(@NotNull Player player) {
        DungeonGamer dungeonPlayer = this.getDungeonPlayer(player);
        if (dungeonPlayer == null) return null;

        return dungeonPlayer.getDungeon();
    }

    @Nullable
    public DungeonInstance getInstanceById(@NotNull String id) {
        return this.instanceByIdMap.get(id.toLowerCase());
    }

    @Nullable
    public DungeonInstance getInstanceByMob(@NotNull LivingEntity entity) {
        DungeonMob mob = this.getDungeonMob(entity);
        return mob == null ? null : mob.getDungeon();
    }

    @Nullable
    public DungeonInstance getInstanceByLocation(@NotNull Block block) {
        return this.getInstanceByLocation(block.getLocation());
    }

    @Nullable
    public DungeonInstance getInstanceByLocation(@NotNull Location location) {
        DungeonConfig dungeonConfig = this.getDungeonByLocation(location);
        return dungeonConfig == null ? null : dungeonConfig.getInstance();
    }

    @Nullable
    public DungeonMob getDungeonMob(@NotNull LivingEntity entity) {
        String id = MobUitls.getDungeonId(entity);
        if (id == null) return null;

        DungeonInstance instance = this.getInstanceById(id);
        if (instance == null) return null;

        return instance.getMob(entity);
    }

    @NotNull
    public Set<DungeonGamer> getDungeonPlayers() {
        return new HashSet<>(this.playerByIdMap.values());
    }

    @Nullable
    public DungeonGamer getDungeonPlayer(@NotNull Player player) {
        return this.getDungeonPlayer(player.getUniqueId());
    }

    @Nullable
    public DungeonGamer getDungeonPlayer(@NotNull UUID playerId) {
        return this.playerByIdMap.get(playerId);
    }

    public boolean isPlaying(@NotNull Player player) {
        return this.isPlaying(player.getUniqueId());
    }

    public boolean isPlaying(@NotNull UUID playerId) {
        return this.playerByIdMap.containsKey(playerId);
    }

    @Nullable
    public MobFaction getFaction(@NotNull LivingEntity entity) {
        if (entity instanceof Player player) {
            if (!this.isPlaying(player)) return null;
            return MobFaction.ALLY;
        }

        DungeonMob mob = this.getDungeonMob(entity);
        return mob == null ? null : mob.getFaction();
    }

    public boolean canDamage(@NotNull LivingEntity damager, @NotNull LivingEntity victim) {
        if (damager instanceof Player player) {
            return this.canPlayerDamageMob(player, victim);
        }
        if (victim instanceof Player player) {
            return this.canMobDamagePlayer(damager, player);
        }

        return this.canMobDamageMob(damager, victim);
    }

    private boolean canPlayerDamageMob(@NotNull Player damagerPlayer, @NotNull LivingEntity victim) {
        DungeonGamer damagerGamer = this.getDungeonPlayer(damagerPlayer);

        if (victim instanceof Player victimPlayer) {
            // Players can't damage each other if any of them is in dungeon.
            DungeonGamer victimGamer = this.getDungeonPlayer(victimPlayer);
            return damagerGamer == null && victimGamer == null;
        }

        DungeonMob victimMob = this.getDungeonMob(victim);

        // Dungeon player can damage only dungeon non-ally mobs.
        if (damagerGamer != null) {
            return victimMob != null && victimMob.isFaction(MobFaction.ENEMY);
        }

        // Non-dungeon player can't damage dungeon mobs.
        return victimMob == null;
    }

    private boolean canMobDamagePlayer(@NotNull LivingEntity damager, @NotNull Player victim) {
        DungeonGamer victimGamer = this.getDungeonPlayer(victim);
        DungeonMob mob = this.getDungeonMob(damager);

        // Dungeon mobs can damage only dungeon players.
        if (mob != null) {
            return victimGamer != null && mob.isFaction(MobFaction.ENEMY);
        }

        // Non-dungeon mobs can't damage dungeon players.
        return victimGamer == null;
    }

    private boolean canMobDamageMob(@NotNull LivingEntity damager, @NotNull LivingEntity victim) {
        DungeonMob damagerMob = this.getDungeonMob(damager);
        DungeonMob victimMob = this.getDungeonMob(victim);

        // Dungeon mobs can damage only dungeon mobs of opposite faction.
        if (damagerMob != null) {
            return victimMob != null && !victimMob.isFaction(damagerMob.getFaction());
        }

        // Non-dungeon mobs can damage only non-dungeon mobs.
        return victimMob == null;
    }

    public boolean canPlace(@NotNull Player player, @NotNull Block block, @NotNull ItemStack itemStack) {
//        DungeonGamer gamer = this.getDungeonPlayer(player);
//        if (gamer != null && itemStack.getType() == Material.TNT && Config.ITEMS_TNT_ALLOW_PLACEMENT.get()) {
//            World world = player.getWorld();
//            Location location = LocationUtil.setCenter2D(block.getLocation());
//
//            itemStack.setAmount(itemStack.getAmount() - 1);
//
//            TNTPrimed tnt = world.spawn(location, TNTPrimed.class);
//            tnt.setSource(player);
//            tnt.setFuseTicks(Config.ITEMS_TNT_FUSE_TICKS.get());
//            return false;
//        }

        return this.canBuild(player, block);
    }

    public boolean canBuild(@NotNull Player player, @NotNull Block block) {
        if (this.isPlaying(player)) return false;
        if (player.hasPermission(Perms.CREATOR)) return true;

        return !this.isDungeonLocation(block);
    }

    public boolean canBreakDecoration(@Nullable Entity damager, @NotNull Entity entity) {
        if (damager instanceof Player player && this.isPlaying(player)) {
            return false;
        }
        if (damager != null && damager.hasPermission(Perms.CREATOR)) {
            return true;
        }

        return !this.isDungeonLocation(entity.getLocation());
    }

    public boolean canInteract(@NotNull Player player, @NotNull Entity entity) {
        DungeonGamer gamer = this.getDungeonPlayer(player);
        if (gamer == null) return true;

        if (entity instanceof Player || entity instanceof Vehicle) return true;

        if (entity instanceof LivingEntity mob) {
            DungeonMob dungeonMob = this.getDungeonMob(mob);
            return dungeonMob == null || dungeonMob.getFaction() == MobFaction.ALLY;
        }

        return false;
    }

    public boolean canUseItem(@NotNull Player player,
                              @NotNull ItemStack itemStack,
                              @Nullable Block block,
                              @NotNull BlockFace face,
                              @NotNull Action action,
                              @Nullable EquipmentSlot slot) {
        DungeonGamer gamer = this.getDungeonPlayer(player);
        if (gamer == null) return true;
        if (gamer.isDead()) return false;
        if (gamer.getState() != GameState.INGAME) return false;
        if (itemStack.getType().isAir()) return true;

        if (block != null) {
            Material blockType = block.getType();
            if (blockType == Material.CAMPFIRE || blockType == Material.DECORATED_POT) {
                return false;
            }
            if (BlockUtil.isFunctional(blockType)) {
                return true;
            }
        }

        EntityType entityType = plugin.getInternals().getSpawnEggType(itemStack);
        if (entityType != null && block != null && action == Action.RIGHT_CLICK_BLOCK) {
            DungeonInstance dungeon = gamer.getDungeon();
            Location location = LocationUtil.setCenter2D(block.getRelative(face).getLocation());
            int level = 1;

            if (dungeon.spawnAllyMob(entityType, location, level)) {
                itemStack.setAmount(itemStack.getAmount() - 1);
            }

            return false;
        }

        if (itemStack.getType() == Material.FIRE_CHARGE && Config.ITEM_FIRE_CHARGE_HAND_LAUNCH.get()) {
            itemStack.setAmount(itemStack.getAmount() - 1);

            Location location = player.getEyeLocation().add(player.getLocation().getDirection());
            SmallFireball fireball = player.getWorld().spawn(location, SmallFireball.class);
            fireball.setShooter(player);
            fireball.setIsIncendiary(true);

            if (slot == EquipmentSlot.OFF_HAND) {
                player.swingOffHand();
            }
            else player.swingMainHand();

            return false;
        }

        if (itemStack.getType() == Material.TNT && Config.ITEMS_TNT_ALLOW_PLACEMENT.get()) {
            itemStack.setAmount(itemStack.getAmount() - 1);

            boolean hasBlock = block != null && player.getGameMode() == GameMode.SURVIVAL;
            Location location;
            if (hasBlock) {
                location = LocationUtil.setCenter2D(block.getLocation());
            }
            else {
                location = player.getEyeLocation().add(player.getLocation().getDirection());
            }

            TNTPrimed tnt = player.getWorld().spawn(location, TNTPrimed.class);
            tnt.setSource(player);
            tnt.setFuseTicks(Config.ITEMS_TNT_FUSE_TICKS.get());
            return false;
        }

        return true;
    }
}
