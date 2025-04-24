package su.nightexpress.dungeons.mob.impl;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.mob.variant.MobVariant;
import su.nightexpress.dungeons.mob.variant.MobVariantRegistry;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.placeholder.Replacer;
import su.nightexpress.nightcore.util.text.NightMessage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MobTemplate extends AbstractFileData<DungeonPlugin> {

    private EntityType entityType;
    private String     name;
    private boolean    nameVisible;

    private final Map<String, String>            accessories;
    private final Map<EquipmentSlot, ItemStack>  equipment;
    private final Map<Attribute, AttributeScale> attributes;

    public MobTemplate(@NotNull DungeonPlugin plugin, @NotNull File file) {
        super(plugin, file);
        this.accessories = new HashMap<>();
        this.attributes = new HashMap<>();
        this.equipment = new HashMap<>();
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        this.setEntityType(ConfigValue.create("EntityType", EntityType.class, EntityType.UNKNOWN).read(config));
        if (!this.plugin.getInternals().isSupportedMob(this.entityType)) {
            this.plugin.error("Invalid or unsupported entity type '" + BukkitThing.toString(this.entityType) + "'.");
            return false;
        }

        this.setName(ConfigValue.create("DisplayName.Value", StringUtil.capitalizeUnderscored(this.getId())).read(config));
        this.setNameVisible(ConfigValue.create("DisplayName.AlwaysVisible", true).read(config));



        config.getSection("Style").forEach(variantId -> {
            String value = config.getString("Style." + variantId);
            if (value == null) return;

            this.addAccessory(variantId.toLowerCase(), value.toLowerCase());
        });

        config.getSection("Equipment").forEach(slotName -> {
            EquipmentSlot slot = StringUtil.getEnum(slotName, EquipmentSlot.class).orElse(null);
            if (slot == null) return;

            ItemTag itemTag = ItemTag.read(config, "Equipment." + slotName);
            ItemStack itemStack = ItemNbt.fromTag(itemTag);
            if (itemStack == null) itemStack = new ItemStack(Material.AIR);

            this.setEquipment(slot, itemStack);
        });

        config.getSection("Attributes").forEach(attId -> {
            Attribute attribute = BukkitThing.getAttribute(attId);
            if (attribute == null) return;

            AttributeScale scale = AttributeScale.read(config, "Attributes." + attId);
            this.attributes.put(attribute, scale);
        });

        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("EntityType", BukkitThing.toString(this.entityType));
        config.set("DisplayName.Value", this.name);
        config.set("DisplayName.AlwaysVisible", this.nameVisible);

        config.remove("Style");
        this.accessories.forEach((variantId, value) -> {
            config.set("Style." + variantId, value);
        });

        config.remove("Equipment");
        this.equipment.forEach((slot, itemStack) -> {
            config.set("Equipment." + slot.name(), ItemNbt.getTag(itemStack));
        });

        config.remove("Attributes");
        this.attributes.forEach((attribute, scale) -> {
            config.set("Attributes." + BukkitThing.toString(attribute), scale);
        });
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public boolean isNameVisible() {
        return this.nameVisible;
    }

    public void setNameVisible(boolean visible) {
        this.nameVisible = visible;
    }

    @NotNull
    public EntityType getEntityType() {
        return this.entityType;
    }

    public void setEntityType(@NotNull EntityType type) {
        this.entityType = type;
    }

    @NotNull
    public Map<String, String> getAccessories() {
        return this.accessories;
    }

    public <T> void addAccessory(@NotNull MobVariant<T> variant, @NotNull T value) {
        this.addAccessory(variant, variant.getRaw(value));
    }

    public void addAccessory(@NotNull MobVariant<?> variant, @NotNull String value) {
        this.addAccessory(variant.getName(), value);
    }

    public void addAccessory(@NotNull String variant, @NotNull String value) {
        this.accessories.put(variant, value.toLowerCase());
    }

    public void dressUp(@NotNull LivingEntity entity) {
        this.accessories.forEach(((name, raw) -> {
            MobVariant<?> variant = MobVariantRegistry.getVariant(name);
            if (variant == null) return;

            variant.apply(entity, raw);
        }));
    }

    @NotNull
    public Map<EquipmentSlot, ItemStack> getEquipment() {
        return this.equipment;
    }

    @NotNull
    public ItemStack getEquipment(@NotNull EquipmentSlot slot) {
        return this.equipment.getOrDefault(slot, new ItemStack(Material.AIR));
    }

    public void setEquipment(@NotNull EquipmentSlot slot, @Nullable ItemStack item) {
        if (item == null) item = new ItemStack(Material.AIR);
        this.equipment.put(slot, item);
    }

    @NotNull
    public Map<Attribute, AttributeScale> getAttributes() {
        return this.attributes;
    }

    public void applySettings(@NotNull LivingEntity entity, int level) {
        String displayName = Replacer.create()
            .replace(Placeholders.GENERIC_NAME, this::getName)
            .replace(Placeholders.GENERIC_LEVEL, () -> NumberUtil.format(level))
            .apply(Config.MOBS_NAME_FORMAT.get());

        entity.setCustomName(NightMessage.asLegacy(displayName));
        entity.setCustomNameVisible(this.nameVisible);

        EntityEquipment armor = entity.getEquipment();
        if (armor != null) {
            this.equipment.forEach(armor::setItem);
        }

        if (entity instanceof Ageable ageable) {
            ageable.setAdult();
        }
        if (entity instanceof PiglinAbstract piglin) {
            piglin.setImmuneToZombification(true);
        }
        else if (entity instanceof Hoglin hoglin) {
            hoglin.setImmuneToZombification(true);
        }
        else if (entity instanceof Zombie zombie) {
            switch (zombie) {
                case ZombieVillager zombieVillager -> zombieVillager.setConversionTime(-1);
                case PigZombie pigZombie -> pigZombie.setAngry(true);
                case Husk husk -> husk.setConversionTime(-1);
                default -> zombie.setConversionTime(-1);
            }
        }

        this.dressUp(entity);
    }

    public void applyAttributes(@NotNull LivingEntity entity, int level) {
        this.attributes.forEach((attribute, scale) -> {
            AttributeInstance instance = entity.getAttribute(attribute);
            if (instance == null) return;

            double scaled = scale.getInitial() + (scale.getPerLevel() * (level - 1));
            double value = Math.clamp(scaled, scale.getMinValue(), scale.getMaxValue());

            instance.setBaseValue(value);
        });
    }
}
