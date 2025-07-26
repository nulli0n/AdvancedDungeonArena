package su.nightexpress.dungeons.dungeon.module;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.dungeon.config.DungeonConfig;
import su.nightexpress.dungeons.dungeon.feature.board.BoardLayout;
import su.nightexpress.dungeons.kit.impl.Kit;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.CommandUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.StringUtil;

import java.util.*;

public class GameSettings implements Writeable {

    private int     timeleft;
    private int     lobbyTime;
    private int     minPlayers;
    private int     maxPlayers;
    private int     playerLives;
    private boolean keepInventoryEnabled;
    private boolean keepInventoryRequiresLives;
    private boolean leaveOnDeath;
    private boolean adventureMode;

    private boolean startAnnouncement;
    private boolean endAnnouncement;
    private boolean scoreboardEnabled;
    private String  scoreboardLayoutId;

    private boolean exhaustEnabled;
    private boolean healthRegainEnabled;
    private boolean healthRegainFromFood;
    private boolean healthRegainFromSaturation;
    private boolean healthRegainFromPotions;
    private boolean healthRegainFromOther;

    private boolean itemDropAllowed;
    private boolean itemPickupAllowed;
    private boolean itemDurabilityEnabled;

    private boolean mobsDropXP;
    private boolean mobsDropLoot;

    private Set<String>                         allowedCommands;
    //private Set<Material>                       bannedItems;
    private Set<CreatureSpawnEvent.SpawnReason> allowedExternalMobSpawns;

    private       boolean              kitsEnabled;
    private final Map<String, Integer> kitsLimits;

    private boolean petsAllowed;
    private boolean mcmmoAllowed;

    public GameSettings(@NotNull DungeonConfig dungeonConfig) {
        //this.bannedItems = Lists.newSet(Material.ENDER_PEARL);
        this.allowedExternalMobSpawns = Lists.newSet(CreatureSpawnEvent.SpawnReason.CUSTOM);
        this.allowedCommands = new HashSet<>();
        this.kitsLimits = new HashMap<>();
        this.setDefaults();
    }

    private void setDefaults() {
        this.setTimeleft(30);
        this.setLobbyTime(30);

        this.setMinPlayers(1);
        this.setMaxPlayers(5);
        this.setPlayerLives(1);
        this.setKeepInventoryEnabled(true);
        this.setKeepInventoryRequiresLives(false);
        this.setLeaveOnDeath(false);
        this.setAdventureMode(true);

        this.setStartAnnouncement(true);
        this.setEndAnnouncement(true);
        this.setScoreboardEnabled(true);
        this.setScoreboardLayoutId(Placeholders.DEFAULT);

        this.setExhaustEnabled(true);
        this.setHealthRegainEnabled(true);
        this.setHealthRegainFromFood(true);
        this.setHealthRegainFromSaturation(true);
        this.setHealthRegainFromPotions(true);
        this.setHealthRegainFromOther(true);

        this.setItemDurabilityEnabled(true);
        this.setItemDropAllowed(true);
        this.setItemPickupAllowed(true);

        this.setMobsDropXP(true);
        this.setMobsDropLoot(true);

        this.setKitsEnabled(false);
    }

