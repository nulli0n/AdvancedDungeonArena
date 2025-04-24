package su.nightexpress.dungeons.mob.variant.impl;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.mob.variant.MobVariantId;

public class AgeMobVariant extends EnumMobVariant<AgeMobVariant.Type> {

    public AgeMobVariant() {
        super(MobVariantId.AGE, Type.class);
    }

    @Override
    @Nullable
    public AgeMobVariant.Type read(@NotNull LivingEntity entity) {
        if (entity instanceof Ageable ageable) return ageable.isAdult() ? Type.ADULT : Type.BABY;

        return null;
    }

//    @Override
//    @NotNull
//    public String getLocalized(@NotNull Type value) {
//        return Lang.AGE_TYPE.getLocalized(value);
//    }

    @Override
    public boolean apply(@NotNull LivingEntity entity, @Nullable Type value) {
        if (!(entity instanceof Ageable ageable)) return false;

        if (value == Type.ADULT) ageable.setAdult();
        else ageable.setBaby();

        return true;
    }

    public enum Type {
        ADULT, BABY
    }
}
