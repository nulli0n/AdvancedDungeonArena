package su.nightexpress.dungeons.mob.variant.impl;

import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.mob.variant.MobVariantId;

public class HorseColorMobVariant extends EnumMobVariant<Horse.Color> {

    public HorseColorMobVariant() {
        super(MobVariantId.HORSE_COLOR, Horse.Color.class);
    }

    @Override
    @Nullable
    public Horse.Color read(@NotNull LivingEntity entity) {
        if (!(entity instanceof Horse horse)) return null;

        return horse.getColor();
    }

//    @Override
//    @NotNull
//    public String getLocalized(Horse.@NotNull Color value) {
//        return Lang.HORSE_COLOR.getLocalized(value);
//    }

    @Override
    public boolean apply(@NotNull LivingEntity entity, @Nullable Horse.Color value) {
        if (value == null) return false;
        if (!(entity instanceof Horse horse)) return false;

        horse.setColor(value);
        return true;
    }
}