    public void load(@NotNull FileConfig config, @NotNull String path) {
        if (config.contains(path + ".General.Keep_Inventory")) {
            boolean oldValue = config.getBoolean(path + ".General.Keep_Inventory");
            config.set(path + ".General.KeepInventory.Enabled", oldValue);
            config.remove(path + ".General.Keep_Inventory");
        }

        this.setTimeleft(config.getInt(path + ".General.Timeleft", this.timeleft));
        this.setLobbyTime(config.getInt(path + ".General.Lobby_Prepare_Time", this.lobbyTime));
        this.setLeaveOnDeath(config.getBoolean(path + ".General.Leave_On_Death", this.leaveOnDeath));
        this.setAdventureMode(config.getBoolean(path + ".General.Adventure_Mode", this.adventureMode));
        this.setMinPlayers(config.getInt(path + ".General.MinPlayers", this.minPlayers));
        this.setMaxPlayers(config.getInt(path + ".General.MaxPlayers", this.maxPlayers));
        this.setPlayerLives(config.getInt(path + ".General.PlayerLives", this.playerLives));
        this.setKeepInventoryEnabled(config.getBoolean(path + ".General.KeepInventory.Enabled", this.keepInventoryEnabled));
        this.setKeepInventoryRequiresLives(ConfigValue.create(path + ".General.KeepInventory.LivesRequired", false).read(config));
        this.setAllowedCommands(config.getStringSet(path + ".General.AllowedCommands"));
        //this.bannedItems = new HashSet<>(config.getStringSet(path + ".General.Banned_Items").stream().map(Material::getMaterial).filter(Objects::nonNull).toList());

        this.setStartAnnouncement(config.getBoolean(path + ".Announcements.OnStart", this.startAnnouncement));
        this.setEndAnnouncement(config.getBoolean(path + ".Announcements.OnEnd", this.endAnnouncement));

        this.setScoreboardEnabled(config.getBoolean(path + ".Scoreboard.Enabled", this.scoreboardEnabled));
        this.setScoreboardLayoutId(config.getString(path + ".Scoreboard.Id", this.scoreboardLayoutId));

        this.setExhaustEnabled(config.getBoolean(path + ".VanillaFeatures.Exhaust.Enabled", true));
        this.setHealthRegainEnabled(config.getBoolean(path + ".VanillaFeatures.HealthRegain.Enabled", true));
        this.setHealthRegainFromFood(!config.getBoolean(path + ".VanillaFeatures.HealthRegain.DisabledFrom.Food", false));
        this.setHealthRegainFromSaturation(!config.getBoolean(path + ".VanillaFeatures.HealthRegain.DisabledFrom.Saturation", false));
        this.setHealthRegainFromPotions(!config.getBoolean(path + ".VanillaFeatures.HealthRegain.DisabledFrom.Potions", false));
        this.setHealthRegainFromOther(!config.getBoolean(path + ".VanillaFeatures.HealthRegain.DisabledFrom.Other", false));
        this.setItemDurabilityEnabled(config.getBoolean(path + ".VanillaFeatures.Item_Durability.Enabled", true));

        this.setItemDropAllowed(config.getBoolean(path + ".PlayerActions.Allow_Item_Drop", true));
        this.setItemPickupAllowed(config.getBoolean(path + ".PlayerActions.Allow_Item_Pickup", true));

        this.setMobsDropXP(config.getBoolean(path + ".Mobs.DropXP"));
        this.setMobsDropLoot(config.getBoolean(path + ".Mobs.DropLoot"));
        this.allowedExternalMobSpawns = new HashSet<>(config.getStringSet(path + ".Mobs.AllowedExternalSpawns")
            .stream().map(raw -> StringUtil.getEnum(raw, CreatureSpawnEvent.SpawnReason.class).orElse(null))
            .filter(Objects::nonNull).toList());

        this.setKitsEnabled(config.getBoolean(path + ".Kits.Enabled", false));
        for (String kitId : config.getSection(path + ".Kits.Limits")) {
            this.kitsLimits.put(kitId.toLowerCase(), config.getInt(path + ".Kits.Limits." + kitId));
        }

        this.setPetsAllowed(config.getBoolean(path + ".Integrations.Pets_Enabled", false));
        this.setMcmmoAllowed(config.getBoolean(path + ".Integrations.Mcmmo_Enabled", true));
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".General.Timeleft", this.timeleft);
        config.set(path + ".General.Lobby_Prepare_Time", this.lobbyTime);
        config.set(path + ".General.Leave_On_Death", this.leaveOnDeath);
        config.set(path + ".General.Adventure_Mode", this.adventureMode);
        config.set(path + ".General.MinPlayers", this.minPlayers);
        config.set(path + ".General.MaxPlayers", this.maxPlayers);
        config.set(path + ".General.PlayerLives", this.playerLives);
        config.set(path + ".General.KeepInventory.Enabled", this.keepInventoryEnabled);
        config.set(path + ".General.KeepInventory.LivesRequired", this.keepInventoryRequiresLives);
        config.set(path + ".General.AllowedCommands", this.allowedCommands);
        //config.set(path + ".General.Banned_Items", this.bannedItems.stream().map(Material::name).toList());

        config.set(path + ".Announcements.OnStart", this.startAnnouncement);
        config.set(path + ".Announcements.OnEnd", this.endAnnouncement);
        config.set(path + ".Scoreboard.Enabled", this.scoreboardEnabled);
        config.set(path + ".Scoreboard.Id", this.scoreboardLayoutId);

        config.set(path + ".VanillaFeatures.Exhaust.Enabled", this.exhaustEnabled);
        config.set(path + ".VanillaFeatures.HealthRegain.Enabled", this.healthRegainEnabled);
        config.set(path + ".VanillaFeatures.HealthRegain.DisabledFrom.Food", !this.healthRegainFromFood);
        config.set(path + ".VanillaFeatures.HealthRegain.DisabledFrom.Saturation", !this.healthRegainFromSaturation);
        config.set(path + ".VanillaFeatures.HealthRegain.DisabledFrom.Potions", !this.healthRegainFromPotions);
        config.set(path + ".VanillaFeatures.HealthRegain.DisabledFrom.Other", !this.healthRegainFromOther);
        config.set(path + ".VanillaFeatures.Item_Durability.Enabled", this.itemDurabilityEnabled);

