package su.nightexpress.dungeons.mob.variant.impl;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.mob.variant.MobVariantId;

public class LlamaColorMobVariant extends EnumMobVariant<Llama.Color> {

    public LlamaColorMobVariant() {
        super(MobVariantId.LLAMA_COLOR, Llama.Color.class);
    }

    @Override
    @Nullable
    public Llama.Color read(@NotNull LivingEntity entity) {
        if (!(entity instanceof Llama llama)) return null;

        return llama.getColor();
    }

//    @Override
//    @NotNull
//    public String getLocalized(Llama.@NotNull Color value) {
//        return Lang.LLAMA_COLOR.getLocalized(value);
//    }

    @Override
    public boolean apply(@NotNull LivingEntity entity, @Nullable Llama.Color value) {
        if (value == null) return false;
        if (!(entity instanceof Llama llama)) return false;

        llama.setColor(value);
        return true;
    }
}
