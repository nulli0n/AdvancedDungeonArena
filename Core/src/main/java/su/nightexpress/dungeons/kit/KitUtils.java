package su.nightexpress.dungeons.kit;

import com.google.common.collect.Sets;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.config.Keys;
import su.nightexpress.dungeons.kit.impl.Kit;
import su.nightexpress.dungeons.util.DungeonUtils;
import su.nightexpress.nightcore.integration.currency.CurrencyId;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.PDCUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.text.NightMessage;

import java.util.HashSet;

import static su.nightexpress.nightcore.util.text.tag.Tags.LIGHT_CYAN;
import static su.nightexpress.nightcore.util.text.tag.Tags.WHITE;

public class KitUtils {

    public static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public static boolean isRentMode() {
        return !isPurchaseMode();
    }

    public static boolean isPurchaseMode() {
        return Config.KITS_PERMANENT_PURCHASES.get();
    }

    public static boolean isItemShareAllowed() {
        return !Config.KITS_PREVENT_ITEM_SHARE.get();
    }

    @NotNull
    public static ItemStack assignKit(@NotNull Kit kit, @NotNull ItemStack itemStack) {
        ItemUtil.editMeta(itemStack, meta -> {
            PDCUtil.set(meta, Keys.kitItem, kit.getId());
        });
        return itemStack;
    }

    public static boolean isKitItem(@NotNull ItemStack itemStack) {
        return PDCUtil.getString(itemStack, Keys.kitItem).isPresent();
    }

    public static void setKitContent(@NotNull Kit kit, @NotNull PlayerInventory inventory) {
        kit.setItems(inventory.getStorageContents());

        for (EquipmentSlot slot : KitUtils.ARMOR_SLOTS) {
            kit.setEquipment(slot, inventory.getItem(slot));
        }
    }