        config.set(path + ".PlayerActions.Allow_Item_Drop", this.itemDropAllowed);
        config.set(path + ".PlayerActions.Allow_Item_Pickup", this.itemPickupAllowed);

        config.set(path + ".Mobs.DropXP", this.mobsDropXP);
        config.set(path + ".Mobs.DropLoot", this.mobsDropLoot);
        config.set(path + ".Mobs.AllowedExternalSpawns", this.allowedExternalMobSpawns.stream().map(Enum::name).toList());

        config.set(path + ".Kits.Enabled", this.kitsEnabled);
        config.remove(path + ".Kits.Limits");
        this.kitsLimits.forEach((kitId, amount) -> config.set(path + ".Kits.Limits." + kitId, amount));

        config.set(path + ".Integrations.Pets_Enabled", this.petsAllowed);
        config.set(path + ".Integrations.Mcmmo_Enabled", this.mcmmoAllowed);
    }

    @Nullable
    public BoardLayout getBoardLayout() {
        return Config.getDungeonBoard(this.scoreboardLayoutId);
    }

    /*@Deprecated
    public boolean isBannedItem(@NotNull ItemStack item) {
        return this.bannedItems.contains(item.getType());
    }*/

    public boolean isAllowedCommand(@NotNull String command) {
        if (this.allowedCommands.isEmpty()) return false;
        if (this.allowedCommands.contains(Placeholders.WILDCARD)) return true;

        Set<String> aliases = CommandUtil.getAliases(command, true);
        return aliases.stream().anyMatch(alias -> this.allowedCommands.contains(alias));
    }

    public boolean isAllowedExternalSpawn(@NotNull CreatureSpawnEvent.SpawnReason reason) {
        return this.allowedExternalMobSpawns.contains(reason);
    }

    public boolean isKitAllowed(@NotNull Kit kit) {
        return this.isKitAllowed(kit.getId());
    }

    public boolean isKitAllowed(@NotNull String id) {
        return this.getKitLimit(id) != 0;
    }

    public boolean hasKitLimits() {
        return !this.kitsLimits.isEmpty();
    }

    public int getKitLimit(@NotNull Kit kit) {
        return this.getKitLimit(kit.getId());
    }

    public int getKitLimit(@NotNull String id) {
        // If nothing defined, then noting to limit.
//        if (this.kitsLimits.isEmpty()) return -1;
//
//        id = id.toLowerCase();

        return this.kitsLimits.getOrDefault(id.toLowerCase(), -1);

        // If kit present, get limit for it.
//        if (this.kitsLimits.containsKey(id)) {
//            return this.kitsLimits.getOrDefault(id, -1);
//        }
//
//        // Otherwise try to get global one.
//        return this.kitsLimits.getOrDefault(Placeholders.WILDCARD, 0);
    }

    // General settings.

    public int getTimeleft() {
        return this.timeleft;
    }

    public boolean hasTimeleft() {
        return this.timeleft > 0;
    }

    public void setTimeleft(int timeleft) {
        this.timeleft = timeleft;
    }

    public int getLobbyTime() {
        return this.lobbyTime;
    }

    public void setLobbyTime(int lobbyTime) {
        this.lobbyTime = Math.max(1, lobbyTime);
    }

    public boolean isAdventureMode() {
        return this.adventureMode;
    }

    public void setAdventureMode(boolean adventureMode) {
        this.adventureMode = adventureMode;
    }

    public int getMinPlayers() {
        return this.minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = Math.max(1, minPlayers);
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getPlayerLives() {
        return this.playerLives;
    }

    public void setPlayerLives(int playerLives) {
        this.playerLives = Math.max(1, playerLives);
    }

    public boolean isKeepInventoryEnabled() {
        return this.keepInventoryEnabled;
    }

    public void setKeepInventoryEnabled(boolean keepInventoryEnabled) {
        this.keepInventoryEnabled = keepInventoryEnabled;
    }

    public boolean isKeepInventoryRequiresLives() {
        return keepInventoryRequiresLives;
    }

    public void setKeepInventoryRequiresLives(boolean keepInventoryRequiresLives) {
        this.keepInventoryRequiresLives = keepInventoryRequiresLives;
    }

    @NotNull
    public Set<String> getAllowedCommands() {
        return this.allowedCommands;
    }

    public void setAllowedCommands(@NotNull Set<String> allowedCommands) {
        this.allowedCommands = allowedCommands;
    }

    public boolean isLeaveOnDeath() {
        return this.leaveOnDeath;
    }

    public void setLeaveOnDeath(boolean leaveOnDeath) {
        this.leaveOnDeath = leaveOnDeath;
    }


    // Other settings.

    public boolean isStartAnnouncement() {
        return this.startAnnouncement;
    }

    public void setStartAnnouncement(boolean startAnnouncement) {
        this.startAnnouncement = startAnnouncement;
    }

    public boolean isEndAnnouncement() {
        return this.endAnnouncement;
    }

    public void setEndAnnouncement(boolean endAnnouncement) {
        this.endAnnouncement = endAnnouncement;
    }

    public boolean isScoreboardEnabled() {
        return this.scoreboardEnabled;
    }

    public void setScoreboardEnabled(boolean scoreboardEnabled) {
        this.scoreboardEnabled = scoreboardEnabled;
    }

    @NotNull
    public String getScoreboardLayoutId() {
        return this.scoreboardLayoutId;
    }

    public void setScoreboardLayoutId(@NotNull String scoreboardLayoutId) {
        this.scoreboardLayoutId = scoreboardLayoutId;
    }


    // Vanilla Features settings.

    public boolean isExhaustEnabled() {
        return this.exhaustEnabled;
    }

    public void setExhaustEnabled(boolean isHungerEnabled) {
        this.exhaustEnabled = isHungerEnabled;
    }

    public boolean isHealthRegainEnabled() {
        return this.healthRegainEnabled;
    }

    public void setHealthRegainEnabled(boolean isRegenerationEnabled) {
        this.healthRegainEnabled = isRegenerationEnabled;
    }

    public boolean isHealthRegainFromFood() {
        return healthRegainFromFood;
    }

    public void setHealthRegainFromFood(boolean healthRegainFromFood) {
        this.healthRegainFromFood = healthRegainFromFood;
    }

    public boolean isHealthRegainFromSaturation() {
        return healthRegainFromSaturation;
    }

    public void setHealthRegainFromSaturation(boolean healthRegainFromSaturation) {
        this.healthRegainFromSaturation = healthRegainFromSaturation;
    }

    public boolean isHealthRegainFromPotions() {
        return healthRegainFromPotions;
    }

    public void setHealthRegainFromPotions(boolean healthRegainFromPotions) {
        this.healthRegainFromPotions = healthRegainFromPotions;
    }

    public boolean isHealthRegainFromOther() {
        return healthRegainFromOther;
    }

    public void setHealthRegainFromOther(boolean healthRegainFromOther) {
        this.healthRegainFromOther = healthRegainFromOther;
    }



    // Player Actions settings.

    public boolean isItemDropAllowed() {
        return this.itemDropAllowed;
    }

    public void setItemDropAllowed(boolean isItemDropEnabled) {
        this.itemDropAllowed = isItemDropEnabled;
    }

    public boolean isItemPickupAllowed() {
        return this.itemPickupAllowed;
    }

    public void setItemPickupAllowed(boolean isItemPickupEnabled) {
        this.itemPickupAllowed = isItemPickupEnabled;
    }

    public boolean isItemDurabilityEnabled() {
        return this.itemDurabilityEnabled;
    }

    public void setItemDurabilityEnabled(boolean isItemDurabilityEnabled) {
        this.itemDurabilityEnabled = isItemDurabilityEnabled;
    }




    // Mobs settings.

    public boolean isMobsDropXP() {
        return this.mobsDropXP;
    }

    public void setMobsDropXP(boolean mobsDropXP) {
        this.mobsDropXP = mobsDropXP;
    }

    public boolean isMobsDropLoot() {
        return this.mobsDropLoot;
    }

    public void setMobsDropLoot(boolean mobsDropLoot) {
        this.mobsDropLoot = mobsDropLoot;
    }

    @NotNull
    public Set<CreatureSpawnEvent.SpawnReason> getAllowedExternalMobSpawns() {
        return this.allowedExternalMobSpawns;
    }



    // Kits settings.

    public boolean isKitsEnabled() {
        return this.kitsEnabled;
    }

    public void setKitsEnabled(boolean kitsEnabled) {
        this.kitsEnabled = kitsEnabled;
    }

    @NotNull
    public Map<String, Integer> getKitsLimits() {
        return this.kitsLimits;
    }

    // Integrations settings.

    public boolean isPetsAllowed() {
        return this.petsAllowed;
    }

    public void setPetsAllowed(boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }

    public boolean isMcmmoAllowed() {
        return this.mcmmoAllowed;
    }

    public void setMcmmoAllowed(boolean mcmmoAllowed) {
        this.mcmmoAllowed = mcmmoAllowed;
    }
}
