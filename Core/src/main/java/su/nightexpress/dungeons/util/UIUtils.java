package su.nightexpress.dungeons.util;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.nightcore.bridge.currency.Currency;
import su.nightexpress.nightcore.integration.currency.EconomyBridge;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.placeholder.Replacer;

import static su.nightexpress.dungeons.Placeholders.*;

public class UIUtils {

//    private static ConfirmMenu confirmMenu;
//
//    public static void load(@NotNull DungeonPlugin plugin) {
//        confirmMenu = new ConfirmMenu(plugin);
//    }
//
//    public static void clear() {
//        confirmMenu.clear();
//        confirmMenu = null;
//    }
//
//    public static void openConfirmation(@NotNull Player player, @NotNull Confirmation confirmation) {
//        confirmMenu.open(player, confirmation);
//    }

    @NotNull
    public static String formatCostEntry(@NotNull String currencyId, double amount) {
        Currency currency = EconomyBridge.getCurrency(currencyId);
        if (currency == null) return currencyId;

        return currency.format(amount);
    }

    @NotNull
    public static String formatPotionEffectEntry(@NotNull PotionEffect effect) {
        return Replacer.create()
            .replace(GENERIC_NAME, LangAssets.get(effect.getType()))
            .replace(GENERIC_AMOUNT, NumberUtil.toRoman(effect.getAmplifier() + 1))
            .apply(Lang.UI_POTION_EFFECT_ENTRY.text());
    }

    @NotNull
    public static String formatAttributeEntry(@NotNull Attribute attribute, @NotNull AttributeModifier modifier) {
        double amount = modifier.getAmount();
        boolean scalar = modifier.getOperation() == AttributeModifier.Operation.ADD_SCALAR;
        boolean negative = amount < 0D;

        TextLocale valueString;
        if (scalar) {
            valueString = negative ? Lang.UI_ATTRIBUTE_NEGATIVE_SCALAR : Lang.UI_ATTRIBUTE_POSITIVE_SCALAR;
        }
        else {
            valueString = negative ? Lang.UI_ATTRIBUTE_NEGATIVE_PLAIN : Lang.UI_ATTRIBUTE_POSITIVE_PLAIN;
        }

        return Replacer.create()
            .replace(GENERIC_NAME, () -> Lang.ATTRIBUTE.getLocalized(attribute))
            .replace(GENERIC_AMOUNT, () -> valueString.text().replace(GENERIC_VALUE, NumberUtil.format(scalar ? (amount * 100D) : amount)))
            .apply(Lang.UI_ATTRIBUTE_ENTRY.text());
    }
}
