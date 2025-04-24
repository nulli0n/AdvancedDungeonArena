package su.nightexpress.dungeons.mob.variant.impl;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.mob.variant.MobVariantId;

public class SheepShearMobVariant extends EnumMobVariant<SheepShearMobVariant.Type> {

    public SheepShearMobVariant() {
        super(MobVariantId.SHEEP_SHEAR, Type.class);
    }

    @Override
    @Nullable
    public SheepShearMobVariant.Type read(@NotNull LivingEntity entity) {
        return entity instanceof Sheep sheep ? (sheep.isSheared() ? Type.SHEARED : Type.NORMAL) : null;
    }

//    @Override
//    @NotNull
//    public String getLocalized(@NotNull Type value) {
//        return Lang.SHEEP_SHEAR.getLocalized(value);
//    }

    @Override
    public boolean apply(@NotNull LivingEntity entity, @Nullable Type value) {
        if (!(entity instanceof Sheep sheep)) return false;

        sheep.setSheared(value == Type.SHEARED);
        return true;
    }

    public enum Type {
        NORMAL, SHEARED
    }
}
