package su.nightexpress.dungeons.mob.variant;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class MobVariant<T> {

    protected final String name;

    public MobVariant(@NotNull String name) {
        this.name = name.toLowerCase();
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    public abstract List<T> values();

    @Nullable
    public abstract T parse(@NotNull String raw);

    @Nullable
    public abstract T read(@NotNull LivingEntity entity);

//    @NotNull
//    public abstract String getLocalized(@NotNull T value);

    @NotNull
    public abstract String getRaw(@NotNull T value);

    public abstract boolean apply(@NotNull LivingEntity entity, @Nullable T value);

    @NotNull
    public List<String> rawValues() {
        return this.values().stream().map(this::getRaw).toList();
    }

//    @NotNull
//    public String getLocalized(@NotNull String raw) {
//        T value = this.parse(raw);
//        return value == null ? raw : this.getLocalized(value);
//    }

    @Nullable
    public String getRaw(@NotNull LivingEntity entity) {
        T value = this.read(entity);
        return value == null ? null : this.getRaw(value);
    }

    public boolean apply(@NotNull LivingEntity entity, @NotNull String raw) {
        T value = this.parse(raw);
        return value != null && this.apply(entity, value);
    }

    public boolean alreadyHas(@NotNull LivingEntity entity, @NotNull String raw) {
        return this.alreadyHas(entity, this.parse(raw));
    }

    public boolean alreadyHas(@NotNull LivingEntity entity, @Nullable T value) {
        return this.read(entity) == value;
    }
}
