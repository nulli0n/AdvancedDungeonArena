package su.nightexpress.dungeons.mob;

import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.mob.impl.AttributeScale;
import su.nightexpress.dungeons.mob.impl.MobTemplate;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.EntityUtil;

import java.io.File;

public class MobCreator {

    public static void create(@NotNull DungeonPlugin plugin) {
        File dir = new File(plugin.getDataFolder() + Config.DIR_MOBS);
        if (dir.exists()) return;

        dir.mkdirs();

        World world = Bukkit.getWorlds().getFirst();

        for (EntityType entityType : BukkitThing.allFromRegistry(Registry.ENTITY_TYPE)) {
            if (!plugin.getInternals().isSupportedMob(entityType)) continue;

            String type = BukkitThing.toString(entityType);

            File mobFile = new File(dir.getAbsolutePath(), type + ".yml");
            MobTemplate mobData = new MobTemplate(plugin, mobFile);

            Class<? extends Entity> clazz = entityType.getEntityClass();
            if (clazz == null) return;

            LivingEntity entity = (LivingEntity) world.spawn(world.getSpawnLocation(), clazz, mob -> {
                mob.setPersistent(false);
            });

            EntityEquipment equipment = entity.getEquipment();
            if (equipment != null) {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack itemStack = equipment.getItem(slot);
                    mobData.setEquipment(slot, itemStack);
                }
            }

            for (Attribute attribute : BukkitThing.allFromRegistry(Registry.ATTRIBUTE)) {
                double startValue;
                double perLevel;

                startValue = EntityUtil.getAttribute(entity, attribute);

                if (attribute == Attribute.MAX_HEALTH) {
                    perLevel = startValue * 0.05;
                }
                else if (attribute == Attribute.ATTACK_DAMAGE) {
                    perLevel = startValue * 0.05;
                }
//                else if (attribute == Attribute.ATTACK_SPEED) {
//                    perLevel = startValue * 0.05;
//                }
                else if (attribute == Attribute.ARMOR) {
                    perLevel = startValue * 0.1;
                }
                else if (attribute == Attribute.MOVEMENT_SPEED) {
                    perLevel = startValue * 0.01;
                }
                else if (attribute == Attribute.FLYING_SPEED) {
                    perLevel = startValue * 0.01;
                }
                else if (attribute == Attribute.SCALE) {
                    perLevel = startValue * 0.02;
                }
                else continue;

                double minValue = 0D;
                double maxValue = 1000D;

                AttributeScale scale = new AttributeScale(startValue, perLevel, minValue, maxValue);
                mobData.getAttributes().put(attribute, scale);
            }

            mobData.setEntityType(entityType);
            mobData.setName(LangAssets.get(entityType));
            mobData.setNameVisible(true);
            mobData.save();

            entity.remove();
        }
    }
}
