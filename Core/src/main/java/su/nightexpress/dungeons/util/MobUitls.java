package su.nightexpress.dungeons.util;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.api.dungeon.Dungeon;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.config.Keys;
import su.nightexpress.dungeons.api.mob.MobIdentifier;
import su.nightexpress.dungeons.registry.mob.MobProviderId;
import su.nightexpress.dungeons.registry.pet.PetRegistry;
import su.nightexpress.dungeons.mob.impl.MobTemplate;
import su.nightexpress.dungeons.dungeon.feature.KillStreak;
import su.nightexpress.nightcore.language.tag.MessageTags;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.PDCUtil;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.*;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.dungeons.Placeholders.*;

public class MobUitls {

    public static boolean isPet(@NotNull LivingEntity entity) {
        return PetRegistry.getProviders().stream().anyMatch(provider -> provider.isPet(entity));
    }

    @NotNull
    public static Map<EntityType, MobIdentifier> getDefaultEggAllies() {
        Map<EntityType, MobIdentifier> map = new HashMap<>();

        List<EntityType> types = new ArrayList<>();
        types.add(EntityType.WOLF);
        types.add(EntityType.CAT);
        types.add(EntityType.FOX);
        types.add(EntityType.OCELOT);
        types.add(EntityType.SPIDER);
        types.add(EntityType.CAVE_SPIDER);
        types.add(EntityType.ZOMBIE);
        types.add(EntityType.SILVERFISH);
        types.add(EntityType.PIGLIN);

        types.forEach(type -> {
            map.put(type, new MobIdentifier(MobProviderId.ADA, BukkitThing.toString(type)));
        });

        return map;
    }

    @NotNull
    public static Map<String, KillStreak> getDefaultKillStreaks() {
        Map<String, KillStreak> map = new HashMap<>();

        String title = MessageTags.OUTPUT.wrap(10, 50);
        String pentaText = title + LIGHT_CYAN.wrap(BOLD.wrap("Penta Kill!")) + TAG_LINE_BREAK + LIGHT_PURPLE.wrap("(+50$)");
        String text15 = title + YELLOW.wrap(BOLD.wrap("x" + GENERIC_AMOUNT + " Kill!")) + TAG_LINE_BREAK + LIGHT_YELLOW.wrap("(Heal)");

        map.put("5", new KillStreak("5", 5, false, pentaText, Lists.newList("eco give " + PLAYER_NAME + " 50")));
        map.put("15", new KillStreak("15", 15, false, text15, Lists.newList("heal " + PLAYER_NAME)));

        return map;
    }

    @Nullable
    public static MobIdentifier getEggAllyIdentifier(@NotNull EntityType entityType) {
        return Config.MOBS_EGG_ALLIES.get().get(entityType);
    }

    public static double getRandomSpawnOffset() {
        double origin = Config.MOBS_SPAWN_OFFSET.get();
        if (origin == 0D) return origin;

        double random = Rnd.getDouble(origin);
        if (Rnd.nextBoolean()) random = -random;

        return random;
    }

    public static void setTemplate(@NotNull LivingEntity entity, @NotNull MobTemplate mobTemplate) {
        PDCUtil.set(entity, Keys.mobTemplateId, mobTemplate.getId());
    }

    @Nullable
    public static String getTemplateId(@NotNull LivingEntity entity) {
        return PDCUtil.getString(entity, Keys.mobTemplateId).orElse(null);
    }

    public static boolean isTemplateMob(@NotNull LivingEntity entity) {
        return getTemplateId(entity) != null;
    }

    public static boolean isExternalAlly(@NotNull MobIdentifier identifier) {
        return Config.MOBS_ALLIES_EXTERNAL.get().contains(identifier);
    }

    public static void setDungeonId(@NotNull LivingEntity entity, @NotNull Dungeon dungeon) {
        PDCUtil.set(entity, Keys.mobDungeonId, dungeon.getId());
    }

    @Nullable
    public static String getDungeonId(@NotNull LivingEntity entity) {
        return PDCUtil.getString(entity, Keys.mobDungeonId).orElse(null);
    }
}