    public static void setWarriorKit(@NotNull Kit kit) {
        kit.setDescription(Lists.newList(

        ));
        kit.setIcon(new NightItem(Material.CHAINMAIL_CHESTPLATE));
        kit.setPotionEffects(new HashSet<>());

        ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET);
        ItemStack chestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);

        helmet.addUnsafeEnchantment(Enchantment.PROTECTION, 1);
        chestplate.addUnsafeEnchantment(Enchantment.PROTECTION, 1);
        leggings.addUnsafeEnchantment(Enchantment.PROTECTION, 1);
        boots.addUnsafeEnchantment(Enchantment.PROTECTION, 1);

        kit.setEquipment(EquipmentSlot.HEAD, helmet);
        kit.setEquipment(EquipmentSlot.CHEST, chestplate);
        kit.setEquipment(EquipmentSlot.LEGS, leggings);
        kit.setEquipment(EquipmentSlot.FEET, boots);

        ItemStack strengthPotion = new ItemStack(Material.POTION);
        ItemUtil.editMeta(strengthPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.STRENGTH, 60 * 20, 0), true);
            potionMeta.setColor(Color.fromRGB(170, 10, 40));
            potionMeta.setDisplayName(NightMessage.asLegacy(WHITE.enclose("Potion of Strength")));
        });

        ItemStack regenPotion = new ItemStack(Material.POTION);
        ItemUtil.editMeta(regenPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 30 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(230, 145, 195));
            potionMeta.setDisplayName(NightMessage.asLegacy(WHITE.enclose("Potion of Resistance")));
        });

        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        sword.addUnsafeEnchantment(Enchantment.SHARPNESS, 2);

        ItemStack[] items = new ItemStack[Kit.INVENTORY_SIZE];
        items[0] = sword;
        items[1] = new ItemStack(Material.COOKED_BEEF, 64);
        for (int slot = 18; slot < 27; slot++) {
            items[slot] = new ItemStack(regenPotion);
        }
        for (int slot = 27; slot < 36; slot++) {
            items[slot] = new ItemStack(strengthPotion);
        }
        kit.setItems(items);
    }

    public static void setTankKit(@NotNull Kit kit) {
        kit.setDescription(Lists.newList(

        ));
        kit.setIcon(new NightItem(Material.DIAMOND_CHESTPLATE));
        kit.setPotionEffects(new HashSet<>());

        kit.setAttribute(Attribute.MAX_HEALTH, AttributeModifier.Operation.ADD_NUMBER, 10D);
        kit.setAttribute(Attribute.MOVEMENT_SPEED, AttributeModifier.Operation.ADD_SCALAR, -0.35);
        kit.setAttribute(Attribute.KNOCKBACK_RESISTANCE, AttributeModifier.Operation.ADD_SCALAR, 0.3);

        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);

        for (Enchantment enchantment : new Enchantment[]{
            Enchantment.PROTECTION,
            Enchantment.BLAST_PROTECTION,
            Enchantment.PROJECTILE_PROTECTION,
            Enchantment.FIRE_PROTECTION,
            Enchantment.THORNS,
        }) {
            helmet.addUnsafeEnchantment(enchantment, 4);
            chestplate.addUnsafeEnchantment(enchantment, 4);
            leggings.addUnsafeEnchantment(enchantment, 4);
            boots.addUnsafeEnchantment(enchantment, 4);
        }

        kit.setEquipment(EquipmentSlot.HEAD, helmet);
        kit.setEquipment(EquipmentSlot.CHEST, chestplate);
        kit.setEquipment(EquipmentSlot.LEGS, leggings);
        kit.setEquipment(EquipmentSlot.FEET, boots);
        kit.setEquipment(EquipmentSlot.OFF_HAND, new ItemStack(Material.SHIELD));

        ItemStack healPotion = new ItemStack(Material.POTION);
        ItemUtil.editMeta(healPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 1), true);
            potionMeta.setColor(Color.fromRGB(240, 65, 100));
            potionMeta.setDisplayName(NightMessage.asLegacy(WHITE.enclose("Potion of Heal")));
        });

        ItemStack defensePotion = new ItemStack(Material.POTION);
        ItemUtil.editMeta(defensePotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.STRENGTH, 120 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(90, 80, 130));
            potionMeta.setDisplayName(NightMessage.asLegacy(WHITE.enclose("Potion of Resistance")));
        });

        ItemStack[] items = new ItemStack[Kit.INVENTORY_SIZE];
        items[0] = new ItemStack(Material.STONE_SWORD);
        items[1] = new ItemStack(Material.COOKED_BEEF, 64);
        for (int slot = 18; slot < 27; slot++) {
            items[slot] = new ItemStack(defensePotion);
        }
        for (int slot = 27; slot < 36; slot++) {
            items[slot] = new ItemStack(healPotion);
        }
        kit.setItems(items);
        kit.setCost(CurrencyId.VAULT, 500);
    }

    public static void setArcherKit(@NotNull Kit kit) {
        kit.setDescription(Lists.newList(

        ));
        kit.setIcon(new NightItem(Material.BOW));
        kit.setPotionEffects(new HashSet<>());

        kit.setAttribute(Attribute.MOVEMENT_SPEED, AttributeModifier.Operation.ADD_SCALAR, 0.3);
        kit.setAttribute(Attribute.ATTACK_KNOCKBACK, AttributeModifier.Operation.ADD_SCALAR, 0.2);

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        ItemUtil.editMeta(helmet, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(150, 235, 250));
        });
        ItemUtil.editMeta(chestplate, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(150, 235, 250));
        });
        ItemUtil.editMeta(leggings, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(150, 235, 250));
        });
        ItemUtil.editMeta(boots, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(150, 235, 250));
        });

        kit.setEquipment(EquipmentSlot.HEAD, helmet);
        kit.setEquipment(EquipmentSlot.CHEST, chestplate);
        kit.setEquipment(EquipmentSlot.LEGS, leggings);
        kit.setEquipment(EquipmentSlot.FEET, boots);

        ItemStack speedPotion = new ItemStack(Material.POTION);
        ItemUtil.editMeta(speedPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 60 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(150, 235, 250));
            potionMeta.setDisplayName(NightMessage.asLegacy(WHITE.enclose("Potion of Swiftness")));
        });

        ItemStack bow = new ItemStack(Material.BOW);
        bow.addUnsafeEnchantment(Enchantment.POWER, 3);
        bow.addUnsafeEnchantment(Enchantment.INFINITY, 1);
        bow.addUnsafeEnchantment(Enchantment.PUNCH, 1);

        ItemStack sword = new ItemStack(Material.STONE_SWORD);
        sword.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);

        ItemStack arrow = new ItemStack(Material.ARROW, 64);

        ItemStack[] items = new ItemStack[Kit.INVENTORY_SIZE];
        items[0] = bow;
        items[1] = sword;
        items[2] = new ItemStack(Material.COOKED_BEEF, 64);
        for (int slot = 18; slot < 27; slot++) {
            items[slot] = new ItemStack(arrow);
        }
        for (int slot = 27; slot < 36; slot++) {
            items[slot] = new ItemStack(speedPotion);
        }
        kit.setItems(items);

        kit.setCost(CurrencyId.VAULT, 1500);
    }

    public static void setAssasinKit(@NotNull Kit kit) {
        kit.setDescription(Lists.newList(

        ));
        kit.setIcon(new NightItem(Material.FEATHER));
        kit.setPotionEffects(Sets.newHashSet(
            new PotionEffect(PotionEffectType.INVISIBILITY, Kit.EFFECT_DURATION, 1)
        ));

        kit.setAttribute(Attribute.MOVEMENT_SPEED, AttributeModifier.Operation.ADD_SCALAR, 0.5);
        kit.setAttribute(Attribute.MAX_HEALTH, AttributeModifier.Operation.ADD_NUMBER, -5);
        kit.setAttribute(Attribute.ARMOR, AttributeModifier.Operation.ADD_SCALAR, -0.5);

        ItemStack helmet = new ItemStack(Material.OAK_LEAVES);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        ItemUtil.editMeta(chestplate, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(65, 140, 65));
        });
        ItemUtil.editMeta(leggings, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(65, 140, 65));
        });
        ItemUtil.editMeta(boots, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(65, 140, 65));
        });

        kit.setEquipment(EquipmentSlot.HEAD, helmet);
        kit.setEquipment(EquipmentSlot.CHEST, chestplate);
        kit.setEquipment(EquipmentSlot.LEGS, leggings);
        kit.setEquipment(EquipmentSlot.FEET, boots);

        ItemStack speedPotion = new ItemStack(Material.SPLASH_POTION, 4);
        ItemUtil.editMeta(speedPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 60 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(65, 140, 65));
            potionMeta.setDisplayName(NightMessage.asLegacy(WHITE.enclose("Splash Potion of Poison")));
        });

        ItemStack bow = new ItemStack(Material.CROSSBOW);
        bow.addUnsafeEnchantment(Enchantment.POWER, 3);

        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        sword.addUnsafeEnchantment(Enchantment.SHARPNESS, 5);

        ItemStack arrow = new ItemStack(Material.ARROW, 64);

        ItemStack[] items = new ItemStack[Kit.INVENTORY_SIZE];
        items[0] = sword;
        items[1] = bow;
        items[2] = new ItemStack(Material.COOKED_BEEF, 64);
        for (int slot = 18; slot < 21; slot++) {
            items[slot] = new ItemStack(arrow);
        }
        for (int slot = 27; slot < 30; slot++) {
            items[slot] = new ItemStack(speedPotion);
        }
        kit.setItems(items);

        kit.setCost(CurrencyId.VAULT, 3500);
    }

    public static void setSupportKit(@NotNull Kit kit) {
        kit.setDescription(Lists.newList(

        ));
        kit.setIcon(new NightItem(Material.POTION));
        kit.setPotionEffects(new HashSet<>());

        ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET);
        ItemStack chestplate = new ItemStack(Material.GOLDEN_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        ItemStack boots = new ItemStack(Material.GOLDEN_BOOTS);

        kit.setEquipment(EquipmentSlot.HEAD, helmet);
        kit.setEquipment(EquipmentSlot.CHEST, chestplate);
        kit.setEquipment(EquipmentSlot.LEGS, leggings);
        kit.setEquipment(EquipmentSlot.FEET, boots);

        ItemStack healPotion = new ItemStack(Material.SPLASH_POTION, 8);
        ItemUtil.editMeta(healPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 1), true);
            potionMeta.setColor(Color.fromRGB(240, 65, 100));
            potionMeta.setDisplayName(NightMessage.asLegacy(WHITE.enclose("Potion of Heal")));
        });

        ItemStack firePotion = new ItemStack(Material.SPLASH_POTION, 8);
        ItemUtil.editMeta(firePotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 90 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(250, 170, 60));
            potionMeta.setDisplayName(NightMessage.asLegacy(WHITE.enclose("Potion of Fire Resistance")));
        });

        ItemStack regenPotion = new ItemStack(Material.SPLASH_POTION, 8);
        ItemUtil.editMeta(regenPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 30 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(230, 145, 195));
            potionMeta.setDisplayName(NightMessage.asLegacy(WHITE.enclose("Potion of Regeneration")));
        });

        ItemStack speedPotion = new ItemStack(Material.SPLASH_POTION, 8);
        ItemUtil.editMeta(speedPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 90 * 20, 0), true);
            potionMeta.setColor(Color.fromRGB(150, 235, 250));
            potionMeta.setDisplayName(NightMessage.asLegacy(WHITE.enclose("Potion of Swiftness")));
        });

        ItemStack harmPotion = new ItemStack(Material.SPLASH_POTION, 8);
        ItemUtil.editMeta(harmPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HASTE, 60 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(250, 240, 130));
            potionMeta.setDisplayName(NightMessage.asLegacy(WHITE.enclose("Potion of Haste")));
        });

        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        sword.addUnsafeEnchantment(Enchantment.SHARPNESS, 1);
        sword.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

        ItemStack[] items = new ItemStack[Kit.INVENTORY_SIZE];
        items[0] = sword;
        items[1] = new ItemStack(Material.COOKED_BEEF, 64);
        for (int slot = 18; slot < 21; slot++) {
            items[slot] = new ItemStack(speedPotion);
        }
        for (int slot = 21; slot < 24; slot++) {
            items[slot] = new ItemStack(harmPotion);
        }
        for (int slot = 27; slot < 30; slot++) {
            items[slot] = new ItemStack(healPotion);
        }
        for (int slot = 30; slot < 33; slot++) {
            items[slot] = new ItemStack(regenPotion);
        }
        for (int slot = 33; slot < 36; slot++) {
            items[slot] = new ItemStack(firePotion);
        }
        kit.setItems(items);

        kit.setCost(CurrencyId.VAULT, 2500);
    }

    public static void setPriestKit(@NotNull Kit kit) {
        kit.setDescription(Lists.newList(

        ));
        kit.setIcon(new NightItem(Material.BLAZE_ROD));
        kit.setPotionEffects(Sets.newHashSet(
            new PotionEffect(PotionEffectType.REGENERATION, Kit.EFFECT_DURATION, 0)
        ));

        kit.setAttribute(Attribute.MAX_HEALTH, AttributeModifier.Operation.ADD_NUMBER, -4);
        kit.setAttribute(Attribute.MOVEMENT_SPEED, AttributeModifier.Operation.ADD_SCALAR, 0.15);

        ItemStack helmet = new ItemStack(Material.GOLDEN_HELMET);
        ItemStack chestplate = new ItemStack(Material.GOLDEN_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.GOLDEN_LEGGINGS);
        ItemStack boots = new ItemStack(Material.GOLDEN_BOOTS);

        kit.setEquipment(EquipmentSlot.HEAD, helmet);
        kit.setEquipment(EquipmentSlot.CHEST, chestplate);
        kit.setEquipment(EquipmentSlot.LEGS, leggings);
        kit.setEquipment(EquipmentSlot.FEET, boots);

        ItemStack holyPotion = new ItemStack(Material.SPLASH_POTION, 10);
        ItemUtil.editMeta(holyPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 2), true);
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 2), true);
            potionMeta.setColor(Color.fromRGB(230, 250, 255));
            potionMeta.setDisplayName(NightMessage.asLegacy(LIGHT_CYAN.enclose("Holy Potion of Smite")));
        });

        ItemStack regenPotion = new ItemStack(Material.SPLASH_POTION, 10);
        ItemUtil.editMeta(regenPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 20, 1), true);
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 30 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(230, 145, 195));
            potionMeta.setDisplayName(NightMessage.asLegacy(LIGHT_CYAN.enclose("Holy Potion of Regeneration")));
        });

        ItemStack healthPotion = new ItemStack(Material.SPLASH_POTION, 10);
        ItemUtil.editMeta(healthPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, 90 * 20, 1), true);
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 90 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(150, 235, 250));
            potionMeta.setDisplayName(NightMessage.asLegacy(LIGHT_CYAN.enclose("Holy Potion of Health")));
        });

        // BOMBS

        ItemStack regenBomb = new ItemStack(Material.LINGERING_POTION, 10);
        ItemUtil.editMeta(regenBomb, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 20, 1), true);
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 30 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(230, 145, 195));
            potionMeta.setDisplayName(NightMessage.asLegacy(LIGHT_CYAN.enclose("Holy Bomb of Regeneration")));
        });

        ItemStack healthBomb = new ItemStack(Material.LINGERING_POTION, 10);
        ItemUtil.editMeta(healthBomb, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, 90 * 20, 1), true);
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 90 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(150, 235, 250));
            potionMeta.setDisplayName(NightMessage.asLegacy(LIGHT_CYAN.enclose("Holy Bomb of Health")));
        });

        // OTHER

        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        sword.addUnsafeEnchantment(Enchantment.SMITE, 5);
        sword.addUnsafeEnchantment(Enchantment.BANE_OF_ARTHROPODS, 5);

        ItemStack[] items = new ItemStack[Kit.INVENTORY_SIZE];
        items[0] = sword;
        items[1] = new ItemStack(Material.COOKED_BEEF, 64);
        for (int slot = 18; slot < 21; slot++) {
            items[slot] = new ItemStack(healthPotion);
        }
        for (int slot = 21; slot < 24; slot++) {
            items[slot] = new ItemStack(holyPotion);
        }
        for (int slot = 24; slot < 27; slot++) {
            items[slot] = new ItemStack(regenPotion);
        }
        for (int slot = 27; slot < 30; slot++) {
            items[slot] = new ItemStack(healthBomb);
        }
        for (int slot = 33; slot < 36; slot++) {
            items[slot] = new ItemStack(regenBomb);
        }
        kit.setItems(items);

        kit.setCost(CurrencyId.VAULT, 4500);
    }

    public static void setPyroKit(@NotNull Kit kit) {
        kit.setDescription(Lists.newList(

        ));
        kit.setIcon(new NightItem(Material.FIRE_CHARGE));
        kit.setPotionEffects(Sets.newHashSet(
            new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Kit.EFFECT_DURATION, 1)
        ));

        kit.setAttribute(Attribute.ATTACK_SPEED, AttributeModifier.Operation.ADD_SCALAR, -0.15);

        ItemStack helmet = new ItemStack(Material.ORANGE_STAINED_GLASS);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        ItemUtil.editMeta(chestplate, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(240, 155, 75));
        });
        ItemUtil.editMeta(leggings, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(240, 155, 75));
        });
        ItemUtil.editMeta(boots, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(240, 155, 75));
        });

        kit.setEquipment(EquipmentSlot.HEAD, helmet);
        kit.setEquipment(EquipmentSlot.CHEST, chestplate);
        kit.setEquipment(EquipmentSlot.LEGS, leggings);
        kit.setEquipment(EquipmentSlot.FEET, boots);

        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        sword.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 5);

        ItemStack bow = new ItemStack(Material.BOW);
        bow.addUnsafeEnchantment(Enchantment.FLAME, 5);

        ItemStack[] items = new ItemStack[Kit.INVENTORY_SIZE];
        items[0] = sword;
        items[1] = bow;
        items[2] = new ItemStack(Material.COOKED_BEEF, 64);
        items[8] = new ItemStack(Material.BLAZE_SPAWN_EGG, 16);
        for (int slot = 18; slot < 21; slot++) {
            items[slot] = new ItemStack(new ItemStack(Material.ARROW, 64));
        }
        for (int slot = 21; slot < 24; slot++) {
            items[slot] = new ItemStack(new ItemStack(Material.FIRE_CHARGE, 64));
        }
        kit.setItems(items);

        kit.setCost(CurrencyId.VAULT, 5000);
    }

    public static void setBomberKit(@NotNull Kit kit) {
        kit.setDescription(Lists.newList(

        ));
        kit.setIcon(new NightItem(Material.TNT));
        kit.setPotionEffects(new HashSet<>());

        kit.setAttribute(Attribute.ARMOR, AttributeModifier.Operation.ADD_SCALAR, 0.3);
        kit.setAttribute(Attribute.MOVEMENT_SPEED, AttributeModifier.Operation.ADD_SCALAR, 0.1);

        ItemStack helmet = new ItemStack(Material.TNT);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        chestplate.addUnsafeEnchantment(Enchantment.BLAST_PROTECTION, 5);
        leggings.addUnsafeEnchantment(Enchantment.BLAST_PROTECTION, 5);
        boots.addUnsafeEnchantment(Enchantment.BLAST_PROTECTION, 5);

        ItemUtil.editMeta(chestplate, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(210, 80, 80));
        });
        ItemUtil.editMeta(leggings, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(210, 80, 80));
        });
        ItemUtil.editMeta(boots, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(210, 80, 80));
        });

        kit.setEquipment(EquipmentSlot.HEAD, helmet);
        kit.setEquipment(EquipmentSlot.CHEST, chestplate);
        kit.setEquipment(EquipmentSlot.LEGS, leggings);
        kit.setEquipment(EquipmentSlot.FEET, boots);

        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);

        ItemStack[] items = new ItemStack[Kit.INVENTORY_SIZE];
        items[0] = sword;
        items[1] = new ItemStack(Material.COOKED_BEEF, 64);
        items[8] = new ItemStack(Material.CREEPER_SPAWN_EGG, 16);
        for (int slot = 18; slot < 21; slot++) {
            items[slot] = new ItemStack(new ItemStack(Material.TNT, 64));
        }
        kit.setItems(items);

        kit.setCost(CurrencyId.VAULT, 7500);
    }
}
