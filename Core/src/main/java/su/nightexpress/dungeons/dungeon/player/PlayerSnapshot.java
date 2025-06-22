package su.nightexpress.dungeons.dungeon.player;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.api.dungeon.DungeonPlayer;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.hook.HookId;
import su.nightexpress.dungeons.hook.impl.EssentialsHook;
import su.nightexpress.dungeons.hook.impl.SunLightHook;
import su.nightexpress.nightcore.util.EntityUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.geodata.pos.ExactPos;

import java.util.*;

public class PlayerSnapshot {

    private static final Map<UUID, PlayerSnapshot> SNAPSHOTS = new HashMap<>();

    private final String                   worldName;
    private final ExactPos                 blockPos;
    private final int                      foodLevel;
    private final float                    saturation;
    private final float                    exhaustion;
    private final double                   health;
    private final ItemStack[]              inventory;
    private final ItemStack[]              armor;
    private final Collection<PotionEffect> effects;
    private final GameMode                 gameMode;
    private final List<ItemStack>          confiscate;

    PlayerSnapshot(@NotNull Player player) {
        this.worldName = player.getWorld().getName();
        this.blockPos = ExactPos.from(player.getLocation());
        this.foodLevel = player.getFoodLevel();
        this.saturation = player.getSaturation();
        this.exhaustion = player.getExhaustion();
        this.health = player.getHealth();
        this.inventory = player.getInventory().getContents();
        this.armor = player.getInventory().getArmorContents();
        this.effects = player.getActivePotionEffects();
        this.gameMode = player.getGameMode();
        this.confiscate = new ArrayList<>();
    }

    @Nullable
    public static PlayerSnapshot get(@NotNull Player player) {
        return SNAPSHOTS.get(player.getUniqueId());
    }

    @NotNull
    public static PlayerSnapshot doSnapshot(@NotNull Player player) {
        PlayerSnapshot snapshot = new PlayerSnapshot(player);
        SNAPSHOTS.put(player.getUniqueId(), snapshot);
        return snapshot;
    }

    public static void clear(@NotNull Player player) {
        //player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGliding(false);
        player.setSneaking(false);
        player.setSprinting(false);
        player.setFoodLevel(20);
        player.setSaturation(20F);
        player.setExhaustion(0F);
        player.setHealth(EntityUtil.getAttribute(player, Attribute.MAX_HEALTH));
        player.setFireTicks(0);
        player.leaveVehicle();
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        if (Plugins.isLoaded(HookId.ESSENTIALS)) {
            EssentialsHook.disableGod(player);
        }
        if (Plugins.isLoaded(HookId.SUNLIGHT)) {
            SunLightHook.disableGod(player);
        }
    }

    public static void restore(@NotNull DungeonPlayer gamer) {
        Player player = gamer.getPlayer();
        PlayerSnapshot snapshot = SNAPSHOTS.remove(player.getUniqueId());
        if (snapshot == null) return;

        DungeonInstance arena = (DungeonInstance) gamer.getDungeon();

        World world = Bukkit.getWorld(snapshot.getWorldName());
        if (world == null) world = Bukkit.getWorlds().getFirst();

        gamer.teleport(snapshot.getBlockPos().toLocation(world));
        player.setFoodLevel(snapshot.getFoodLevel());
        player.setSaturation(snapshot.getSaturation());
        player.setExhaustion(snapshot.getExhaustion());
        player.setHealth(Math.min(EntityUtil.getAttribute(player, Attribute.MAX_HEALTH), snapshot.getHealth()));
        player.setGameMode(snapshot.getGameMode());

        if (player.getGameMode() == GameMode.CREATIVE) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        player.addPotionEffects(snapshot.getPotionEffects());

        // Return player inventory before the game
        if (arena.getConfig().gameSettings().isKitsEnabled() || Config.DUNGEON_ALWAYS_RESTORE_INVENTORY.get()) {
            player.getInventory().setContents(snapshot.getInventory());
            player.getInventory().setArmorContents(snapshot.getArmor());
        }
        else {
            snapshot.getConfiscate().forEach(item -> Players.addItem(player, item));
        }
    }

    @NotNull
    public String getWorldName() {
        return this.worldName;
    }

    @NotNull
    public ExactPos getBlockPos() {
        return this.blockPos;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getExhaustion() {
        return exhaustion;
    }

    public double getHealth() {
        return health;
    }

    @NotNull
    public ItemStack[] getInventory() {
        return this.inventory;
    }

    public ItemStack[] getArmor() {
        return this.armor;
    }

    @NotNull
    public Collection<PotionEffect> getPotionEffects() {
        return this.effects;
    }

    @NotNull
    public GameMode getGameMode() {
        return this.gameMode;
    }

    @NotNull
    public List<ItemStack> getConfiscate() {
        return confiscate;
    }
}
