package su.nightexpress.dungeons.mob.variant.impl;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.mob.variant.MobVariantId;

public class CreeperPowerMobVariant extends EnumMobVariant<CreeperPowerMobVariant.Type> {

    public CreeperPowerMobVariant() {
        super(MobVariantId.CREEPER_POWER, Type.class);
    }

    @Override
    @Nullable
    public CreeperPowerMobVariant.Type read(@NotNull LivingEntity entity) {
        return entity instanceof Creeper creeper ? (creeper.isPowered() ? Type.POWERED : Type.DEFAULT) : null;
    }

//    @Override
//    @NotNull
//    public String getLocalized(@NotNull CreeperPowerVariantHandler.Type value) {
//        return Lang.POWER_TYPE.getLocalized(value);
//    }

    @Override
    public boolean apply(@NotNull LivingEntity entity, @Nullable CreeperPowerMobVariant.Type value) {
        if (!(entity instanceof Creeper creeper)) return false;

        creeper.setPowered(value == Type.POWERED);
        return true;
    }

    public enum Type {
        DEFAULT, POWERED
    }
}
