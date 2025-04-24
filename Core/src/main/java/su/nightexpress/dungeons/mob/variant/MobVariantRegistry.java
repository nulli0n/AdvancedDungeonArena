package su.nightexpress.dungeons.mob.variant;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.mob.variant.impl.*;

import java.util.*;

public class MobVariantRegistry {

    private static final Map<String, MobVariant<?>> VARIANT_MAP = new HashMap<>();

    public static void load() {
        register(new AgeMobVariant());
        register(new SizeMobVariant());
        register(new CreeperPowerMobVariant());
        register(new FoxTypeMobVariant());
        register(new LlamaColorMobVariant());
        register(new SheepColorMobVariant());
        register(new HorseStyleMobVariant());
        register(new HorseColorMobVariant());
        register(new SheepShearMobVariant());
    }

//    public static <T> void register(@NotNull String name, @NotNull MobVariant<T> handler) {
//        VARIANT_MAP.put(name.toLowerCase(), handler);
//    }

    public static void register(@NotNull MobVariant<?> variant) {
        VARIANT_MAP.put(variant.getName(), variant);
    }

    public static void clear() {
        VARIANT_MAP.clear();
    }

    @Nullable
    public static MobVariant<?> getVariant(@NotNull String name) {
        return VARIANT_MAP.get(name.toLowerCase());
    }

    @NotNull
    public static Set<MobVariant<?>> getVariants() {
        return new HashSet<>(VARIANT_MAP.values());
    }

    @NotNull
    public static List<String> getVariantNames() {
        return new ArrayList<>(VARIANT_MAP.keySet());
    }
}
