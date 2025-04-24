package su.nightexpress.dungeons.mob.variant.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.mob.variant.MobVariant;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.StringUtil;

import java.util.List;

public abstract class EnumMobVariant<E extends Enum<E>> extends MobVariant<E> {

    protected final Class<E> clazz;

    public EnumMobVariant(@NotNull String name, @NotNull Class<E> clazz) {
        super(name);
        this.clazz = clazz;
    }

    @Override
    @NotNull
    public List<E> values() {
        return Lists.newList(clazz.getEnumConstants());
    }

    @Override
    @Nullable
    public E parse(@NotNull String raw) {
        return StringUtil.getEnum(raw, this.clazz).orElse(null);
    }

    @Override
    @NotNull
    public String getRaw(@NotNull E value) {
        return value.name().toLowerCase();
    }
}
