package su.nightexpress.dungeons.mob.variant.impl;

import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.mob.variant.MobVariantId;

public class HorseStyleMobVariant extends EnumMobVariant<Horse.Style> {

    public HorseStyleMobVariant() {
        super(MobVariantId.HORSE_STYLE, Horse.Style.class);
    }

    @Override
    @Nullable
    public Horse.Style read(@NotNull LivingEntity entity) {
        if (!(entity instanceof Horse horse)) return null;

        return horse.getStyle();
    }

//    @Override
//    @NotNull
//    public String getLocalized(Horse.@NotNull Style value) {
//        return Lang.HORSE_STYLE.getLocalized(value);
//    }

    @Override
    public boolean apply(@NotNull LivingEntity entity, @Nullable Horse.Style value) {
        if (value == null) return false;
        if (!(entity instanceof Horse horse)) return false;

        horse.setStyle(value);
        return true;
    }
}
