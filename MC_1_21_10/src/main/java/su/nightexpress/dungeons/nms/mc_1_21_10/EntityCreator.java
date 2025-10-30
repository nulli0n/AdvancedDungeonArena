package su.nightexpress.dungeons.nms.mc_1_21_10;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import org.bukkit.craftbukkit.v1_21_R6.entity.CraftEntityType;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.nms.mc_1_21_10.mob.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityCreator {

    private static final Set<EntityType> VANILLA_SUPPORTED = new HashSet<>();
    private static final Map<EntityType, Creator<?>> CUSTOMS = new HashMap<>();

    static {
        VANILLA_SUPPORTED.add(EntityType.BEE);
        VANILLA_SUPPORTED.add(EntityType.BLAZE);
        VANILLA_SUPPORTED.add(EntityType.BOGGED);
        VANILLA_SUPPORTED.add(EntityType.BREEZE);
        VANILLA_SUPPORTED.add(EntityType.CAT);
        VANILLA_SUPPORTED.add(EntityType.CAVE_SPIDER);
        VANILLA_SUPPORTED.add(EntityType.COW);
        VANILLA_SUPPORTED.add(EntityType.CHICKEN);
        VANILLA_SUPPORTED.add(EntityType.CREEPER);
        VANILLA_SUPPORTED.add(EntityType.DONKEY);
        VANILLA_SUPPORTED.add(EntityType.DROWNED);
        VANILLA_SUPPORTED.add(EntityType.ENDERMAN);
        VANILLA_SUPPORTED.add(EntityType.ENDERMITE);
        VANILLA_SUPPORTED.add(EntityType.EVOKER);
        VANILLA_SUPPORTED.add(EntityType.FOX);
        VANILLA_SUPPORTED.add(EntityType.GHAST);
        VANILLA_SUPPORTED.add(EntityType.GUARDIAN);
        VANILLA_SUPPORTED.add(EntityType.HORSE);
        VANILLA_SUPPORTED.add(EntityType.HUSK);
        VANILLA_SUPPORTED.add(EntityType.ILLUSIONER);
        VANILLA_SUPPORTED.add(EntityType.LLAMA);
        VANILLA_SUPPORTED.add(EntityType.MULE);
        VANILLA_SUPPORTED.add(EntityType.MOOSHROOM);
        VANILLA_SUPPORTED.add(EntityType.OCELOT);
        VANILLA_SUPPORTED.add(EntityType.PANDA);
        VANILLA_SUPPORTED.add(EntityType.PIG);
        VANILLA_SUPPORTED.add(EntityType.PILLAGER);
        VANILLA_SUPPORTED.add(EntityType.PHANTOM);
        VANILLA_SUPPORTED.add(EntityType.POLAR_BEAR);
        VANILLA_SUPPORTED.add(EntityType.RABBIT);
        VANILLA_SUPPORTED.add(EntityType.RAVAGER);
        VANILLA_SUPPORTED.add(EntityType.SHEEP);
        VANILLA_SUPPORTED.add(EntityType.SHULKER);
        VANILLA_SUPPORTED.add(EntityType.SILVERFISH);
        VANILLA_SUPPORTED.add(EntityType.STRAY);
        VANILLA_SUPPORTED.add(EntityType.SKELETON);
        VANILLA_SUPPORTED.add(EntityType.SKELETON_HORSE);
        VANILLA_SUPPORTED.add(EntityType.SPIDER);
        VANILLA_SUPPORTED.add(EntityType.SNOW_GOLEM);
        VANILLA_SUPPORTED.add(EntityType.TURTLE);
        VANILLA_SUPPORTED.add(EntityType.TRADER_LLAMA);
        VANILLA_SUPPORTED.add(EntityType.VEX);
        VANILLA_SUPPORTED.add(EntityType.VILLAGER);
        VANILLA_SUPPORTED.add(EntityType.VINDICATOR);
        VANILLA_SUPPORTED.add(EntityType.WANDERING_TRADER);
        VANILLA_SUPPORTED.add(EntityType.WITCH);
        VANILLA_SUPPORTED.add(EntityType.WITHER_SKELETON);
        VANILLA_SUPPORTED.add(EntityType.WITHER);
        VANILLA_SUPPORTED.add(EntityType.WOLF);
        VANILLA_SUPPORTED.add(EntityType.ZOMBIE);
        VANILLA_SUPPORTED.add(EntityType.ZOMBIE_HORSE);
        VANILLA_SUPPORTED.add(EntityType.ZOMBIE_VILLAGER);
        VANILLA_SUPPORTED.add(EntityType.ZOMBIFIED_PIGLIN);

        CUSTOMS.put(EntityType.CREAKING, CreakingMob::new);
        CUSTOMS.put(EntityType.PIGLIN, PiglinMob::new);
        CUSTOMS.put(EntityType.PIGLIN_BRUTE, PiglinBruteMob::new);
        CUSTOMS.put(EntityType.HOGLIN, HoglinMob::new);
        CUSTOMS.put(EntityType.ZOGLIN, ZoglinMob::new);
        CUSTOMS.put(EntityType.GOAT, GoatMob::new);
        CUSTOMS.put(EntityType.FROG, FrogMob::new);
        CUSTOMS.put(EntityType.ALLAY, AllayMob::new);
        CUSTOMS.put(EntityType.WARDEN, WardenMob::new);
        CUSTOMS.put(EntityType.SLIME, SlimeMob::new);
        CUSTOMS.put(EntityType.MAGMA_CUBE, MagmaCubeMob::new);
        CUSTOMS.put(EntityType.IRON_GOLEM, IronGolemMob::new);
    }

    public interface Creator<T extends Mob> {

        @NotNull T create(@NotNull ServerLevel level/*, @NotNull Dungeon dungeon, @NotNull MobFaction faction*/);
    }

    public static boolean isCustom(@NotNull EntityType type) {
        return CUSTOMS.containsKey(type);
    }

    public static boolean isVanilla(@NotNull EntityType type) {
        return VANILLA_SUPPORTED.contains(type);
    }

    public static boolean isSupported(@NotNull EntityType type) {
        return isCustom(type) || isVanilla(type);
    }

    @Nullable
    public static Mob createEntity(/*@NotNull Dungeon arena, @NotNull MobFaction faction, */@NotNull EntityType type, @NotNull ServerLevel level) {
        if (VANILLA_SUPPORTED.contains(type)) {
            var nmsType = CraftEntityType.bukkitToMinecraft(type);
            return (Mob) nmsType.create(level, null);
        }

        Creator<?> creator = CUSTOMS.get(type);
        if (creator == null) return null;

        return creator.create(level/*, arena, faction*/);
    }
}
